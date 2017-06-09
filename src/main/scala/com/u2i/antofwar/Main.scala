package com.u2i.antofwar

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws._
import akka.stream._
import akka.stream.scaladsl.{BidiFlow, Flow, Keep, Source}
import akka.{Done, NotUsed}
import com.u2i.antofwar.model.Ant
import com.u2i.antofwar.phoenix.PhoenixMessage
import com.u2i.antofwar.strategies.{CompletelyRandom, GoToNotOwn}

import scala.concurrent.Future
import scala.util.Try

object Main extends App {
  var playerId: Int = 0
  val strategyName = args(0)
  println(s"PLAYER ID: $playerId")

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val formats = org.json4s.DefaultFormats

  import org.json4s.JsonDSL._
  import org.json4s._
  import org.json4s.native.JsonMethods._
  import system.dispatcher

  val strategy = strategyName match {
    case "random" => new CompletelyRandom(40, 40)
    case "notOwn" => new GoToNotOwn(40, 40)
    case _ => throw new IllegalArgumentException(s"Wrong strategy name: $strategyName")
  }

  val unpackMessage = Flow[Message].collect { case TextMessage.Strict(text) => text }
  val messageCodec: BidiFlow[Message, String, String, Message, NotUsed] = BidiFlow.fromFlows(
    unpackMessage,
    Flow[String].map(TextMessage.Strict))
  val jsonCodec = BidiFlow.fromFunctions[String, JValue, JValue, String](
    parse(_),
    jValue => compact(render(jValue)))
  val phoenixMessageCodec = BidiFlow.fromFunctions[JValue, Try[PhoenixMessage], PhoenixMessage, JValue](
    jValue => Try(PhoenixMessage(
      (jValue \ "topic").extract[String],
      (jValue \ "event").extract[String],
      (jValue \ "ref").extractOpt[String],
      jValue \ "payload"
    )),
    msg => ("topic" -> msg.topic) ~ ("event" -> msg.event) ~ ("ref" -> msg.ref.orNull) ~ ("payload" -> msg.payload)
  )


  val topic = "observer:lobby"

  val helloSource: Source[PhoenixMessage, NotUsed] =
    Source.single(
      PhoenixMessage(topic = topic, event = "phx_join", payload = JObject(), ref = None))

  def log[T]: Flow[T, T, NotUsed] = Flow[T].map { x =>
    println(x)
    x
  }

  val handlePhoenixMessage: Flow[Try[PhoenixMessage], PhoenixMessage, NotUsed] =
    Flow[Try[PhoenixMessage]]
      .mapConcat(_.toOption.toList)
      .mapConcat {
        case PhoenixMessage(`topic`, "hello_message", _, payload) =>
          playerId = (payload \ "body" \ "player_id").extract[Int]
          println(s"Player id set to: $playerId")
          List()
        case PhoenixMessage(`topic`, "board_state", _, payload) =>
          val game = payload \ "game"
          (game \ "next_player").extract[Int] match {
            case x if x == playerId =>
              val ants = (payload \ "ants").extract[Seq[Ant]]
              val board = (payload \ "board").extract[Seq[Int]]
              val myAnts = ants.filter(_.player == playerId)
              val moveActionJsons =
                strategy
                  .moves(playerId, board, myAnts)
                  .map { move =>
                    ("cmd" -> "move") ~ ("id" -> move.id) ~ ("to" -> Seq(move.to._1, move.to._2))
                  }
              val spawnActionJson = ("cmd" -> "spawn") ~ ("player_id" -> playerId)
              val actionJsons = moveActionJsons :+ spawnActionJson
              List(PhoenixMessage(
                topic = topic,
                event = "user_command",
                ref = None,
                payload = actionJsons
              ))
            case _ =>
              List()
          }
        case PhoenixMessage(`topic`, "phx_reply", _, _) => List()
        case _ => List()
      }

  val codec: BidiFlow[Message, Try[PhoenixMessage], PhoenixMessage, Message, NotUsed] =
    messageCodec.atop(jsonCodec).atop(phoenixMessageCodec)

  val flow: Flow[Message, Message, Future[Done]] =
    codec
      .join(handlePhoenixMessage.prepend(helloSource))
      .watchTermination()(Keep.right)

  // upgradeResponse is a Future[WebSocketUpgradeResponse] that
  // completes or fails when the connection succeeds or fails
  // and closed is a Future[Done] representing the stream completion from above
  val (upgradeResponse, terminationFuture) =
  Http().singleWebSocketRequest(WebSocketRequest("ws://localhost:4000/socket/websocket"), flow)
  //  Http().singleWebSocketRequest(WebSocketRequest("ws://10.94.2.207:4000/socket/websocket"), flow)

  val connected = upgradeResponse.map { upgrade =>
    // just like a regular http request we can access response status which is available via upgrade.response.status
    // status code 101 (Switching Protocols) indicates that server support WebSockets
    if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
      Done
    } else {
      throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
    }
  }

  // in a real application you would not side effect here
  // and handle errors more carefully
  connected.onComplete(println)
  //  promise.success(None)

  terminationFuture.onComplete { triedDone =>
    println(s"Stream terminated with: $triedDone")
    println("Shutting down...")
    system.terminate()
  }
}

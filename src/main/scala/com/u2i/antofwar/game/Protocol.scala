package com.u2i.antofwar.game

import akka.http.scaladsl.model.ws.Message
import akka.stream.scaladsl.{Flow, Keep, Source}
import akka.{Done, NotUsed}
import com.typesafe.scalalogging.Logger
import com.u2i.antofwar.Strategy
import com.u2i.antofwar.model.{Ant, Board, BoardSize}
import com.u2i.antofwar.phoenix.{Codecs, PhoenixMessage}
import org.json4s.JsonAST.{JObject, JValue}
import org.json4s.JsonDSL._

import scala.concurrent.Future

class Protocol(strategy: Strategy, boardSize: BoardSize) {
  private implicit val formats = org.json4s.DefaultFormats
  private val topic = "observer:lobby"
  private val logger = Logger[Protocol]

  private lazy val joinMessageSource: Source[PhoenixMessage, NotUsed] =
    Source.single(
      PhoenixMessage(topic = topic, event = Events.phxJoin, payload = JObject(), ref = None))

  private var playerId: Int = 0

  private def createActions(payload: JValue): List[PhoenixMessage] = {
    val foodByPlayer = (payload \ "game" \ "food").extract[Map[String, Int]].map {
      case (numString, food) => (numString.toInt, food)
    }
    val ants = (payload \ "ants").extract[Seq[Ant]]
    val boardSeq = (payload \ "board").extract[IndexedSeq[Int]]
    val board = Board(boardSeq, boardSize)
    val boardState = BoardState(playerId, board, ants, foodByPlayer)
    val moveActionJsons =
      strategy
        .moves(boardState)
        .map { move =>
          ("cmd" -> "move") ~ ("id" -> move.id) ~ ("to" -> move.to.toSeq)
        }
    val spawnActionJsons =
      if (strategy.shouldSpawn(boardState)) Some(("cmd" -> "spawn") ~ ("player_id" -> playerId))
      else None
    val actionJsons = moveActionJsons ++ spawnActionJsons
    List(PhoenixMessage(
      topic = topic,
      event = "user_command",
      ref = None,
      payload = actionJsons
    ))
  }

  private lazy val handlePhoenixMessage: Flow[PhoenixMessage, PhoenixMessage, NotUsed] =
    Flow[PhoenixMessage]
      .mapConcat {
        case PhoenixMessage(`topic`, Events.helloMessage, _, payload) =>
          playerId = (payload \ "body" \ "player_id").extract[Int]
          logger.info(s"Player id set to: $playerId")
          List()
        case PhoenixMessage(`topic`, Events.boardState, _, payload) =>
          val game = payload \ "game"
          (game \ "next_player").extract[Int] match {
            case x if x == `playerId` =>
              createActions(payload)
            case _ =>
              List()
          }
        case _ => List()
      }

  lazy val flow: Flow[Message, Message, Future[Done]] =
    Codecs.phoenixMessageCodec
      .join(handlePhoenixMessage.prepend(joinMessageSource))
      .watchTermination()(Keep.right)
}

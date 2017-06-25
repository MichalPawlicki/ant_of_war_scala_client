package com.u2i.antofwar.game

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{Message, WebSocketRequest}
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import com.u2i.antofwar.Strategy

import scala.concurrent.Future

object Game {
  def play(serverUrl: String, strategy: Strategy)
          (implicit system: ActorSystem, materializer: Materializer): Future[Done] = {
    import system.dispatcher

    val gameProtocol = new Protocol(strategy)
    val flow: Flow[Message, Message, Future[Done]] = gameProtocol.flow

    // upgradeResponse is a Future[WebSocketUpgradeResponse] that
    // completes or fails when the connection succeeds or fails
    // and closed is a Future[Done] representing the stream completion from above
    val (upgradeResponse, terminationFuture) = Http().singleWebSocketRequest(WebSocketRequest(serverUrl), flow)

    val connected = upgradeResponse.map { upgrade =>
      // just like a regular http request we can access response status which is available via upgrade.response.status
      // status code 101 (Switching Protocols) indicates that server support WebSockets
      if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
        Done
      } else {
        throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
      }
    }

    connected.onComplete(println)

    connected.flatMap(_ => terminationFuture)
  }
}

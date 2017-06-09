package com.u2i.antofwar

import akka.actor.ActorSystem
import akka.stream._
import com.u2i.antofwar.strategies.{CompletelyRandom, GoToNotOwn}

object Main extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  val strategy = args.toSeq match {
    case "random" :: _ => new CompletelyRandom(40, 40)
    case "notOwn" :: _ => new GoToNotOwn(40, 40)
    case _ => new GoToNotOwn(40, 40)
  }

  val gameFinishedFuture = Game.play("ws://localhost:4001/socket/websocket", strategy)

  gameFinishedFuture.onComplete { triedDone =>
    println(s"Stream terminated with: $triedDone")
    println("Shutting down...")
    system.terminate()
  }
}

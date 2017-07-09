package com.u2i.antofwar

import akka.actor.ActorSystem
import akka.stream._
import com.typesafe.config.ConfigFactory
import com.u2i.antofwar.game.{Game, Settings}
import com.u2i.antofwar.strategies.{CompletelyRandom, GoToNotOwn}

object Main extends App {
  val config = ConfigFactory.load()
  val settings = new Settings(config)
  val strategy: Strategy = settings.strategyName match {
    case "random" => new CompletelyRandom(40, 40)
    case "notOwn" => new GoToNotOwn(40, 40)
    case _ => new GoToNotOwn(40, 40)
  }

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  val gameFinishedFuture = Game.play(settings.serverUrl, strategy)

  gameFinishedFuture.onComplete { triedDone =>
    println(s"Stream terminated with: $triedDone")
    println("Shutting down...")
    system.terminate()
  }
}

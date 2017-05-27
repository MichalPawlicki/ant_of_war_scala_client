package com.u2i.antofwar.strategies

import com.u2i.antofwar.Strategy
import com.u2i.antofwar.model.{Ant, Move}

import scala.util.Random

class CompletelyRandom(playerId: Int, boardWidth: Int, boardHeight: Int) extends Strategy {
  override def moves(board: Seq[Int], yourAnts: Seq[Ant]): Seq[Move] = yourAnts.map { ant =>
    val newX = normalize(ant.x + Random.nextInt(3) - 1, 0, boardWidth - 1)
    val newY = normalize(ant.y + Random.nextInt(3) - 1, 0, boardHeight - 1)
    Move(ant.id, (newX, newY))
  }

  private def normalize(num: Int, min: Int, max: Int): Int = Math.min(max, Math.max(min, num))
}

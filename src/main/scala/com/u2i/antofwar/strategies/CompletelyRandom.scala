package com.u2i.antofwar.strategies

import com.u2i.antofwar.Strategy
import com.u2i.antofwar.game.BoardState
import com.u2i.antofwar.model.{Move, Point}

import scala.util.Random

class CompletelyRandom extends Strategy with Spawning {
  override def moves(boardState: BoardState): Seq[Move] = {
    val (width, height) = (boardState.width, boardState.height)

    boardState.myAnts.map { ant =>
      val newX = normalize(ant.x + Random.nextInt(3) - 1, 0, width - 1)
      val newY = normalize(ant.y + Random.nextInt(3) - 1, 0, height - 1)
      Move(ant.id, Point(newX, newY))
    }
  }

  private def normalize(num: Int, min: Int, max: Int): Int = Math.min(max, Math.max(min, num))
}

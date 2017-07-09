package com.u2i.antofwar.strategies

import com.u2i.antofwar.Strategy
import com.u2i.antofwar.game.BoardState
import com.u2i.antofwar.model.{Move, Point}

import scala.util.Random

class GoToNotOwn extends Strategy with Spawning {
  override def moves(boardState: BoardState): Seq[Move] = {
    val board = boardState.board
    val (width, height) = (boardState.width, boardState.height)

    boardState.myAnts.map { ant =>
      val possibleGoals = for {
        x <- Random.shuffle(normalize(ant.x - 1, width) to normalize(ant.x + 1, width))
        y <- Random.shuffle(normalize(ant.y - 1, height) to normalize(ant.y + 1, height))
        if board(x, y) != boardState.myPlayerId
      } yield Point(x, y)

      val finalGoal = possibleGoals.headOption.getOrElse {
        val randomX = normalize(ant.x + Random.nextInt(3) - 1, width)
        val randomY = normalize(ant.y + Random.nextInt(3) - 1, height)
        Point(randomX, randomY)
      }

      Move(ant.id, finalGoal)
    }
  }

  private def normalize(num: Int, max: Int): Int = Math.min(max - 1, Math.max(0, num))
}

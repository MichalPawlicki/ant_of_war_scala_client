package com.u2i.antofwar.strategies

import com.u2i.antofwar.Strategy
import com.u2i.antofwar.model.{Ant, Move}

import scala.util.Random

class GoToNotOwn(playerId: Int, boardWidth: Int, boardHeight: Int) extends Strategy {
  override def moves(board: Seq[Int], yourAnts: Seq[Ant]): Seq[Move] = {
    val betterBoard = board.grouped(boardHeight).map(_.toIndexedSeq).toIndexedSeq

    yourAnts.map { ant =>
      val possibleGoals = for {
        x <- Random.shuffle(normalizeX(ant.x - 1) to normalizeX(ant.x + 1))
        y <- Random.shuffle(normalizeY(ant.y - 1) to normalizeY(ant.y + 1))
        if betterBoard(x)(y) != playerId
      } yield (x, y)

      val finalGoal = possibleGoals.headOption.getOrElse {
        val randomX = normalizeX(ant.x + Random.nextInt(3) - 1)
        val randomY = normalizeY(ant.y + Random.nextInt(3) - 1)
        (randomX, randomY)
      }

      Move(ant.id, finalGoal)
    }
  }

  private def normalizeX(num: Int): Int = Math.min(boardWidth - 1, Math.max(0, num))

  private def normalizeY(num: Int): Int = Math.min(boardHeight - 1, Math.max(0, num))
}

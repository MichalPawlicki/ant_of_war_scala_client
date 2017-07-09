package com.u2i.antofwar.strategies

import com.u2i.antofwar.Strategy
import com.u2i.antofwar.game.BoardState
import com.u2i.antofwar.model.Move

import scala.util.Random

class GoToNotOwn(boardWidth: Int, boardHeight: Int) extends Strategy with Spawning {
  override def moves(boardState: BoardState): Seq[Move] = {
    val betterBoard = boardState.board.grouped(boardHeight).map(_.toIndexedSeq).toIndexedSeq

    boardState.myAnts.map { ant =>
      val possibleGoals = for {
        x <- Random.shuffle(normalizeX(ant.x - 1) to normalizeX(ant.x + 1))
        y <- Random.shuffle(normalizeY(ant.y - 1) to normalizeY(ant.y + 1))
        if betterBoard(x)(y) != boardState.myPlayerId
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

  override def shouldSpawn(boardState: BoardState): Boolean = shouldSpawn(boardState, boardWidth, boardHeight)
}

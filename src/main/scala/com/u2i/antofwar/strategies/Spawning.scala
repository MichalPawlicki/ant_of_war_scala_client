package com.u2i.antofwar.strategies

import com.u2i.antofwar.game.BoardState
import com.u2i.antofwar.model.Point

trait Spawning {
  def shouldSpawn(boardState: BoardState, boardWidth: Int, boardHeight: Int): Boolean = {
    val spawnPoint = findSpawnPoint(boardState.myPlayerId, boardWidth, boardHeight)

    boardState.myAnts
      .find(_.position == spawnPoint)
      .forall(_ => false)
  }

  private def findSpawnPoint(myPlayerId: Int, boardWidth: Int, boardHeight: Int): Point = myPlayerId match {
    case 1 => Point(0, 0)
    case 2 => Point(boardWidth - 1, 0)
    case 3 => Point(0, boardHeight - 1)
    case 4 => Point(boardWidth - 1, boardHeight - 1)
    case _ => Point(0, 0)
  }
}

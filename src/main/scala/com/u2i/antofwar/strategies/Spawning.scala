package com.u2i.antofwar.strategies

import com.u2i.antofwar.game.BoardState
import com.u2i.antofwar.model.Point

trait Spawning {
  def shouldSpawn(boardState: BoardState): Boolean = {
    val spawnPoint = findSpawnPoint(boardState.myPlayerId, boardState.width, boardState.height)

    boardState.myAnts.find(_.position == spawnPoint).forall(_ => false)
  }

  private def findSpawnPoint(myPlayerId: Int, width: Int, height: Int): Point = myPlayerId match {
    case 1 => Point(0, 0)
    case 2 => Point(width - 1, 0)
    case 3 => Point(0, height - 1)
    case 4 => Point(width - 1, height - 1)
    case _ => Point(0, 0)
  }
}

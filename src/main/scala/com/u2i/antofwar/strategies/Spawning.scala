package com.u2i.antofwar.strategies

import com.u2i.antofwar.game.BoardState
import com.u2i.antofwar.model.{Point, Spawn}
import com.u2i.antofwar.model.Spawn.ShouldSpawn

trait Spawning {
  def shouldSpawn(boardState: BoardState, boardWidth: Int, boardHeight: Int): ShouldSpawn = {
    val spawnPoint = findSpawnPoint(boardState.myPlayerId, boardWidth, boardHeight)

    boardState.myAnts
      .find(ant => ant.x == spawnPoint.x && ant.y == spawnPoint.y)
      .map(_ => Spawn.no)
      .getOrElse(Spawn.yes)
  }

  private def findSpawnPoint(myPlayerId: Int, boardWidth: Int, boardHeight: Int): Point = myPlayerId match {
    case 1 => Point(0, 0)
    case 2 => Point(boardWidth - 1, 0)
    case 3 => Point(0, boardHeight - 1)
    case 4 => Point(boardWidth - 1, boardHeight - 1)
    case _ => Point(0, 0)
  }
}

package com.u2i.antofwar

import com.u2i.antofwar.game.BoardState
import com.u2i.antofwar.model.Move

trait Strategy {
  def moves(boardState: BoardState): Seq[Move]

  def shouldSpawn(boardState: BoardState): Boolean
}

package com.u2i.antofwar

import com.u2i.antofwar.model.{Ant, Move}

trait Strategy {
  def moves(myPlayerId: Int, board: Seq[Int], yourAnts: Seq[Ant]): Seq[Move]
}

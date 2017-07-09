package com.u2i.antofwar.game

import com.u2i.antofwar.model.{Ant, Board}

case class BoardState(myPlayerId: Int, board: Board, allAnts: Seq[Ant], foodByPlayerId: Map[Int, Int]) {
  lazy val myAnts: Seq[Ant] = allAnts.filter(_.player == myPlayerId)
  lazy val myFood: Int = foodByPlayerId(myPlayerId)
  lazy val width: Int = board.size.width
  lazy val height: Int = board.size.height
}

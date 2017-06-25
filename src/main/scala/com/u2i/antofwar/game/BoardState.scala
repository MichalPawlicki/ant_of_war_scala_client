package com.u2i.antofwar.game

import com.u2i.antofwar.model.Ant

case class BoardState(myPlayerId: Int, board: Seq[Int], allAnts: Seq[Ant], foodByPlayerId: Map[Int, Int]) {
  lazy val myAnts: Seq[Ant] = allAnts.filter(_.player == myPlayerId)

//  lazy val board2D =
}

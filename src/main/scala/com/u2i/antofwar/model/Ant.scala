package com.u2i.antofwar.model

case class Ant(x: Int, y: Int, player: Int, id: Int) {
  lazy val position: Point = Point(x, y)
}

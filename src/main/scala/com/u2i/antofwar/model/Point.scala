package com.u2i.antofwar.model

case class Point(x: Int, y: Int) {
  lazy val toSeq: Seq[Int] = Seq(x, y)
}

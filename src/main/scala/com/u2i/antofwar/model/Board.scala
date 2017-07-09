package com.u2i.antofwar.model

case class Board(boardSeq: IndexedSeq[Int], size: BoardSize) {
  private lazy val width = size.width

  def apply(x: Int, y: Int): Int = boardSeq(x * width + y)

  def apply(point: Point): Int = apply(point.x, point.y)
}

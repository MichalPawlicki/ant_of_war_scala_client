package com.u2i.antofwar.model

object Spawn {
  type ShouldSpawn = Option[Unit]
  val yes: ShouldSpawn = Some()
  val no: ShouldSpawn = None
}

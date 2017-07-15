package com.u2i.antofwar.game

import com.typesafe.config.Config

class Settings(config: Config) {
  val serverUrl: String = config.getString("server-url")
  def strategyName: String = config.getString("strategy-name")
}

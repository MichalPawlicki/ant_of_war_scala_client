package com.u2i.antofwar.game

import com.typesafe.config.Config

class Settings(config: Config) {
  val serverUrl: String = config.getString("server-url")
  val strategyName: String = config.getString("strategy-name")
}

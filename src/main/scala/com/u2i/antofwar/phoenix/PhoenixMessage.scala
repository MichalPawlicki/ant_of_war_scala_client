package com.u2i.antofwar.phoenix

import org.json4s.JsonAST.JValue

case class PhoenixMessage(topic: String, event: String, ref: Option[String], payload: JValue)

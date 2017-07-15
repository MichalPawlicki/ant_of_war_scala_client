package com.u2i.antofwar.phoenix

import akka.NotUsed
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{BidiFlow, Flow}
import com.typesafe.scalalogging.Logger
import org.json4s.JValue
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods.{compact, parse, render}

object Codecs {
  private implicit val formats = org.json4s.DefaultFormats
  private val logger = Logger[Codecs.type]

  private lazy val unpackMessage: Flow[Message, String, NotUsed] = Flow[Message].collect { case TextMessage.Strict(text) => text }
  private lazy val messageCodec: BidiFlow[Message, String, String, Message, NotUsed] = BidiFlow.fromFlows(
    unpackMessage,
    Flow[String].map(TextMessage.Strict))
  private lazy val jsonCodec: BidiFlow[String, JValue, JValue, String, NotUsed] = BidiFlow.fromFunctions[String, JValue, JValue, String](
    string => parse(string),
    jValue => compact(render(jValue)))
  private lazy val jsonToPhoenixMessageCodec: BidiFlow[JValue, PhoenixMessage, PhoenixMessage, JValue, NotUsed] = BidiFlow.fromFunctions[JValue, PhoenixMessage, PhoenixMessage, JValue](
    jValue => PhoenixMessage(
      (jValue \ "topic").extract[String],
      (jValue \ "event").extract[String],
      (jValue \ "ref").extractOpt[String],
      jValue \ "payload"
    ),
    msg => ("topic" -> msg.topic) ~ ("event" -> msg.event) ~ ("ref" -> msg.ref.orNull) ~ ("payload" -> msg.payload)
  )

  lazy val phoenixMessageCodec: BidiFlow[Message, PhoenixMessage, PhoenixMessage, Message, NotUsed] =
    messageCodec.atop(jsonCodec).atop(jsonToPhoenixMessageCodec)
}

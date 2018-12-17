package com.xela

import java.net.URI

import io.circe.{Decoder, Encoder, HCursor, Json}

object CirceExtensions {

  implicit val uriEncoder: Encoder[URI] = a => Json.fromString(a.toString)

  implicit val uriDecoder: Decoder[URI] = (c: HCursor) => c.as[String].map(URI.create)

}

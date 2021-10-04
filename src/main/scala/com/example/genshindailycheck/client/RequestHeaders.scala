package com.example.genshindailycheck.client

import io.jvm.uuid._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import com.example.genshindailycheck.Config
import scala.collection.mutable
import scala.language.postfixOps
object RequestHeaders {

  def GenshinRequestMessage(
      method: HttpMethod,
      uri: Uri,
      cookie: String,
      entity: Option[RequestEntity],
      extra:Boolean
  ): HttpRequest = {
    HttpRequest(
      method,
      uri,
      Seq(
        RawHeader("Accept-Encoding", Config.AcceptEncoding),
        RawHeader("Cookie", cookie),
        RawHeader("User-Agent", Config.Ua),
        RawHeader("Referer", Config.ReferUrl),
        RawHeader("x-rpc-device_id", UUID.random.toString.toUpperCase),
        RawHeader("x-rpc-client_type", Config.ClientType),
        RawHeader("x-rpc-app_version", Config.AppVersion),
        RawHeader("DS", Config.getDS)
      ),
      entity.getOrElse(HttpEntity.Empty)
    )
  }
}

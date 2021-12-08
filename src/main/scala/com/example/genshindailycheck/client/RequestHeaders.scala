package com.example.genshindailycheck.client

import io.jvm.uuid._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import com.example.genshindailycheck.Config
import scala.collection.mutable
import scala.language.postfixOps
object RequestHeaders {
  def GenshinCloudHeaders(token: String, device_id: String) = Seq(
    RawHeader("Accept-Encoding", Config.AcceptEncoding),
    RawHeader("x-rpc-combo_token", token),
    RawHeader("x-rpc-client_type", "2"),
    RawHeader("x-rpc-app_version", "1.3.0"),
    RawHeader("x-rpc-sys_version", "11"),
    RawHeader("x-rpc-channel", "mihoyo"),
    RawHeader("x-rpc-device_id", device_id),
    RawHeader("x-rpc-device_name", "Xiaomi Mi 10 Pro"),
    RawHeader("x-rpc-device_model", "Mi 10 Pro"),
    RawHeader("x-rpc-app_id", Config.appId),
    RawHeader("x-rpc-channel", "mihoyo"),
    RawHeader("x-rpc-channel", "mihoyo"),
    RawHeader("User-Agent", Config.Ua),
    RawHeader("Referer", "https://app.mihoyo.com"),
    RawHeader("Content-Length", "0"),
    RawHeader("Host", "api-cloudgame.mihoyo.com"),
    RawHeader("Connection", "Keep-Alive")
  )
  def GladosHeaders(token:String)=Seq(
    RawHeader("Cookie",token),
    RawHeader("Content-Type","application/json")
  )
  def GenshinCloudRequest(
      method: HttpMethod,
      uri: Uri,
      token: String,
      device_id: String,
      entity: Option[RequestEntity]
  ): HttpRequest = {
    HttpRequest(
      method,
      uri,
      GenshinCloudHeaders(token, device_id),
      entity.getOrElse(HttpEntity.Empty)
    )
  }
  def GladosRequest(
      method: HttpMethod,
      uri: Uri,
      token: String,
      entity: Option[RequestEntity]
  ): HttpRequest = {
    HttpRequest(
      method,
      uri,
      GladosHeaders(token),
      entity.getOrElse(HttpEntity.Empty)
    )
  }
  def GenshinHeaders(cookie: String) = Seq(
    RawHeader("Accept-Encoding", Config.AcceptEncoding),
    RawHeader("Cookie", cookie),
    RawHeader("User-Agent", Config.Ua),
    RawHeader("Referer", Config.ReferUrl),
    RawHeader("x-rpc-device_id", UUID.random.toString.toUpperCase),
    RawHeader("x-rpc-client_type", Config.ClientType),
    RawHeader("x-rpc-app_version", Config.AppVersion),
    RawHeader("DS", Config.getDS)
  )
  def GenshinRequestMessage(
      method: HttpMethod,
      uri: Uri,
      cookie: String,
      entity: Option[RequestEntity],
      extra: Boolean
  ): HttpRequest = {
    HttpRequest(
      method,
      uri,
      GenshinHeaders(cookie),
      entity.getOrElse(HttpEntity.Empty)
    )
  }
}

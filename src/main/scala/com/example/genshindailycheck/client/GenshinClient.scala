package com.example.genshindailycheck.client

import akka.http.scaladsl.client.RequestBuilding.*
import akka.http.javadsl.model.HttpHeader
import akka.http.scaladsl.model.headers.{
  Cookie,
  HttpEncodings,
  RawHeader,
  Referer,
  `User-Agent`
}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl.*
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import akka.http.scaladsl.coding.Coders
import scala.util.{Failure, Success}
import scala.concurrent.{Future, Promise}
import java.nio.charset.Charset
import akka.pattern.pipe
import akka.http.scaladsl.model.*
import akka.stream.scaladsl.{FileIO, Framing}
import java.util.Date
import akka.util.ByteString
import scala.concurrent.duration.*
import akka.http.scaladsl.model.Uri.Query
import java.io.FileWriter
import akka.http.scaladsl.unmarshalling.*
import akka.http.scaladsl.common.EntityStreamingSupport
import akka.http.scaladsl.common.JsonEntityStreamingSupport
import spray.json.JsArray
import java.nio.file.Paths
import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps
import spray.json._
import DefaultJsonProtocol._

class GenshinClient(cookie: String, extra: Boolean = false) {
  if (cookie.isEmpty) {
    throw new java.lang.IllegalArgumentException("必须设置cookie")
  }
  def ExecuteGenshinRequest(
      path: String,
      parameters: String = "",
      method: HttpMethod,
      entity: Option[RequestEntity]
  ): String = {
    import GenshinClient.system
    import GenshinClient.executionContext
    import GenshinClient.ExecuteRequest
    val responseFuture: Future[HttpResponse] = {
      Http().singleRequest(
        RequestHeaders.GenshinRequestMessage(
          method,
          Uri(s"${GenshinClient.OpenApi}$path$parameters"),
          cookie,
          entity,
          extra
        )
      )
    }
    ExecuteRequest(Await.result(responseFuture, 10 seconds))
  }
}
object GenshinClient {
  val OpenApi: String = "https://api-takumi.mihoyo.com/"
  implicit val system: ActorSystem[String] =
    ActorSystem(Behaviors.empty[String], "SingleRequest")
  implicit val executionContext: ExecutionContextExecutor =
    system.executionContext

  def entityOfSize(size: Int) =
    HttpEntity(ContentTypes.`application/json`, "0" * size)

  def decodeResponse(response: HttpResponse): HttpResponse = {
    val decoder = response.encoding match {
      case HttpEncodings.gzip =>
        Coders.Gzip
      case HttpEncodings.deflate =>
        Coders.Deflate
      case HttpEncodings.identity =>
        Coders.NoCoding
      case other =>
        Coders.NoCoding
    }
    decoder.decodeMessage(response)
  }

  def defaultEntity(content: String) = {
    HttpEntity.Default(
      ContentTypes.`text/plain(UTF-8)`,
      content.length,
      Source(ByteString(content) :: Nil)
    )
  }
  def ExecuteRequest(request: HttpResponse): String = {
    val resp = decodeResponse(request)
    val strFuture = Future {
      val result: StringBuilder = new StringBuilder()
      Await.ready(
        resp.entity.dataBytes
          .map(_.utf8String)
          .runForeach(result.append(_)),
        10 seconds
      )
      result.toString()
    }
    Await.result(strFuture, 10 seconds)
  }
  def ExecuteGenshinCloudRequest(
      method: HttpMethod,
      uri: Uri,
      token: String,
      device_id: String,
      entity: Option[RequestEntity]
  ) = {
    val responseFuture: Future[HttpResponse] = {
      Http().singleRequest(
        RequestHeaders.GenshinCloudRequest(
          method,
          uri,
          token,
          device_id,
          entity
        )
      )
    }
    ExecuteRequest(Await.result(responseFuture, 10 seconds))
  }
}

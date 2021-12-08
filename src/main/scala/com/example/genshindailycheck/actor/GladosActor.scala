package com.example.genshindailycheck.actor

import cats.syntax.either.*
import io.circe.*
import io.circe.parser.*
import cats.syntax.functor.*
import io.circe.{Decoder, Encoder}, io.circe.generic.auto.*
import io.circe.syntax.*
import scala.concurrent.*
import akka.actor.Actor
import com.example.genshindailycheck.client.GenshinClient.*
import akka.http.scaladsl.model.HttpMethods
import com.example.genshindailycheck.Config
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.HttpEntity
import java.util.Date
case class GladosRequest(token: String)

class GladosActor extends Actor {
  def baseUrl = "https://https://glados.rocks/"
  override def receive: Receive = {
    case GladosRequest(token) => {
      val checkRes = ExecuteGladosRequest(
        HttpMethods.POST,
        Uri(s"${baseUrl}/api/user/checkin"),
        token,
        Some(
          HttpEntity(
            "{token: \"glados_network\"}"
          )
        )
      )
      val statusRes = parse(
        ExecuteGladosRequest(
          HttpMethods.GET,
          Uri(s"${baseUrl}/api/user/status"),
          token,
          Some(
            HttpEntity(
              "{token: \"glados_network\"}"
            )
          )
        )
      ).getOrElse(Json.Null)
      var data = statusRes.hcursor
        .downField("data")
      val created_at: Long = data
        .downField("created_at")
        .as[Long]
        .getOrElse(0)
      val days: Int = data
        .downField("days")
        .as[Int]
        .getOrElse(0)
      val date = System.currentTimeMillis()
      val diff = days - (date - created_at) / (1000 * 60 * 60 * 24)
      println(s"还剩${diff}天")
    }
    case _ => println("cloud")
  }
}

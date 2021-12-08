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
import akka.http.scaladsl.model.ContentTypes
case class GladosRequest(token: String)

class GladosActor extends Actor {
  def baseUrl = "https://glados.rocks"
  override def receive: Receive = {
    case GladosRequest(token) => {
      val checkRes = parse(ExecuteGladosRequest(
        HttpMethods.POST,
        Uri(s"${baseUrl}/api/user/checkin"),
        token,
        Some(
          HttpEntity(
            ContentTypes.`application/json`,
            """{"token":"glados_network"}"""
          )
        )
      )).getOrElse(Json.Null)
      val message=checkRes.hcursor.downField("message").as[String].getOrElse("")
      println(s"${message}")
      val statusRes = parse(
        ExecuteGladosRequest(
          HttpMethods.GET,
          Uri(s"${baseUrl}/api/user/status"),
          token,
          None
        )
      ).getOrElse(Json.Null)
      val days: Double = statusRes.hcursor
        .downField("data")
        .downField("leftDays")
        .as[Double]
        .getOrElse(0)
      println(s"还剩${days.toInt}天")
    }
    case _ => println("cloud")
  }
}

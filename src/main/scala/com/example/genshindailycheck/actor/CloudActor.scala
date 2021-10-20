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
case class GenshinCloudRequest(token: String, device_id: String)

class CloudActor extends Actor {
  override def receive: Receive = {
    case GenshinCloudRequest(token, device_id) => {
      val loginRes = ExecuteGenshinCloudRequest(
        HttpMethods.POST,
        Uri(s"${Config.HostUrl}/hk4e_cg_cn/gamer/api/login"),
        token,
        device_id,
        None
      )
      var walletRes = parse(
        ExecuteGenshinCloudRequest(
          HttpMethods.GET,
          Uri(s"${Config.HostUrl}/hk4e_cg_cn/wallet/wallet/get"),
          token,
          device_id,
          None
        )
      ).getOrElse(Json.Null)
      var coin = walletRes.hcursor
        .downField("data")
        .downField("coin")
        .downField("coin_num")
        .as[Int]
        .getOrElse(0)
      var free_time = walletRes.hcursor
        .downField("data")
        .downField("free_time")
        .downField("free_time")
        .as[Int]
        .getOrElse(0)
      var total_time = walletRes.hcursor
        .downField("data")
        .downField("total_time")
        .downField("total_time")
        .as[Int]
        .getOrElse(0)
      println(s"coin:$coin,free_time:$free_time,total_time:$total_time")
    }
    case _ => println("cloud")
  }
}

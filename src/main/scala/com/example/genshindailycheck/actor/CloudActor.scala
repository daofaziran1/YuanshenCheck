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
import com.example.genshindailycheck.*
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
      var free_time = walletRes.hcursor
        .downField("data")
        .downField("free_time")
        .downField("free_time")
        .as[Int]
        .getOrElse(0)
      println(s"free_time:$free_time")
    }
    case Stop() => {
      println("停止")
      Main.num += 1
      if (Main.num == Main.numOfActor) {
        context.system.terminate()
        context.stop(self)
      } else {
        context.stop(self)
      }
    }
    case _ => println("cloud")
  }
}

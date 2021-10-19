package com.example.genshindailycheck.actor

import akka.actor.Actor
import cats.syntax.either.*
import io.circe.*
import io.circe.parser.*
import cats.syntax.functor.*
import io.circe.{Decoder, Encoder}, io.circe.generic.auto.*
import io.circe.syntax.*
import scala.concurrent.*
import ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.example.genshindailycheck.client.*
import com.example.genshindailycheck.Config
import com.example.genshindailycheck.SafeUtil
import spray.json.DefaultJsonProtocol
import io.circe.HCursor
import akka.http.scaladsl.model.*
import scala.collection.mutable
import io.jvm.uuid.*
import java.io.IOException
import concurrent.duration.*
import scala.concurrent.Future

case class SignDayEntity(
    retcode: Int,
    message: String,
    data: SignDayData
) {
  def checkOutCodeAndSleep = Future {
    Thread.sleep(3000)
    retcode match {
      case 0     => "执行成功"
      case -5003 => s"$message"
      case _     => throw new RuntimeException(s"请求异常$message")
    }
  }
}
case class SignDayData(
    total_sign_day: Int,
    today: String,
    is_sign: Boolean,
    first_bind: Boolean
) {
  override def toString =
    s"签到天数:$total_sign_day,今日为:$today,签到情况:${if (is_sign) "已签到" else "未签到"}"
}
case class SignResultData(code: String)

case class SignResultEntity(
    retcode: Int,
    message: String,
    data: SignResultData
) {
  def checkOutCodeAndSleep: Future[String] = Future {
    Thread.sleep(3000)
    retcode match {
      case 0     => "执行成功"
      case -5003 => s"$message"
      case _     => throw new RuntimeException(s"请求异常$message")
    }
  }
}

case class UserGameRolesListItem(
    game_biz: String,
    region: String,
    game_uid: String,
    nickname: String,
    level: Int,
    is_chosen: Boolean,
    region_name: String,
    is_official: Boolean
) {
  override def toString =
    s"昵称:$nickname,等级:$level,区域:$region_name"
}
case class UserGameRolesData(list: List[UserGameRolesListItem])

case class UserGameRolesEntity(
    retcode: Int,
    message: String,
    data: UserGameRolesData
) {
  val checkOutCodeAndSleep = Future {
    Thread.sleep(3000)
    retcode match {
      case 0     => "执行成功"
      case -5003 => s"$message"
      case _     => throw new RuntimeException(s"请求异常$message")
    }
  }
}
given Encoder[SignDayData] = {
  Encoder.forProduct4("total_sign_day", "today", "is_sign", "first_bind")(u =>
    (u.total_sign_day, u.today, u.is_sign, u.first_bind)
  )
}
given Decoder[SignDayData] = new Decoder[SignDayData] {
  final def apply(c: HCursor): Decoder.Result[SignDayData] =
    for {
      total_sign_day <- c.downField("total_sign_day").as[Int]
      today <- c.downField("today").as[String]
      is_sign <- c.downField("is_sign").as[Boolean]
      first_bind <- c.downField("first_bind").as[Boolean]
    } yield {
      new SignDayData(total_sign_day, today, is_sign, first_bind)
    }
}

given Encoder[SignDayEntity] = {
  Encoder.forProduct3("retcode", "message", "data")(u =>
    (u.retcode, u.message, u.data)
  )
}
given Decoder[SignDayEntity] = new Decoder[SignDayEntity] {
  final def apply(c: HCursor): Decoder.Result[SignDayEntity] =
    for {
      retcode <- c.downField("retcode").as[Int]
      message <- c.downField("message").as[String]
      data <- c.downField("data").as[SignDayData]
    } yield {
      new SignDayEntity(retcode, message, data)
    }
}
given Encoder[SignResultData] =
  Encoder.forProduct1("code")(u => u.code)
given Decoder[SignResultData] = new Decoder[SignResultData] {
  final def apply(c: HCursor): Decoder.Result[SignResultData] =
    for {
      code <- c.downField("code").as[String]
    } yield {
      new SignResultData(code)
    }
}
given Encoder[UserGameRolesListItem] = {
  Encoder.forProduct8(
    "game_biz",
    "region",
    "game_uid",
    "nickname",
    "level",
    "is_chosen",
    "region_name",
    "is_official"
  )(u =>
    (
      u.game_biz,
      u.region,
      u.game_uid,
      u.nickname,
      u.level,
      u.is_chosen,
      u.region_name,
      u.is_official
    )
  )
}
given Decoder[UserGameRolesListItem] = new Decoder[UserGameRolesListItem] {
  final def apply(c: HCursor): Decoder.Result[UserGameRolesListItem] =
    for {
      game_biz <- c.downField("game_biz").as[String]
      region <- c.downField("region").as[String]
      game_uid <- c.downField("game_uid").as[String]
      nickname <- c.downField("nickname").as[String]
      level <- c.downField("level").as[Int]
      is_chosen <- c.downField("is_chosen").as[Boolean]
      region_name <- c.downField("region_name").as[String]
      is_official <- c.downField("is_official").as[Boolean]
    } yield {
      new UserGameRolesListItem(
        game_biz,
        region,
        game_uid,
        nickname,
        level,
        is_chosen,
        region_name,
        is_official
      )
    }
}

given Encoder[SignResultEntity] = {
  Encoder.forProduct3("retcode", "message", "data")(u =>
    (u.retcode, u.message, u.data)
  )
}
given Decoder[SignResultEntity] = new Decoder[SignResultEntity] {
  final def apply(c: HCursor): Decoder.Result[SignResultEntity] =
    for {
      retcode <- c.downField("retcode").as[Int]
      message <- c.downField("message").as[String]
      data <- c.downField("data").as[SignResultData]
    } yield {
      new SignResultEntity(retcode, message, data)
    }
}

given Encoder[UserGameRolesData] = {
  Encoder.forProduct1("list")(u => (u.list))
}
given Decoder[UserGameRolesData] = new Decoder[UserGameRolesData] {
  final def apply(c: HCursor): Decoder.Result[UserGameRolesData] =
    for {
      list <- c.downField("list").downArray.as[UserGameRolesListItem]
    } yield {
      new UserGameRolesData(List(list))
    }
}
given Encoder[UserGameRolesEntity] = {
  Encoder.forProduct3("retcode", "message", "data")(u =>
    (u.retcode, u.message, u.data)
  )
}
given Decoder[UserGameRolesEntity] = new Decoder[UserGameRolesEntity] {
  final def apply(c: HCursor): Decoder.Result[UserGameRolesEntity] =
    for {
      retcode <- c.downField("retcode").as[Int]
      message <- c.downField("message").as[String]
      data <- c.downField("data").as[UserGameRolesData]
    } yield {
      new UserGameRolesEntity(retcode, message, data)
    }
}
case class CheckInRequest(cookie: String)

class GenshinActor extends Actor {
  override def receive: Receive = {
    case CheckInRequest(cookie) => {
      val client = new GenshinClient(cookie)
      var roleResult = parse(
        client
          .GetExecuteRequest(
            Config.GetUserGameRolesByCookie,
            "game_biz=hk4e_cn"
          )
      ).getOrElse(Json.Null)
      var rolesResult: UserGameRolesEntity =
        roleResult.as[UserGameRolesEntity].getOrElse(null)
      println(roleResult.noSpaces)
      val accountBindCount = rolesResult.data.list.size
      println(s"绑定了$accountBindCount 个角色")
      for (i <- rolesResult.data.list.indices) {
        val role = rolesResult.data.list(i)
        println(role)
        val signDayResult = parse(
          client.GetExecuteRequest(
            Config.GetBbsSignRewardInfo,
            s"act_id=${Config.ActId}&region=${role.region}&uid=${role.game_uid}"
          )
        ).getOrElse(Json.Null).as[SignDayEntity].getOrElse(null)
        val data = Map(
          "act_id" -> Config.ActId,
          "region" -> role.region,
          "uid" -> role.game_uid
        )
        println(data.asJson.noSpaces)
        val signClient = new GenshinClient(cookie, true)
        val res = signClient.PostExecuteRequest(
          Config.PostSignInfo,
          "",
          Some(
            HttpEntity(
              data.asJson.noSpaces
            )
          )
        )
        println(res)
      }
    }
    case _ => println("GenshinActor")
  }
}

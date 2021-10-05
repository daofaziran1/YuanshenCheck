package com.example.genshindailycheck

import cats.syntax.functor._
import io.circe.{Decoder, Encoder}, io.circe.generic.auto._
import io.circe.syntax._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.Future
import client._
import spray.json.DefaultJsonProtocol
import io.circe.HCursor
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model._
import scala.collection.mutable
import io.jvm.uuid._
import java.io.IOException

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
  def checkOutCodeAndSleep = Future {
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
object Main {
  def main(args: Array[String]) = {
    import cats.syntax.either._
    import io.circe._, io.circe.parser._
    import concurrent.duration._
    println("开始签到")
    // if (args.length <= 0) {
    //   throw new RuntimeException("获取参数不对")
    // }
    try {
      val cookieString =System.getenv("COOKIE")
      val cookies = cookieString.split("#")
      var accountNum = 0
      cookies.foreach(cookie => {
        accountNum += 1
        println(s"开始签到 账号$accountNum")
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
//        Await.result(rolesResult.checkOutCodeAndSleep, 60.seconds)
        val accountBindCount = rolesResult.data.list.size
        println(s"账号$accountNum 绑定了$accountBindCount 个角色")
        for (i <- rolesResult.data.list.indices) {
          println(rolesResult.data.list(i))
          val roles = rolesResult.data.list(i)
          val signDayResult = parse(
            client.GetExecuteRequest(
              Config.GetBbsSignRewardInfo,
              s"act_id=${Config.ActId}&region=${roles.region}&uid=${roles.game_uid}"
            )
          ).getOrElse(Json.Null).as[SignDayEntity].getOrElse(null)
          //检查第二步是否签到
//          Await.result(rolesResult.checkOutCodeAndSleep, 60.seconds)
          println(signDayResult.data)
          val data = Map(
            "act_id" -> Config.ActId,
            "region" -> roles.region,
            "uid" -> roles.game_uid
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
          val result = parse(res).getOrElse(Json.Null).as[SignResultEntity].getOrElse(null)
//          println(Await.result(result.checkOutCodeAndSleep, 60.seconds))
        }
      })
    } catch {
      case e: IllegalArgumentException => {
        println(s"参数错误${e}")
      }
      case e: Exception => {
        println(s"发生未知错误${e}")
        e.printStackTrace
      }
    }
    println("签到结束")
    System.exit(0)
  }
}

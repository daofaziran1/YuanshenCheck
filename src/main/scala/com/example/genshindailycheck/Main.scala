package com.example.genshindailycheck

import cats.syntax.functor.*
import io.circe.{Decoder, Encoder}, io.circe.generic.auto.*
import io.circe.syntax.*
import com.example.genshindailycheck.actor.*
import akka.actor.ActorSystem
import cats.syntax.either.*
import io.circe.*, io.circe.parser.*
import concurrent.duration.*
import akka.actor.Props
import scala.concurrent.Future
import scala.concurrent.Await

object Main {
  def main(args: Array[String]) = {
    println("开始签到")
    val system = ActorSystem("system")
    val checkActor = system.actorOf(Props[GenshinActor])
    val cloudActor = system.actorOf(Props[CloudActor])
    val gladosActor = system.actorOf(Props[GladosActor])
    val token = sys.env("token")
    val device_id = sys.env("device_id")
    val cookieString = sys.env("COOKIE")
    val glados = sys.env("glados")
    val cookies = cookieString.split("#")
    cookies.foreach(cookie => {
      checkActor ! CheckInRequest(cookie)
    })
    val tokens = token.split("#")
    val device_ids = device_id.split("#")
    for (i <- tokens.indices) {
      cloudActor ! GenshinCloudRequest(tokens(i), device_ids(i))
    }
    val glados_tokens = glados.split("#")
    glados_tokens.foreach(token => {
      gladosActor ! GladosRequest(token)
    })
    import scala.language.postfixOps
    Await.ready(system.terminate(), 10 seconds)
    println("签到结束")
    System.exit(0)
  }
}

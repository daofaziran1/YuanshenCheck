package com.example.genshindailycheck

import java.security.MessageDigest
import scala.util.Random
import scala.collection.mutable
object SafeUtil{
  //MD5加密
  def UserMd5(str:String)={
     val md5 = MessageDigest.getInstance("MD5")
      val encoded = md5.digest((str).getBytes)
      encoded.map("%02x".format(_)).mkString
  }
  //1f3742741e52c379281fb3dd9cc8bad5
  //bbd8e0d756166368650984ef2576230a

  //获取时间戳
  def GetTimeStamp():Long=
    (System.currentTimeMillis()/1000)
  //固定长度随机字符串
  def GetRandomString(length:Int):String={
    val str="abcdefghijklmnopqrstuvwxyz0123456789"
    val sb=new mutable.StringBuilder(length)
    for(i <- 0 until length){
      sb.append(str(Random.nextInt(str.length)))
    }
    sb.result()
  }
}
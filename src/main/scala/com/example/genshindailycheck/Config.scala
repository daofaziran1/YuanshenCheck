package com.example.genshindailycheck

object Config {
  def Ua =
    s"Mozilla/5.0 (Linux; Android 5.1.1; f103 Build/LYZ28N; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/52.0.2743.100 Safari/537.36 miHoYoBBS/$AppVersion"
  def AcceptEncoding = "gzip, deflate"
  def AppVersion = "2.3.0"
  def ClientType="5"
  def Salt="h8w582wxwgqvahcdkpvdhbh2w9casgfl"
  def ActId ="e202009291139501"
  def BaseUrl = "https://webstatic.mihoyo.com/bbs/event/signin-ys/index.html"
  def ReferUrl =
    s"$BaseUrl?bbs_auth_required=true&act_id=$ActId&utm_source=bbs&utm_medium=mys&utm_campaign=icon"
  //获取账号信息
  def GetUserGameRolesByCookie = "binding/api/getUserGameRolesByCookie?"
  //获取签到信息
  def GetBbsSignRewardInfo ="event/bbs_sign_reward/info?"
  //签到
  def PostSignInfo ="event/bbs_sign_reward/sign"
  //获取头部DS
  def getDS={
    val time =SafeUtil.GetTimeStamp()
    val stringRom=SafeUtil.GetRandomString(6).toLowerCase
    val stringAdd=s"salt=$Salt&t=$time&r=$stringRom"
    val stringMd5=SafeUtil.UserMd5(stringAdd)
    s"$time,$stringRom,$stringMd5"
  }
}

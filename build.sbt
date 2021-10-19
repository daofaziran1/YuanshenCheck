val scala3Version = "3.0.2"
lazy val akkaHttpVersion = "10.2.4"
lazy val akkaVersion = "2.6.14"
val circeVersion = "0.14.1"
lazy val root = (project in file("."))
  .settings(
    organization := "com.example",
    name := "genshindailycheck",
    version := "0.0.1-SNAPSHOT",
    trapExit := false,
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "io.jvm.uuid" %% "scala-uuid" % "0.3.1"
    ).map(x => x.cross(CrossVersion.for3Use2_13)),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion
    )
  )

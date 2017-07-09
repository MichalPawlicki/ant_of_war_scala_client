import sbt.Keys.version

lazy val dependencies = Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe" % "config" % "1.3.1",
  "com.typesafe.akka" %% "akka-http" % "10.0.6",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "org.json4s" %% "json4s-native" % "3.5.2"
)

lazy val root = (project in file("."))
  .settings(
    organization := "com.u2i.antofwar",
    name := "scala-client",
    version := "1.0",
    scalaVersion := "2.12.2",
    libraryDependencies ++= dependencies,
    cancelable in Global := true
  )

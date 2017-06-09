import sbt.Keys.version

lazy val dependencies = Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.6",
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

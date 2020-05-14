name := "imdb-copycat"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % "test"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.11"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.26" // or whatever the latest version is
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11"

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "3.3.0",
  "org.slf4j" % "slf4j-nop" % "1.7.26",
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "org.xerial" % "sqlite-jdbc" % "3.7.2"
)

scalacOptions += "-deprecation"
fork in run := true

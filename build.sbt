name := "soymilky"

version := "0.1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "org.twitter4j" % "twitter4j-stream" % "3.0.3"
)

initialCommands in console := """
  import soymilky._
  import twitter4j._
"""


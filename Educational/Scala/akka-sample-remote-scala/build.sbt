name := """akka-sample-remote-scala"""

version := "2.3.9"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-remote" % "2.3.9"
)



fork in run := true
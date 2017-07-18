name := "stanoq"
organization := "com.stanoq"
version := "1.0"
scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV       = "2.4.16"
  val akkaHttpV   = "10.0.1"
  val scalaTestV  = "3.0.1"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "org.scalatest"     %% "scalatest" % scalaTestV % "test",
    "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.2" % "test",
    "io.gatling"            % "gatling-test-framework"    % "2.2.2" % "test",
    "org.jsoup"             % "jsoup" % "1.10.3"
  )
}

val test = project.in(file(".")).enablePlugins(GatlingPlugin)
Revolver.settings
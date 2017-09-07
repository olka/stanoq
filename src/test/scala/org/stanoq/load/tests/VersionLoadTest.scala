package org.stanoq.load.tests

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class VersionLoadTest extends Simulation{

  val httpConf = http.baseURL("http://192.168.1.5:9000").doNotTrackHeader("1")

  val scn = scenario("Version simulation")
    .exec(http("Version test").get("/version").check(status.is(200)))

  setUp(scn.inject(rampUsersPerSec(50) to 350 during(45 seconds))).protocols(httpConf)
}

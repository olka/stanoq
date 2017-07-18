
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LoadTest extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:9000")
    .acceptHeader("application/json,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("Version simulation")
    .exec(http("VersionTest")
    .get("/version").check(status.is(200)))//body(RawFileBody("config.json")).asJSON
    .pause(2)

  setUp(scn.inject(rampUsersPerSec(200) to 550 during(1 minute))).protocols(httpConf)

}
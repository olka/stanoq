package org.stanoq.tests.crawler

import org.scalatest._
import org.stanoq.auth.JwtAuth
import spray.json._
import org.stanoq.crawler.model.{ConfigProperties, CrawlerProtocols}
import pdi.jwt.exceptions.JwtValidationException

class AuthSpec extends FlatSpec with Matchers with CrawlerProtocols {

  val config = ConfigProperties("https://www.websocket.org/index.html",3)
  var jwt = "";

  "Auth" should "be able to encode JWT" in {
    jwt = new JwtAuth(config.toJson.toString, "secret").encode
    jwt.length should be >0
  }

  "Auth" should "be able to properly decode JWT" in {
      config.toJson.toString shouldBe new JwtAuth(jwt, "secret").decode.get._2
  }

  "Auth" should "should throw validation exception" in {
    assertThrows[JwtValidationException] {
      config.toJson.toString should not be new JwtAuth(jwt, "secret2").decode.get._2
    }
  }
}

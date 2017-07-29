package org.stanoq.tests

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import org.stanoq.version.org.stanoq.crawler.VersionService

class VersionSpec extends FlatSpec with Matchers with ScalatestRouteTest {

  val versionService = new VersionService

  "Version service" should "respond with proper service version" in {
    Get("/version") ~> versionService.route ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      entityAs[String] shouldBe s"""{"version": 0.1}"""
    }
  }
}

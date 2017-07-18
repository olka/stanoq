package org.stanoq.tests.crawler

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import org.stanoq.crawler.CrawlerService
import org.stanoq.crawler.model.{ConfigProperties, CrawlerProtocols, CrawlerResponse}
import spray.json._

class ServiceSpec extends FlatSpec with Matchers with ScalatestRouteTest with CrawlerProtocols {

  val crawlerService = new CrawlerService
  val configJson = scala.io.Source.fromFile("config.json").mkString

  "CrawlerService" should "respond with 16 processed pages on crawling websocket.org with depth 1 (json)" in {
    Post(s"/crawler", configJson.parseJson.convertTo[ConfigProperties]) ~> crawlerService.route ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[CrawlerResponse].visited.size shouldBe 16
      responseAs[CrawlerResponse].errors.size shouldBe 0
    }
  }

  "CrawlerService" should "respond with 33 processed pages on crawling websocket.org with depth 3" in {
    Post(s"/crawler", ConfigProperties("https://www.websocket.org/echo.html",3)) ~> crawlerService.route ~> check {
      status shouldBe FailedDependency
      contentType shouldBe `application/json`
      responseAs[CrawlerResponse].visited.size shouldBe 33
      responseAs[CrawlerResponse].errors.size shouldBe 2
    }
  }
}

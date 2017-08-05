package org.stanoq.tests.crawler

import akka.event.NoLogging
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import org.stanoq.crawler.CrawlerService
import org.stanoq.crawler.model.{ConfigProperties, CrawlerProtocols, CrawlerResponse}
import spray.json._

import scala.io.Source

class ServiceSpec extends FlatSpec with Matchers with ScalatestRouteTest with CrawlerProtocols {
  override def testConfigSource = "akka.loglevel = DEBUG"
  def config = testConfig
  val logger = NoLogging

  val crawlerService = new CrawlerService
  val configJson = Source.fromFile("config.json").mkString

  "CrawlerService" should "respond with 7 processed pages on crawling websocket.org with depth 1 (json)" in {
    Post(s"/crawler", configJson.parseJson.convertTo[ConfigProperties]) ~> crawlerService.route ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[CrawlerResponse].pages.size should be >2
    }
  }

  "CrawlerService" should "respond with 33 processed pages on crawling websocket.org with depth 4" in {
    Post(s"/crawler", ConfigProperties("https://www.websocket.org/index.html",4)) ~> crawlerService.route ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      val res = responseAs[CrawlerResponse]
      res.pages.size should be >20
      res.pages.filter(_.statusCode!=200).size shouldBe 2
    }
  }
}

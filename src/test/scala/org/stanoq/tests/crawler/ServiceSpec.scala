package org.stanoq.tests.crawler

import akka.event.NoLogging
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import org.stanoq.crawler.CrawlerService
import org.stanoq.crawler.model.{ConfigProperties, CrawlerProtocols, Node}
import spray.json._

import scala.io.Source

class ServiceSpec extends FlatSpec with Matchers with ScalatestRouteTest with CrawlerProtocols {
  override def testConfigSource = "akka.loglevel = DEBUG"
  def config = testConfig
  val logger = NoLogging

  val crawlerService = new CrawlerService
  val configJson = Source.fromFile("config.json").mkString

  "CrawlerService" should "respond with >2 processed pages on crawling websocket.org with depth 1 (json)" in {
    Post(s"/crawler", configJson.parseJson.convertTo[ConfigProperties]) ~> crawlerService.route ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[Node].children.size should be >0
    }
  }

  "CrawlerService" should "respond with >20 processed pages on crawling websocket.org with depth 3" in {
    Post(s"/crawler", ConfigProperties("https://www.websocket.org/index.html",3)) ~> crawlerService.route ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[Node].getChildCount should be >20
    }
  }
}

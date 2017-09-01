package org.stanoq.tests.crawler

import akka.event.NoLogging
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.scalatest._
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.stanoq.crawler.CrawlerService
import org.stanoq.crawler.model._
import org.stanoq.stream.StreamService
import spray.json._

import scala.concurrent.duration._
import scala.io.Source

class ServiceSpec extends AsyncFlatSpec with Matchers with ScalatestRouteTest with CrawlerProtocols with Eventually with IntegrationPatience{
  override def testConfigSource = "akka.loglevel = DEBUG"
  def config = testConfig
  val logger = NoLogging
  implicit val timeout = RouteTestTimeout(5.seconds)

  val crawlerService = new CrawlerService
  val streamService = new StreamService
  val configJson = Source.fromFile("config.json").mkString

  "CrawlerService" should "respond with 20 processed pages on crawling websocket.org with depth 3" in {
    Post(s"/crawler", ConfigProperties("https://www.websocket.org/index.html",3)) ~> crawlerService.route ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[Node].getChildCount should be >=20
    }
  }

  "CrawlerService" should "handle crawlerStream endpoint properly" in {
      Post(s"/crawlerStream", configJson.parseJson.convertTo[ConfigProperties]) ~> streamService.route ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      eventually {
        responseAs[Seq[CrawlerResponse]].size should be > 0
        responseAs[Seq[CrawlerResponse]].head.node.value should include("websocket")
      }
    }
  }
}

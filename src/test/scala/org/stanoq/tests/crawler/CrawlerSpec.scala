package org.stanoq.tests.crawler

import akka.event.NoLogging
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import org.stanoq.crawler.{Crawler, CrawlerService}
import org.stanoq.crawler.model.{ConfigProperties, CrawlerProtocols, CrawlerResponse}
import spray.json._

class CrawlerSpec extends FlatSpec with Matchers {

  "Crawler" should "respond with 16 processed pages on crawling websocket.org with depth 1" in {
    val crawler = new Crawler(ConfigProperties("https://www.websocket.org/echo.html", 1))
    crawler.process shouldBe true
    crawler.visitedPages.size shouldBe 7
    crawler.getErrorPages.size shouldBe 0
  }

  "Crawler" should "respond with 16 processed pages on crawling websocket.org with depth 2" in {
    val crawler = new Crawler(ConfigProperties("https://www.websocket.org/echo.html", 2))
    crawler.process shouldBe true
    crawler.visitedPages.size shouldBe 16
    crawler.getErrorPages.size shouldBe 0
  }

  "Crawler" should "respond with 33 processed pages on crawling websocket.org with depth 3" in {
    val crawler = new Crawler(ConfigProperties("https://www.websocket.org/echo.html", 3))
    crawler.process shouldBe false
    crawler.visitedPages.size shouldBe 33
    crawler.getErrorPages.size shouldBe 2
  }

}

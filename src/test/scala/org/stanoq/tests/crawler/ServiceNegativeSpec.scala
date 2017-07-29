package org.stanoq.tests.crawler

import akka.event.Logging.LoggerInitialized
import akka.event.NoLogging
import akka.http.scaladsl.server.ValidationRejection
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import org.stanoq.crawler.CrawlerService
import org.stanoq.crawler.model.{ConfigProperties, CrawlerProtocols}

class ServiceNegativeSpec extends FlatSpec with Matchers with ScalatestRouteTest with CrawlerProtocols {
  val crawlerService = new CrawlerService(null)

  "org.stanoq.Service" should "respond with ValidationRejection on empty url" in {
    Post(s"/crawler", ConfigProperties("", 4)) ~> crawlerService.route ~> check {
      rejection === ValidationRejection("Cashflow entity has wrong structure!", None)
    }
  }

  "org.stanoq.Service" should "respond with ValidationRejection on null url" in {
    val exception = intercept[java.lang.IllegalArgumentException] {
      Post(s"/crawler", ConfigProperties(null, 4)) ~> crawlerService.route ~> check {}
    }
    exception.getMessage shouldBe "requirement failed: Config wasn't properly set!"
  }

  "org.stanoq.Service" should "respond with ValidationRejection on negative depthLimit" in {
    Post(s"/crawler", ConfigProperties("https://www.websocket.org/echo.html", -2)) ~> crawlerService.route ~> check {
      rejection === ValidationRejection("Cashflow entity has wrong structure!", None)
    }
  }

  "org.stanoq.Service" should "respond with ValidationRejection on negative timeout" in {
    Post(s"/crawler", ConfigProperties("https://www.websocket.org/echo.html", 2, -4)) ~> crawlerService.route ~> check {
      rejection === ValidationRejection("Cashflow entity has wrong structure!", None)
    }
  }

  "org.stanoq.Service" should "respond with ValidationRejection on bad url" in {
    Post(s"/crawler", ConfigProperties("https://www.co.hml", 2)) ~> crawlerService.route ~> check {
      rejection === ValidationRejection("Cashflow entity has wrong structure!", None)
    }
  }
}

package org.stanoq.tests.crawler

import org.scalatest._
import org.stanoq.crawler.Crawler
import org.stanoq.crawler.model.ConfigProperties

class CrawlerSpec extends FlatSpec with Matchers {

  "Crawler" should "handle recursive page structure" in {
    val crawler = new Crawler(ConfigProperties("https://www.websocket.org/echo.html", 2)).process
    crawler.visitedPages.filter(url => crawler.root.print.contains(url)).size shouldBe crawler.root.convertToNode.getChildCount-1
  }

  "Crawler" should "properly process gatling.io" in {
    val crawler = new Crawler(ConfigProperties("http://gatling.io", 2)).process
    crawler.visitedPages.size should be >=40
  }

  "Crawler" should "properly process https://www.websocket.org" in {
    val crawler = new Crawler(ConfigProperties("https://www.websocket.org", 20)).process
    crawler.root.convertToNode.getChildCount-1 shouldBe crawler.visitedPages.size
    crawler.visitedPages.size should be >=58
  }

}

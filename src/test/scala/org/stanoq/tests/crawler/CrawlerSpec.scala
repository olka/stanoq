package org.stanoq.tests.crawler

import org.scalatest._
import org.stanoq.crawler.Crawler
import org.stanoq.crawler.model.ConfigProperties

class CrawlerSpec extends FlatSpec with Matchers {

  "Crawler" should "respond with 16 processed pages on crawling websocket.org with depth 2" in {
    val crawler = new Crawler(ConfigProperties("https://www.websocket.org/echo.html", 2)).process
    crawler.visitedPages.size shouldBe 16
    crawler.getErrorPages.size shouldBe 0
  }

  "Crawler" should "respond with >9 processed pages on crawling facebook with depth 1" in {
    val crawler = new Crawler(ConfigProperties("https://facebook.com", 1)).process
    crawler.visitedPages.size should be >9
  }

  "Crawler" should "handle recursive page structure" in {
    val crawler = new Crawler(ConfigProperties("https://www.websocket.org/echo.html", 5)).process
    crawler.visitedPages.keySet.filter(p => crawler.root.print.contains(p.url)).size shouldBe crawler.visitedPages.size
  }

  "Crawler" should "be able convert to node" in {
    val crawler = new Crawler(ConfigProperties("https://www.websocket.org/echo.html", 4)).process
    crawler.root.convertToNode.children.size shouldBe 1
    crawler.root.convertToNode.getChildCount-1 shouldBe crawler.visitedPages.size
  }

}

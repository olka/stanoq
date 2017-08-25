package org.stanoq.tests.crawler

import org.scalatest._
import org.stanoq.crawler.Crawler
import org.stanoq.crawler.model.ConfigProperties

class CrawlerSpec extends FlatSpec with Matchers {

  "Crawler" should "respond with 16 processed pages on crawling websocket.org with depth 2" in {
    val crawler = new Crawler(ConfigProperties("https://www.websocket.org/echo.html", 2)).process
    crawler.visitedSet.size shouldBe 16
  }

  "Crawler" should "respond with >5 processed pages on crawling facebook with depth 1" in {
    val crawler = new Crawler(ConfigProperties("https://facebook.com", 1)).process
    crawler.visitedSet.size should be >5
  }

  "Crawler" should "handle recursive page structure" in {
    val crawler = new Crawler(ConfigProperties("https://www.websocket.org/echo.html", 5)).process
    crawler.visitedSet.filter(url => crawler.root.print.contains(url)).size shouldBe crawler.root.convertToNode.getChildCount
  }

  "Crawler" should "be able convert to node" in {
    val crawler = new Crawler(ConfigProperties("https://www.websocket.org/echo.html", 4)).process
    crawler.root.convertToNode.print
    crawler.root.convertToNode.children.size shouldBe 1
    crawler.root.convertToNode.getChildCount-1 shouldBe crawler.visitedSet.size
  }


  "Crawler" should "properly process gatling.io" in {
    val crawler = new Crawler(ConfigProperties("http://gatling.io", 1)).process
    println(crawler.root.convertToNode.print)
    crawler.visitedSet.size shouldBe 41
  }

}

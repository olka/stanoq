package org.stanoq.crawler

import java.nio.charset.StandardCharsets
import java.nio.file.StandardOpenOption._
import java.nio.file.{Files, Paths}

import akka.actor.ActorSystem
import akka.event.Logging
import org.jsoup.nodes.{Document, Element}
import org.jsoup.{Connection, HttpStatusException, Jsoup}
import org.stanoq.crawler.model.{ConfigProperties, Page}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Try

case class Cookie(key: String, value:String)

class Crawler(system: ActorSystem,config:ConfigProperties, cookie: Option[Cookie] = None){
  val logger = Logging(system, getClass)
  val visitedPages, errorPages = createSet[String]

  def process: Boolean = process("")

  def process(url: String): Boolean = {
    logger.info("Processing " + config.getUrl + url)
    val root: Page = new Page(config.getUrl, "ROOT")
    crawl(config.getUrl + url, 0, root)
    errorsCheck.size == 0
  }

  private def errorsCheck: mutable.Set[String] = {
    if (errorPages.size == 0) errorPages
    logger.info("Double checking " + errorPages)
    errorPages.filter(url => getDocument(url, new Page("", "")).isEmpty)
  }

  private def crawl(url: String, depth: Int, prev: Page) {
    if (!visitedPages.add(url) || depth > config.depthLimit) return
    val doc: Document = getDocument(url, prev) getOrElse (return)
    logger.info(visitedPages.size + " : " + url + " " + doc.title)
    parseLinksToVisit(doc).par.foreach(link => crawl(link, depth + 1, prev.addChild(new Page(url, doc.title()))))
  }

  private def parseLinksToVisit(doc: Document): mutable.Set[String] ={
    def predicate(l: Element):Boolean = {
      val link = l.attr("href")
      !(link.startsWith("#") || link.startsWith(".") || link.startsWith("mailto"))
    }
    val list = doc.select("a[href]").iterator().asScala.toStream.filter(predicate).map(l => l.attr("abs:href")).to[mutable.Set]
    list.filter(link => config.getExclusions.filter(s => link.contains(s)).size==0 && link.contains(config.getDomain))
  }

  private def writeToFile(filename: String, slist: java.util.Collection[String]) = Try(Files.write(Paths.get(filename), slist, StandardCharsets.UTF_8, APPEND, CREATE))
  private def createSet[T] = java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap[T, java.lang.Boolean]).asScala


  private def getDocument(url: String, prev: Page): Option[Document] = {
    def getDocument(con: Connection) = if (cookie.isEmpty) Some(con.get) else Some(con.cookie(cookie.get.key, cookie.get.value).get)
    (Try(Jsoup.connect(url).timeout(30 * 500)).map(getDocument).recover {
      case e: HttpStatusException => logger.error(e.getStatusCode + " :: on " + url + " :: came from: " + prev.url);errorPages.add(url);None
      case e: Exception => logger.info(e.getMessage);errorPages.add(url);None}).get
  }
}

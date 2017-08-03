package org.stanoq.crawler

import java.nio.charset.StandardCharsets
import java.nio.file.StandardOpenOption._
import java.nio.file.{Files, Paths}
import java.util.concurrent.ConcurrentHashMap

import akka.actor.ActorSystem
import akka.event.Logging
import org.jsoup.nodes.{Document, Element}
import org.jsoup.{Connection, HttpStatusException, Jsoup}
import org.stanoq.crawler.model.{ConfigProperties, Page}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Try

case class Cookie(key: String, value:String)

class Crawler(config:ConfigProperties, cookie: Option[Cookie] = None){
  val logger = Logging(ActorSystem(), getClass)
  val visitedPages = new ConcurrentHashMap[Page,String].asScala

  def process: Boolean = process("")

  def process(url: String): Boolean = {
    logger.info("Processing " + config.getUrl + url)
    val root: Page = new Page(config.getUrl, "ROOT",0)
    crawl(config.getUrl + url, 0, root)
    errorsCheck.size == 0
  }

  def getErrorPages = visitedPages.keySet.filter(_.statusCode!=200)

  private def errorsCheck: Set[String] = {
    getErrorPages.map(_.url).toSet
//    logger.info("Double checking " + errorPages)
//    errorPages.filter(page => getDocument(page.url,null).isEmpty).map(_.url).toSet
  }

  private def crawl(url: String, depth: Int, prev: Page) {
    if (visitedPages.keySet.exists(_.url.equals(url)) || depth > config.depthLimit) return
    val doc: Document = getDocument(url,prev) getOrElse (return)
    val page = new Page(url, doc.title(), 200)
    visitedPages.put(page,prev.url)
//    logger.info(visitedPages.size + " : " + url + " " + doc.title + " d:"+depth)
    parseLinksToVisit(doc).par.foreach(link => crawl(link, depth + 1, page))
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

  private def getDocument(url: String, prev:Page): Option[Document] = {
    def getDocument(con: Connection) = if (cookie.isEmpty) Some(con.get) else Some(con.cookie(cookie.get.key, cookie.get.value).get)
    (Try(Jsoup.connect(url).timeout(30 * 500)).map(getDocument).recover {
      case e: HttpStatusException => logger.error(e.getStatusCode + " :: on " + url);visitedPages.put(new Page(url,e.getMessage,e.getStatusCode),prev.url);None
      case e: Exception => logger.info(e.getMessage);visitedPages.put(new Page(url,e.getMessage,0),prev.url);None}).get
  }
}

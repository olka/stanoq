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
import scala.util.Try

case class Cookie(key: String, value:String)

class Crawler(config:ConfigProperties, cookie: Option[Cookie] = None){
  val logger = Logging(ActorSystem(), getClass)
  val visitedPages = new ConcurrentHashMap[Page,String].asScala
  val visitedSet = createSet[String]
  private val domain = getDomain(config.url)
  val root: Page = new Page(domain, domain,0,createSet[Page])

  def process:Crawler = process("")

  def process(url: String) = {
    logger.info("Processing " + config.url + url)
    crawl(config.url + url, 0, root)
    this
  }

  def getErrorPages = visitedPages.keySet.filter(_.statusCode!=200)

  private def errorsCheck: Set[String] = {
    getErrorPages.map(_.url).toSet
//    logger.info("Double checking " + errorPages)
//    errorPages.filter(page => getDocument(page.url,null).isEmpty).map(_.url).toSet
  }

  private def crawl(url: String, depth: Int, prev: Page) {
    if (!visitedSet.add(url) || !visitedSet.add(url.substring(0,url.length-1)) || depth > config.depthLimit) return
    val doc: Document = getDocument(url,prev) getOrElse (return)
    val page = new Page(url, doc.title(), 200,createSet[Page])
    visitedPages.put(page,prev.url)
    prev.addChild(page)
//    logger.info(visitedPages.size + " : " + url + " " + doc.get.title + " d:"+depth)
    val links = parseLinksToVisit(doc)
//    logger.info(url + " "+links.size)
    links.par.foreach(link => crawl(link, depth + 1, page))
  }

  private def parseLinksToVisit(doc: Document): List[String] ={
    def predicate(l: Element):Boolean = {
      val link = l.attr("href")
      !(link.startsWith("#") || link.startsWith(".") || link.startsWith("mailto"))
    }
    val list = doc.select("a[href]").iterator().asScala.toStream.filter(predicate).map(l => l.attr("abs:href")).toList
    list.filter(link => config.exclusions.filter(s => link.contains(s)).size==0 && link.contains(domain))
  }

  private def writeToFile(filename: String, slist: java.util.Collection[String]) = Try(Files.write(Paths.get(filename), slist, StandardCharsets.UTF_8, APPEND, CREATE))

  private def getDocument(url: String, prev:Page): Option[Document] = {
    def getDocument(con: Connection) = if (cookie.isEmpty) Some(con.get) else Some(con.cookie(cookie.get.key, cookie.get.value).get)
    (Try(Jsoup.connect(url).timeout(30 * 1000)).map(getDocument).recover {
      case e: HttpStatusException => logger.error(e.getStatusCode + " :: on " + url);
        prev.addChild(new Page(url,e.getMessage,e.getStatusCode,createSet[Page]));
        visitedPages.put(new Page(url,e.getMessage,e.getStatusCode,null),prev.url);
        None
      case e: Exception => logger.error(e.getMessage);visitedPages.put(new Page(url,e.getMessage,0,null),prev.url);None}
      ).get
  }
}

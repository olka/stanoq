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
  val visitedSet = createSet[String]
  private val domain = getDomain(config.url)
  val root: Page = new Page(domain, domain,0,createSet[Page])   //problem with stack!

  def process:Crawler = process("")

  def process(url: String) = {
    logger.info("Processing " + config.url + url)
    crawl(config.url + url, 0, root)
    this
  }

//  private def errorsCheck: Set[String] = getErrorPages.map(_.url).toSet   errorPages.filter(page => getDocument(page.url,null).isEmpty).map(_.url).toSet

  private def crawl(url: String, depth: Int, prev: Page) {//FIXME!!!
    if (!visitedSet.add(url)  || !visitedSet.add(url.substring(0,url.length-1)) || depth > config.depthLimit) return
    val page = new Page(url, "doc.title()", 200,createSet[Page])
    prev.addChild(page)
    val doc: Document = getDocument(url,prev) getOrElse (return)

//    logger.info(visitedPages.size + " : " + url + " " + doc.get.title + " d:"+depth)
    val links = parseLinksToVisit(doc)
//    logger.info(url + " "+links.size)
    links.foreach(link => crawl(link, depth + 1, page))
  }

  private def parseLinksToVisit(doc: Document): List[String] ={
    def predicate(l: String):Boolean = {logger.info(l);(!(l.trim.length<7 || l.startsWith("mailto"))) && l.contains(domain)}
    doc.select("a[href]").iterator().asScala.toStream.map(_.attr("abs:href")).filter(predicate).toList
  }

  private def writeToFile(filename: String, slist: java.util.Collection[String]) = Try(Files.write(Paths.get(filename), slist, StandardCharsets.UTF_8, APPEND, CREATE))

  private def getDocument(url: String, prev:Page): Option[Document] = {
    def getDocument(con: Connection) = if (cookie.isEmpty) Some(con.get) else Some(con.cookie(cookie.get.key, cookie.get.value).get)
    (Try(Jsoup.connect(url).timeout(30 * 1000)).map(getDocument).recover {
      case e: HttpStatusException => logger.error(e.getStatusCode + " :: on " + url);
        prev.addChild(new Page(url,e.getMessage,e.getStatusCode,createSet[Page]));
        visitedSet.add(url);None}).get
  }
}

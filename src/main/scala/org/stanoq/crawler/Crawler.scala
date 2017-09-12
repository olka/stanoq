package org.stanoq.crawler

import java.util.Collections
import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

import akka.actor.ActorSystem
import akka.event.Logging
import org.jsoup.nodes.Document
import org.jsoup.{Connection, HttpStatusException, Jsoup}
import org.stanoq.crawler.model.{ConfigProperties, Page}

import scala.collection.JavaConverters._
import scala.util.Try

class Crawler(config:ConfigProperties){
  val logger = Logging(ActorSystem(), getClass)
  val visitedPages = createSet[String]
  private val domain:String = config.getDomain
  val root: Page = new Page(domain, domain,0,createSet[Page])

  def process:Crawler = process("")

  def process(url: String) = {
    logger.info("Processing " + config.url + url)
    crawl(config.url + url, 1, root)
    root.statusCode = 200
    this
  }

  private def crawl(url: String, depth: Int, prev: Page) {
    if (visitedPages.contains(url.substring(0,url.length-1)) || !visitedPages.add(url)) return
    var doc: Document = getDocument(url,prev) getOrElse (return)
    val page = new Page(url, doc.title(), 200,createSet[Page])
    prev.addChild(page)
    logger.info(visitedPages.size + " : " + url + " " + doc.title + " d:"+depth)
    val links = parseLinksToVisit(doc)
    logger.info(url + " "+links.size)
    if(depth > config.depthLimit) return
    doc = null
    links.par.foreach(link => crawl(link, depth + 1, page))
  }

  private def parseLinksToVisit(doc: Document): List[String] ={
    def predicate(l: String):Boolean = (!(l.trim.length<7 || l.startsWith("mailto"))) && l.contains(domain)
    doc.select("a").iterator().asScala.toStream.map(_.attr("abs:href")).filter(predicate).toList
  }

  private def createSet[T] = Collections.newSetFromMap(new ConcurrentHashMap[T, java.lang.Boolean]).asScala

  private def getDocument(url: String, prev:Page): Option[Document] = {
    def getDocument(con: Connection):Option[Document] = {
      val time = System.nanoTime()
      val res = con.execute()
      println("Load time: " + url + "::::" +TimeUnit.MILLISECONDS.convert(System.nanoTime()-time, TimeUnit.NANOSECONDS))
      Some(res.parse())
    }
    (Try(Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(30 * 1000)).map(getDocument).recover {
      case e: HttpStatusException => logger.error(e.getStatusCode + " :: on " + url);
        prev.addChild(new Page(url,e.getMessage,e.getStatusCode,createSet[Page]));None
      case e: Exception => logger.error(e.getMessage + " :: on " + url);
        prev.addChild(new Page(url,e.getMessage,500,createSet[Page]));None
    }).get
  }
}

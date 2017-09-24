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
  val root: Page = new Page(domain, domain,0,0,0,createSet[Page])

  def process(url: String="") = {
    logger.info("Processing " + config.url + url)
    crawl(config.url + url, 1, root)
    root.statusCode = 200
    this
  }

  private def crawl(url: String, depth: Int, prev: Page) {
    if (visitedPages.contains(url.substring(0,url.length-1)) || !visitedPages.add(url)) return
    val (page,links) = getPage(url,prev)
    if(page.statusCode!=200) return
    prev.addChild(page)
    logger.info(url + " "+links.size)
    if(depth > config.depthLimit) return
    links.par.foreach(link => crawl(link, depth + 1, page))
  }

  private def createSet[T] = Collections.newSetFromMap(new ConcurrentHashMap[T, java.lang.Boolean]).asScala

  private def getPage(url: String, prev:Page): (Page,List[String]) = {
    def parseLinksToVisit(doc: Document): List[String] ={
      def predicate(l: String) = (!(l.trim.length<7 || l.startsWith("mailto"))) && l.contains(domain)
      doc.select("a").iterator().asScala.toStream.map(_.attr("abs:href")).filter(predicate).toList
    }

    def getDocument(con: Connection):(Page,List[String]) = {
      val time = System.nanoTime()
      val res = con.execute()
      val pageSize = res.bodyAsBytes().size
      val timeToLoad = TimeUnit.MILLISECONDS.convert(System.nanoTime()-time, TimeUnit.NANOSECONDS)
      val doc = res.parse()
      val links = parseLinksToVisit(doc)
      (new Page(url, doc.title(), 200,timeToLoad,pageSize,createSet[Page]),parseLinksToVisit(doc))
    }

    (Try(Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(30 * 1000)).map(getDocument).recover {
      case e: HttpStatusException => logger.error(e.getStatusCode + " :: on " + url);
        val errPage = new Page(url,e.getMessage,e.getStatusCode,2000,9000,createSet[Page])
        prev.addChild(errPage); (errPage, List())
      case e: Exception => logger.error(e.getMessage + " :: on " + url);
        val errPage = new Page(url,e.getMessage,500,2000,9000,createSet[Page])
        prev.addChild(errPage);(errPage, List())
    }).get
  }
}

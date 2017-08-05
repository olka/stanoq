package org.stanoq.crawler.model

import java.net.{URI, URISyntaxException, URL}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.util.Try

case class ConfigProperties(url:String, depthLimit:Int, timeout:Long=5, exclusions:List[String]=List(",")) {
  require(url != null, "Config wasn't properly set!")

  def validate = {
    if (depthLimit <0) false
    else if (timeout <0) false
    else if (url.equals("")) false
    else if (Try(new URL(url).getContent).isFailure) false
    else true
  }
  def getUrl: String = url

  def getDomain: String = {
    try {return new URI(getUrl).getHost}
    catch {case e: URISyntaxException => return null}
  }

  def getExclusions: List[String] = exclusions
}

case class Node(id: String, label:String, statusCode:Int )
case class Link(source: String, target:String)
case class CrawlerResponse(pages: List[Node], links: List[Link])

trait CrawlerProtocols extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val pageFormat: RootJsonFormat[Page] = jsonFormat3(Page.apply)
  implicit val configFormat: RootJsonFormat[ConfigProperties] = jsonFormat4(ConfigProperties.apply)
  implicit val nodeFormat: RootJsonFormat[Node] = jsonFormat3(Node.apply)
  implicit val linkFormat: RootJsonFormat[Link] = jsonFormat2(Link.apply)
  implicit val crawlerResponseFormat: RootJsonFormat[CrawlerResponse] = jsonFormat2(CrawlerResponse.apply)
}


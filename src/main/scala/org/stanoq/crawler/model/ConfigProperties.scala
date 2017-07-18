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

case class CrawlerResponse(visited: Set[String], errors:Set[String])

trait CrawlerProtocols extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val configFormat: RootJsonFormat[ConfigProperties] = jsonFormat4(ConfigProperties.apply)
  implicit val crawlerResponseFormat: RootJsonFormat[CrawlerResponse] = jsonFormat2(CrawlerResponse.apply)
}


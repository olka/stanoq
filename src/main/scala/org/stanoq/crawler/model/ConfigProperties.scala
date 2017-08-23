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

case class Node(value: String, children:List[Node]){
  override def toString() = value + " \n "
  def print:String = toString() + children.map(_.print).mkString
}

trait CrawlerProtocols extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val configFormat: RootJsonFormat[ConfigProperties] = jsonFormat4(ConfigProperties.apply)
  implicit val nodeFormat: RootJsonFormat[Node] = jsonFormat2(Node.apply)
}


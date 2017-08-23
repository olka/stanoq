package org.stanoq.crawler.model

import java.net.{URI, URISyntaxException, URL}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsonFormat, RootJsonFormat}

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
}

case class Node(value: String, children:Option[List[Node]], id:String){
  override def toString() = value + " ||"
  def print:String = toString() + children.get.map(_.print).mkString
  def getChildCount:Int = 1+children.get.map(_.getChildCount).sum
}

trait CrawlerProtocols extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val nodeFormat: RootJsonFormat[Node] = rootFormat(lazyFormat(jsonFormat(Node, "value", "children", "id")))
  implicit val configFormat: RootJsonFormat[ConfigProperties] = jsonFormat4(ConfigProperties.apply)
}


package org.stanoq.crawler.model

import java.net.{URI, URL}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.collection.mutable.Set
import scala.util.Try

case class ConfigProperties(url: String, depthLimit: Int, timeout: Long = 5, exclusions: List[String] = List(",")) {
  require(url != null, "Config wasn't properly set!")

  def validate = {
    def validateUrl = Try(new URL(if (url.contains("http")) url else "http://" + url).getContent()).isFailure

    if (depthLimit < 0) false
    else if (timeout < 0) false
    else if (url.equals("")) false
    else if (validateUrl) false
    else true
  }

  def getDomain: String = Try(new URI(url).getHost).get
}

case class Page(url: String, name: String, var statusCode: Int, timeToLoad: Long, size: Int, children: Set[Page]) {
  def addChild(page: Page) = children.add(page)

  def print: String = url + children.map(_.print).mkString

  def convertToNode: Node = {
    def getChildNodes = if (children.size > 0) Some(children.map(_.convertToNode).toList) else None

    Node(s"$name : $statusCode", getChildNodes, timeToLoad)
  }

  def parse: List[(EchartNode, List[EchartLink])] = {
    def getColor = {
      //добавить больше градацию
      import javafx.scene.paint.Color
      val hue: Int = if (timeToLoad > 1000) 0 else (timeToLoad / 6.6666).toInt
      val c: Color = Color.web("hsl(" + hue + ",100%,100%)")
      "#" + c.toString.substring(2, 8)
    }

    def getTuple = (EchartNode(url, timeToLoad, statusCode, getColor, "", size), children.map(p => EchartLink(url, p.url)).toList)

    getTuple :: children.flatMap(_.parse).toList
  }
}

case class EchartLink(source: String, target: String)

case class EchartNode(name: String, value: Long, statusCode: Int, color: String, category: String = "", size: Long = 0) {
  //

  //symbolSize: node.size
}

case class EchartResponse(nodes: List[EchartNode], links: List[EchartLink])

case class Node(value: String, children: Option[List[Node]], id: Long) {
  def getChildCount: Int = if (children.isEmpty) 1 else 1 + children.get.map(_.getChildCount).sum
}

case class CrawlerResponse(node: Node, echart: EchartResponse)

trait CrawlerProtocols extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val nodeFormat: RootJsonFormat[Node] = rootFormat(lazyFormat(jsonFormat(Node, "value", "children", "id")))
  implicit val configFormat: RootJsonFormat[ConfigProperties] = jsonFormat4(ConfigProperties.apply)
  implicit val elinkFormat: RootJsonFormat[EchartLink] = jsonFormat2(EchartLink.apply)
  implicit val enodeFormat: RootJsonFormat[EchartNode] = jsonFormat6(EchartNode.apply)
  implicit val echartFormat: RootJsonFormat[EchartResponse] = jsonFormat2(EchartResponse.apply)
  implicit val responseFormat: RootJsonFormat[CrawlerResponse] = jsonFormat2(CrawlerResponse.apply)
}
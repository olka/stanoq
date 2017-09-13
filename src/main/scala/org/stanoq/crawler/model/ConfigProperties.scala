package org.stanoq.crawler.model

import java.net.{URI, URL}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.collection.mutable.Set
import scala.util.Try

case class ConfigProperties(url:String, depthLimit:Int, timeout:Long=5, exclusions:List[String]=List(",")) {
  require(url != null, "Config wasn't properly set!")

  def validate = {
    def validateUrl = Try(new URL(if(url.contains("http")) url else "http://"+url).getContent()).isFailure

    if (depthLimit <0) false
    else if (timeout <0) false
    else if(url.equals("")) false
    else if(validateUrl) false
    else true
  }

  def getDomain: String = Try(new URI(url).getHost).get
}

case class Page(url: String, pageName: String, var statusCode: Int, timeToLoad:Long, children:Set[Page]){
  def addChild(page: Page) = children.add(page)
  def print:String = url + children.map(_.print).mkString
  def convertToNode:Node = Node(s"$pageName : $statusCode",if(children.size>0)Some(children.map(_.convertToNode).toList) else None,url)

  def parse:List[(EchartNode,List[EchartLink])] = {
    def getTuple = (EchartNode(url,hashCode,""), children.map(p => EchartLink(url,p.url)).toList)
    getTuple :: children.flatMap(_.parse).toList
  }
}

case class EchartLink(source: String, target: String)
case class EchartNode(name:String,value: Int,category:String)
case class EchartResponse(nodes:List[EchartNode], links:List[EchartLink])
case class Node(value: String, children:Option[List[Node]], id:String){
  def getChildCount:Int = if (children.isEmpty) 1 else 1 + children.get.map(_.getChildCount).sum
}
case class CrawlerResponse(node:Node,echart:EchartResponse)

trait CrawlerProtocols extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val nodeFormat: RootJsonFormat[Node] = rootFormat(lazyFormat(jsonFormat(Node, "value", "children", "id")))
  implicit val configFormat: RootJsonFormat[ConfigProperties] = jsonFormat4(ConfigProperties.apply)
  implicit val elinkFormat: RootJsonFormat[EchartLink] = jsonFormat2(EchartLink.apply)
  implicit val enodeFormat: RootJsonFormat[EchartNode] = jsonFormat3(EchartNode.apply)
  implicit val echartFormat: RootJsonFormat[EchartResponse] = jsonFormat2(EchartResponse.apply)
  implicit val responseFormat: RootJsonFormat[CrawlerResponse] = jsonFormat2(CrawlerResponse.apply)
}
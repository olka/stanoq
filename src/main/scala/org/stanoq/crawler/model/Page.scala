package org.stanoq.crawler.model

import scala.collection.mutable.Set

case class Page(url: String, pageName: String, statusCode: Int, children:Set[Page]){
  def addChild(page: Page) = children.add(page)
  override def toString() = url + " || "
  def print:String = toString() + children.map(_.print).mkString
  def convertToNode:Node = Node(s"$pageName : $statusCode",if(children.size>0)Some(children.map(_.convertToNode).toList) else None,url)
}
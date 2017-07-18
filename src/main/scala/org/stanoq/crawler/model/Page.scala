package org.stanoq.crawler.model

import scala.collection.mutable

case class Page(url: String, pageName: String) {

  private val adjacentPages: mutable.Set[Page] = mutable.Set[Page]()

  def addChild(page: Page): Page = {
    adjacentPages.add(page)
    page
  }
}

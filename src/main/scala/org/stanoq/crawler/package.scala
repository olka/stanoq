package org.stanoq

import java.net.URI
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

import scala.util.Try
import scala.collection.JavaConverters._

package object crawler {

  def getDomain(url:String): String = Try(new URI(url).getHost).get
  def createSet[T] = Collections.newSetFromMap(new ConcurrentHashMap[T, java.lang.Boolean]).asScala
}

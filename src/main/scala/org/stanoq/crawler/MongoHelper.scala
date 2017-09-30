package org.stanoq.crawler

import java.util.concurrent.TimeUnit

import com.typesafe.config.ConfigFactory
import org.mongodb.scala.{MongoClient, MongoCollection}
import org.stanoq.crawler.model._
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.model.Filters.equal

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object MongoHelper {
  val config = ConfigFactory.load()
  val mongoClient: MongoClient = MongoClient(config.getString("mongo.url"))
  val pageRegistry = fromRegistries(fromProviders(classOf[Page]), DEFAULT_CODEC_REGISTRY)
  val database = mongoClient.getDatabase("stanoq").withCodecRegistry(pageRegistry)
  val collection: MongoCollection[Page] = database.getCollection("crawler")

  def getAllWithLimit(limit: Int):List[EchartResponse] = Await.result(collection.find().limit(limit).toFuture(), Duration(10, TimeUnit.SECONDS)).map(root => toEchartResponse(root)).toList
  def toEchartResponse(page:Page):EchartResponse = {
    val echartRoot = page.parse
    EchartResponse(echartRoot.map(_._1),echartRoot.flatMap(_._2))
  }
  def getAll = getAllWithLimit(0)
  def getPage(url: String) = Await.result(collection.find(equal("value",url)).toFuture(),Duration(10, TimeUnit.SECONDS))
  def persist(page: Page) = Await.result(collection.insertOne(page).head(), Duration(10, TimeUnit.SECONDS))
  def deletePage(page: Page) = Await.result(collection.deleteOne(equal("url", page.url)).head(),Duration(10, TimeUnit.SECONDS))
}

package org.stanoq.crawler

import java.util.concurrent.TimeUnit

import com.typesafe.config.ConfigFactory
import org.mongodb.scala.{MongoClient, MongoCollection}
import org.stanoq.crawler.model._
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object MongoHelper {
  val config = ConfigFactory.load()
  val mongoClient: MongoClient = MongoClient(config.getString("mongo.url"))

  val responseRegistry = fromRegistries(fromProviders(classOf[CrawlerResponse],classOf[ConfigProperties],classOf[Node],classOf[EchartResponse],classOf[EchartNode],classOf[EchartLink]), DEFAULT_CODEC_REGISTRY)
  val database = mongoClient.getDatabase("stanoq").withCodecRegistry(responseRegistry)
  val collection: MongoCollection[CrawlerResponse] = database.getCollection("crawler")
  def filterResponse(config:ConfigProperties):Bson = and(equal("config.url", config.url),equal("config.depthLimit", config.depthLimit))
  def size = Await.result(collection.count().head(), Duration(10, TimeUnit.SECONDS)).toInt
  def getLatest:List[CrawlerResponse] = Await.result(collection.find().skip(size-1).toFuture(), Duration(10, TimeUnit.SECONDS)).toList
  def getAll(limit: Int) = Await.result(collection.find().limit(limit).toFuture(), Duration(10, TimeUnit.SECONDS)).toList
  def getResponse(url: String) = Await.result(collection.find(equal("config.url",url)).toFuture(),Duration(10, TimeUnit.SECONDS))
  def persist(response: CrawlerResponse) = Await.result(collection.insertOne(response).head(), Duration(10, TimeUnit.SECONDS))
  def deleteSite(config: ConfigProperties) = Await.result(collection.deleteOne(filterResponse(config)).head(),Duration(10, TimeUnit.SECONDS))
  def deleteAll() = Await.result(collection.drop().head(),Duration(10, TimeUnit.SECONDS))
}

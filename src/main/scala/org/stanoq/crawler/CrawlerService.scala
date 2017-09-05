package org.stanoq.crawler

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory
import org.mongodb.scala.{Document, MongoClient, MongoCollection}
import org.stanoq.crawler.model._
import spray.json._
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}

import scala.concurrent._
import scala.concurrent.Future
import scala.concurrent.duration.Duration

class CrawlerService() extends CrawlerProtocols {

  implicit val blockingDispatcher: ExecutionContext = ActorSystem().dispatchers.lookup("blocking-dispatcher")
  val config = ConfigFactory.load()
  val mongoClient: MongoClient = MongoClient(config.getString("mongo.url"))
  val nodeRegistry = fromRegistries(fromProviders(classOf[Node]), DEFAULT_CODEC_REGISTRY )
  val database = mongoClient.getDatabase("stanoq").withCodecRegistry(nodeRegistry)

  def handleCrawlerRequest(config: ConfigProperties) = {
    validate(config.validate, "Config wasn't properly set!") {
      complete {
        val crawler = new Crawler(config).process
        Future {
          val root: Node = crawler.root.convertToNode
          val crawlerEntity = HttpEntity(ContentType(MediaTypes.`application/json`), root.toJson.toString())
          HttpResponse(StatusCodes.OK, entity = crawlerEntity)
        }
      }
    }
  }

  def persist(node: Node) = {
    val collection: MongoCollection[Node] = database.getCollection("crawler")
    val res = Await.result(collection.insertOne(node).head(),Duration(10, TimeUnit.SECONDS))
    complete {
      println(res + " ::: "+node);
      HttpResponse(StatusCodes.Created)
    }
  }

  def getAll = {
    val collection: MongoCollection[Node] = database.getCollection("crawler")
    val res = Await.result(collection.find().toFuture(),Duration(10, TimeUnit.SECONDS))
    complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentType(MediaTypes.`application/json`), res.toList.toJson.toString())))
  }

  val route =
    pathPrefix("crawler") {pathEnd {(post & entity(as[ConfigProperties])) (handleCrawlerRequest)}}~
    pathPrefix("persist") {pathEnd {(post & entity(as[Node]))             (persist)}}~
    pathPrefix("getAll") {pathEnd  {(get)                                 (getAll)}}
}
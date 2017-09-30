package org.stanoq.crawler

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import org.stanoq.crawler.model._
import spray.json._

import scala.concurrent._
import scala.concurrent.Future

class CrawlerService() extends CrawlerProtocols {

  implicit val blockingDispatcher: ExecutionContext = ActorSystem().dispatchers.lookup("blocking-dispatcher")


  def handleCrawlerRequest(config: ConfigProperties) = {
    validate(config.validate, "Config wasn't properly set!") {
      complete {
        val crawler = new Crawler(config).process("/")
        Future {
          val root: Node = crawler.root.convertToNode
          val crawlerEntity = HttpEntity(ContentType(MediaTypes.`application/json`), root.toJson.toString())
          HttpResponse(StatusCodes.OK, entity = crawlerEntity)
        }
      }
    }
  }

  def getAll = complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentType(MediaTypes.`application/json`), MongoHelper.getAll.toList.toJson.toString())))
  def getAny = complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentType(MediaTypes.`application/json`), MongoHelper.getAllWithLimit(1).toList.toJson.toString())))
  def getPage(url:String) = complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentType(MediaTypes.`application/json`), MongoHelper.getPage(url).toList.toJson.toString())))
  def persist(page: Page) = {
    MongoHelper.persist(page)
    complete{HttpResponse(StatusCodes.Created)}
  }
  def deletePage(page:Page) = {
    MongoHelper.deletePage(page)
    complete{HttpResponse(StatusCodes.Gone)}
  }

  val route = pathPrefix("crawler") {pathEnd {
      (post & entity(as[ConfigProperties]))      (handleCrawlerRequest)}}~
    pathPrefix("node") {pathEnd {
        get                                      (getAny)~
        get {parameters('value.as[String])       (getPage)}~
        (delete & entity(as[Page]))              (deletePage)~
        (post & entity(as[Page]))                (persist)}}~
    pathPrefix("nodes")  {pathEnd {(get)         (getAll)}}
}
package org.stanoq.crawler

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
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
        val crawler = new Crawler(config).process
        Future {
          val root: Node = crawler.root.convertToNode
          val crawlerEntity = HttpEntity(ContentType(MediaTypes.`application/json`), root.toJson.toString())
          HttpResponse(StatusCodes.OK, entity = crawlerEntity)
        }
      }
    }
  }

  val route = pathPrefix("crawler") {
    pathEnd {
      (post & entity(as[ConfigProperties])) (handleCrawlerRequest)
    }
  }

}
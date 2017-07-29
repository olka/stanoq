package org.stanoq.crawler

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import org.stanoq.crawler.model.{ConfigProperties, CrawlerProtocols, CrawlerResponse}
import spray.json._
import scala.concurrent._

import scala.concurrent.Future

class CrawlerService(system: ActorSystem) extends CrawlerProtocols {

  implicit val blockingDispatcher:ExecutionContext = system.dispatchers.lookup("blocking-dispatcher")

  def handleCrawlerRequest(config: ConfigProperties) = {
    validate(config.validate, "Config wasn't properly set!") {
      complete {
        val crawler = new Crawler(system,config)
        Future {
          val statusCode = if (crawler.process) StatusCodes.OK else StatusCodes.FailedDependency
          val crawlerEntity = HttpEntity(ContentType(MediaTypes.`application/json`), CrawlerResponse(crawler.visitedPages.toSet, crawler.errorPages.toSet).toJson.toString())
          HttpResponse(statusCode, entity = crawlerEntity)
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
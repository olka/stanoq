package org.stanoq.crawler

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import org.stanoq.crawler.model._
import spray.json._

import scala.concurrent._
import scala.concurrent.Future

class CrawlerService(system: ActorSystem) extends CrawlerProtocols {

//  implicit val blockingDispatcher:ExecutionContext = system.dispatchers.lookup("blocking-dispatcher")

  def handleCrawlerRequest(config: ConfigProperties) = {
    validate(config.validate, "Config wasn't properly set!") {
      complete {
        val crawler = new Crawler(config)
//        Future {
          val statusCode = if (crawler.process) StatusCodes.OK else StatusCodes.FailedDependency
          val nodes = crawler.visitedPages.map{case(page,url) => Node(page.url,page.pageName,page.statusCode)}.toList
          val links = crawler.visitedPages.map{case(page,urk) => Link(urk,page.url)}.toList
          val crawlerEntity = HttpEntity(ContentType(MediaTypes.`application/json`), CrawlerResponse(nodes,links).toJson.toString())
          HttpResponse(statusCode, entity = crawlerEntity)
//        }
      }
    }
  }

  val route = pathPrefix("crawler") {
    pathEnd {
      (post & entity(as[ConfigProperties])) (handleCrawlerRequest)
    }
  }

}
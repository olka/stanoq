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

//  implicit val blockingDispatcher:ExecutionContext = system.dispatchers.lookup("blocking-dispatcher")

  def handleCrawlerRequest(config: ConfigProperties) = {
    validate(config.validate, "Config wasn't properly set!") {
      complete {
        val crawler = new Crawler(config).process
//        Future {
          def getId(url:String) = Math.abs(url.hashCode).toString
          val nodes = crawler.visitedPages.map{case(page,url) => Node(getId(page.url),page.pageName.trim,page.statusCode)}.toList
          val links = crawler.visitedPages.map{case(page,url) => Link(getId(url),getId(page.url))}.toList
          val crawlerEntity = HttpEntity(ContentType(MediaTypes.`application/json`), CrawlerResponse(nodes,links).toJson.toString())
          HttpResponse(StatusCodes.OK, entity = crawlerEntity)
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
package org.stanoq.crawler

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import org.stanoq.crawler.model.{ConfigProperties, CrawlerResponse, CrawlerProtocols}
import spray.json._

class CrawlerService extends CrawlerProtocols {
  def handleCrawlerRequest(config: ConfigProperties) = {
    validate(config.validate, "Config wasn't properly set!") {
      complete {
        val crawler = new Crawler(config)
        val statusCode = if (crawler.process) StatusCodes.OK else StatusCodes.FailedDependency
        val crawlerEntity = HttpEntity(ContentType(MediaTypes.`application/json`), CrawlerResponse(crawler.visitedPages.toSet, crawler.errorPages.toSet).toJson.toString())
        HttpResponse(statusCode, entity = crawlerEntity)
      }
    }
  }

  val route = pathPrefix("crawler") {
    pathEnd {
      (post & entity(as[ConfigProperties])) (handleCrawlerRequest)
    }
  }

}
package org.stanoq

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.stanoq.crawler.CrawlerService
import org.stanoq.version.org.stanoq.crawler.VersionService
import akka.http.scaladsl.server.Directives._


object RestController extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)
  val crawlerService = new CrawlerService
  val versionService = new VersionService
  val routes = crawlerService.route ~ versionService.route


  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}

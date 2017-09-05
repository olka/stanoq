package org.stanoq

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.stanoq.crawler.CrawlerService
import org.stanoq.stream.StreamService
import org.stanoq.version.org.stanoq.crawler.VersionService

object RestController extends App with CorsSupport {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val log = system.log

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)
  val crawlerService = new CrawlerService
  val versionService = new VersionService
  val streamService = new StreamService
  val angularRoute = pathPrefix("") {getFromResourceDirectory("webapp/dist") ~ getFromResource("webapp/dist/index.html")}
  val debug = pathPrefix("debug") { getFromBrowseableDirectories(".")}

  val routes = crawlerService.route ~ versionService.route ~ streamService.route ~ angularRoute ~ debug
  val loggedRoutes = DebuggingDirectives.logRequestResult("INFO:", Logging.InfoLevel)(routes)

  val bindingFuture = Http().bindAndHandle(corsHandler(routes), config.getString("http.interface"), config.getInt("http.port"))
  bindingFuture.map(_.localAddress).map(addr => s"Bound to $addr").foreach(log.info)
  sys.addShutdownHook(system.terminate())
}

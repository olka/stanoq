package org.stanoq.version


package org.stanoq.crawler

import akka.http.scaladsl.model.{HttpEntity, _}
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory

class VersionService {
  val version  = ConfigFactory.load().getString("stanoq.version")
  val route = pathPrefix("version") {
      pathEnd {
        get {
          println(ConfigFactory.load().getString("mongo.url"))
          complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentType(MediaTypes.`application/json`), s"""{"version": $version}""")))
        }
      }
    }
}
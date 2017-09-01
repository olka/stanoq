package org.stanoq.stream
import akka.actor.ActorSystem
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.server.Directives._
import akka.stream.ThrottleMode
import akka.stream.impl.Stages.DefaultAttributes
import akka.stream.scaladsl.{Flow, Source}
import spray.json._

import scala.concurrent.duration._
import org.stanoq.crawler.Crawler
import org.stanoq.crawler.model._

import scala.concurrent.{ExecutionContext, Future}

class StreamService extends CrawlerProtocols{

  implicit val blockingDispatcher: ExecutionContext = ActorSystem().dispatchers.lookup("blocking-dispatcher")
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json().withParallelMarshalling(parallelism = 8, unordered = false)

  def getResponse(config:ConfigProperties) = {
    val crawler = new Crawler(config)
    Future {crawler.process}
    def pageRoot = crawler.root
    def echartRoot = pageRoot.parse
    def node = pageRoot.convertToNode
    val source = {
      def response = CrawlerResponse(node,(echartRoot.map(_._1),echartRoot.flatMap(_._2)))
      def next(node: CrawlerResponse) = if (pageRoot.statusCode == 200) None else Some((response, response))
      Source.unfold(response)(next).withAttributes(DefaultAttributes.delayInitial)
    }
    complete(source.via(getThrottlingFlow[CrawlerResponse]))
  }

  def getThrottlingFlow[T] = Flow[T].throttle(elements = 1, per = 600.millis, maximumBurst = 0, mode = ThrottleMode.Shaping)

  val route =
    pathPrefix("crawlerStream") {
      pathEnd {
        (post & entity(as[ConfigProperties])) (getResponse)
      }
    }
}
package org.stanoq.crawler

import akka.actor.ActorSystem
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.server.Directives._
import akka.stream.ThrottleMode
import akka.stream.impl.Stages.DefaultAttributes
import akka.stream.scaladsl.{Flow, Source}
import org.stanoq.crawler.model._
import spray.json._
import MongoHelper._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class StreamService extends CrawlerProtocols{

  implicit val blockingDispatcher: ExecutionContext = ActorSystem().dispatchers.lookup("blocking-dispatcher")
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json().withParallelMarshalling(parallelism = 8, unordered = false)


  def getResponse(config:ConfigProperties) = {
    val crawler = new Crawler(config)
    Future {crawler.process("/")}
    def pageRoot = crawler.root
    def properRoot = if(pageRoot.children.size>0)pageRoot.children.head else pageRoot
    def echartRoot = properRoot.parse
    def node = properRoot.convertToNode
    val source = {
      def response = CrawlerResponse(node,EchartResponse(echartRoot.map(_._1),echartRoot.flatMap(_._2)))
      def next(node: CrawlerResponse) = {
        if (node == null) {persist(response.echart); None}
        else if (pageRoot.statusCode == 200) {println(response.toJson.toString);Some((null, response))}
        else Some((response, response))
      }
      Source.unfold(response)(next).withAttributes(DefaultAttributes.buffer)
    }
    encodeResponse(complete(source.via(getThrottlingFlow[CrawlerResponse])))
  }

  def getThrottlingFlow[T] = Flow[T].throttle(1, per = 450.millis, 1, ThrottleMode.shaping)

  val route =
    pathPrefix("crawlerStream") {
      pathEnd {
        (post & entity(as[ConfigProperties])) (getResponse)
      }
    }
}
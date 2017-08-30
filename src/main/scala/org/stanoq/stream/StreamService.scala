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
import org.stanoq.crawler.model.{ConfigProperties, CrawlerProtocols, EchartResponse, Node}

import scala.concurrent.{ExecutionContext, Future}

class StreamService extends CrawlerProtocols{

  implicit val blockingDispatcher: ExecutionContext = ActorSystem().dispatchers.lookup("blocking-dispatcher")
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json().withParallelMarshalling(parallelism = 8, unordered = false)

  def getNodes(config:ConfigProperties) = {
    def pageRoot = getRoot(config)
    def root = pageRoot.convertToNode
    val source = {
      def next(node: Node) = if (pageRoot.statusCode == 200) None else Some((root, root))
      Source.unfold(root)(next).withAttributes(DefaultAttributes.delayInitial)
    }
    complete(source.via(getThrottlingFlow[Node]))
  }

  def getEchartNodes(config:ConfigProperties) = {
    def pageRoot = getRoot(config)
    def root = getRoot(config).parse
    val source = {
      def echartResp = EchartResponse(root.map(_._1),root.flatMap(_._2))
      def next(node:EchartResponse) = if(pageRoot.statusCode == 200) {println(echartResp.toJson.toString());None} else Some((echartResp,echartResp))
      Source.unfold(echartResp)(next)
    }
    complete(source.via(getThrottlingFlow[EchartResponse]))
  }

  def getRoot(config:ConfigProperties) =  {
    val crawler =  new Crawler(config)
    Future{crawler.process}
    crawler.root
  }
  def getThrottlingFlow[T] = Flow[T].throttle(elements = 1, per = 200.millis, maximumBurst = 0, mode = ThrottleMode.Shaping)

  val route =
    pathPrefix("crawlerStream") {
      pathEnd {
        (post & entity(as[ConfigProperties])) (getNodes)
      }
    }~
      pathPrefix("crawlerStreamEchart") {
        pathEnd {
          (post & entity(as[ConfigProperties])) (getEchartNodes)
        }
      }
}
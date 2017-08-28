package org.stanoq.stream
import akka.actor.{ActorSystem}
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.server.Directives._
import akka.stream.ThrottleMode
import akka.stream.impl.Stages.DefaultAttributes
import akka.stream.scaladsl.{Flow, Source}

import scala.concurrent.duration._
import org.stanoq.crawler.Crawler
import org.stanoq.crawler.model.{ConfigProperties, CrawlerProtocols, Node}

import scala.concurrent.{ExecutionContext, Future}

class StreamService extends CrawlerProtocols{

  implicit val blockingDispatcher: ExecutionContext = ActorSystem().dispatchers.lookup("blocking-dispatcher")
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json().withParallelMarshalling(parallelism = 8, unordered = false)

  def getNodes(config:ConfigProperties) = {
    val crawler =  new Crawler(config)
    Future{crawler.process}
    def pageRoot = crawler.root
    def root = pageRoot.convertToNode
    val source = {
      def next(node:Node) = if(pageRoot.statusCode == 200) None else Some((root, root))
      Source.unfold(root)(next).withAttributes(DefaultAttributes.delayInitial)
    }

    val throttlingFlow = Flow[Node].throttle(
      elements = 1,
      per = 200.millis,
      maximumBurst = 0,
      mode = ThrottleMode.Shaping
    )
    complete(source.via(throttlingFlow))
  }

  val route = pathPrefix("crawlerStream") {
      pathEnd {
        (post & entity(as[ConfigProperties])) (getNodes)
      }
    }

//
//  val AcceptJson = Accept(MediaRange(MediaTypes.`application/json`))
//  val AcceptXml = Accept(MediaRange(MediaTypes.`text/xml`))
//
//  Get("/tweets").withHeaders(AcceptJson) ~> route ~> check {
//    responseAs[String] shouldEqual
//      """[""" +
//        """{"uid":1,"txt":"#Akka rocks!"},""" +
//        """{"uid":2,"txt":"Streaming is so hot right now!"},""" +
//        """{"uid":3,"txt":"You cannot enter the same river twice."}""" +
//        """]"""
//  }

}
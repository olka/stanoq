package org.stanoq.load.tests

import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

object GatlingRunner extends App {

  val props = new GatlingPropertiesBuilder
  props.simulationClass("org.stanoq.load.tests.LoadTest")
  Gatling.fromMap(props.build)
}

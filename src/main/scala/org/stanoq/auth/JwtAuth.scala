package org.stanoq.auth

import pdi.jwt.{Jwt, JwtAlgorithm}

/**
  * Jwt helper class
  * @param payload @Json object converted to @String
  * @param secret Combination of url and depth. This is UUID of crawling result
  */
case class JwtAuth(payload:String, secret:String){
  def decode = Jwt.decodeRawAll(payload, secret, Seq(JwtAlgorithm.HS256))
  def encode = Jwt.encode(payload, secret, JwtAlgorithm.HS256)
}

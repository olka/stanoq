package org.stanoq.auth

import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtHeader, JwtOptions}

case class JwtAuth(payload:String, secret:String){
  def decode = Jwt.decodeRawAll(payload, secret, Seq(JwtAlgorithm.HS256))
  def encode = Jwt.encode(payload, secret, JwtAlgorithm.HS256)
}

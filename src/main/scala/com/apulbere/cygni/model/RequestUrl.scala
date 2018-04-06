package com.apulbere.cygni.model

import java.util.UUID

import com.apulbere.cygni.util.ParameterResolver

case class RequestUrl(ip: String, port: Int, prefix: String) {
  override def toString = s"http://$ip:$port/$prefix"
}

object RequestUrl {
  def apply(parameterResolver: ParameterResolver): RequestUrl = {
    val ip = parameterResolver.get("ip").getOrElse("localhost")
    val port = parameterResolver.get("port").map(_.toInt).getOrElse(8080)
    new RequestUrl(ip, port, UUID.randomUUID().toString)
  }
}

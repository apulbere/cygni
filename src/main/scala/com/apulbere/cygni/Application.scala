package com.apulbere.cygni

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.apulbere.cygni.util.{ParameterResolver, QrCodeGenerator}

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Application {
  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def main(args: Array[String]): Unit = {
    val filePath = args.lift(0).getOrElse(throw new IllegalArgumentException("Path to the file is not provided"))
    val fileName = Paths.get(filePath).getFileName

    val route =
      pathSingleSlash {
        respondWithHeader(RawHeader("Content-Disposition", s"inline; filename=$fileName")) {
          getFromFile(filePath)
        }
      }

    val parameterResolver = ParameterResolver(args)
    val port = parameterResolver.get("port").map(_.toInt).getOrElse(8080)
    val ip = parameterResolver.get("ip").getOrElse("localhost")
    val bindingFuture = Http().bindAndHandle(route, ip, port)

    val fileUrl = s"http://$ip:$port/"

    displayInfoToConsole(fileUrl)
    println("Press RETURN to stop...")

    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  def displayInfoToConsole(fileUrl: String): Unit = {
    println(QrCodeGenerator.from(fileUrl))
    println(fileUrl)
  }
}
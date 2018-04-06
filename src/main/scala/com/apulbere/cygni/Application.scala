package com.apulbere.cygni

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.apulbere.cygni.model.RequestUrl
import com.apulbere.cygni.util.{ParameterResolver, QrCodePrinter}

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Application {
  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def main(args: Array[String]): Unit = {
    val filePath = args.lift(0).getOrElse(throw new IllegalArgumentException("Path to the file is not provided"))
    val fileName = Paths.get(filePath).getFileName
    val requestUrl = RequestUrl(ParameterResolver(args))

    val route =
      pathPrefix(requestUrl.prefix) {
        respondWithHeader(RawHeader("Content-Disposition", s"inline; filename=$fileName")) {
          getFromFile(filePath)
        }
      }

    val bindingFuture = Http().bindAndHandle(route, requestUrl.ip, requestUrl.port)

    displayInfoToConsole(requestUrl)

    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  def displayInfoToConsole(requestUrl: RequestUrl): Unit = {
    val fileUrl = requestUrl.toString
    QrCodePrinter.print(fileUrl)
    println(fileUrl)
    println("Press RETURN to stop...")
  }
}
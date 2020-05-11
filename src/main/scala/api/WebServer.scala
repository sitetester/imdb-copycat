package api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import api.route.RouteProvider

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object WebServer {

  val port = 8080

  def main(args: Array[String]) {

    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    val bindingFuture = Http().bindAndHandle(RouteProvider.getRoute, "localhost", port)

    println(s"Server online at http://localhost:$port/\nPress RETURN to stop...")

    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}

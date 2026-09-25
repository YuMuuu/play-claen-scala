package dev.yumuuu.playclean.bootstrap

import dev.yumuuu.playclean.presentation.http.ApiRoutes
import jakarta.annotation.{PostConstruct, PreDestroy}
import jakarta.inject.{Inject, Singleton}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.scaladsl.Http.ServerBinding
import org.apache.pekko.http.scaladsl.Http

import scala.concurrent.Await
import scala.concurrent.duration.*

@Singleton
class PekkoHttpServer @Inject() (
    config: AppConfig,
    routes: ApiRoutes,
    actorSystem: ActorSystem[Nothing]
) {
  given ActorSystem[Nothing] = actorSystem

  lazy val serverBinding: ServerBinding =
    Await.result(
      Http().newServerAt(config.server.host, config.server.port).bind(routes.route),
      30.seconds
    )

  @PostConstruct
  def start(): Unit =
    val _ = serverBinding

  @PreDestroy
  def stop(): Unit = {
    val _ = Await.result(
      serverBinding.terminate(config.server.gracefulShutdownTimeout),
      15.seconds
    )
  }
}

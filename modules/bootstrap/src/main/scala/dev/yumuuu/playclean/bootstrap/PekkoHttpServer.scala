package dev.yumuuu.playclean.bootstrap

import dev.yumuuu.playclean.presentation.http.ApiRoutes
import jakarta.annotation.{PostConstruct, PreDestroy}
import jakarta.inject.{Inject, Singleton}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.scaladsl.Http.ServerBinding
import org.apache.pekko.http.scaladsl.HttpExt

import scala.concurrent.Await
import scala.concurrent.duration.*

@Singleton
class PekkoHttpServer @Inject() (
    config: AppConfig,
    routes: ApiRoutes,
    http: HttpExt
)(using ActorSystem[Nothing]) {
  private var binding: Option[ServerBinding] = None

  @PostConstruct
  def start(): Unit =
    binding = Some(
      Await.result(
        http.newServerAt(config.server.host, config.server.port).bind(routes.route),
        30.seconds
      )
    )

  @PreDestroy
  def stop(): Unit =
    binding.foreach { serverBinding =>
      val _ =
        Await.result(serverBinding.terminate(config.server.gracefulShutdownTimeout), 15.seconds)
    }
}

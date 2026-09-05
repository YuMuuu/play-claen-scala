package dev.yumuuu.playclean.bootstrap

import cats.effect.IO
import dev.yumuuu.playclean.application.auth.AuthenticatedUser
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.{Disposes, Produces, Typed}
import jakarta.inject.{Named, Singleton}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.{Http, HttpExt}
import org.typelevel.doobie.Transactor
import pureconfig.ConfigSource

import scala.concurrent.Await
import scala.concurrent.duration.*

@ApplicationScoped
class AppModule {
  @Produces
  @Singleton
  def pekkoHttp(using ActorSystem[Nothing]): HttpExt = Http()

  @Produces
  @Singleton
  @Typed(Array(classOf[ActorSystem[Nothing]]))
  def actorSystem(): ActorSystem[Nothing] =
    ActorSystem[Nothing](Behaviors.empty, "pekko-http")

  def disposeActorSystem(@Disposes actorSystem: ActorSystem[Nothing]): Unit =
    actorSystem.terminate()
    val _ = Await.result(actorSystem.whenTerminated, 10.seconds)

  @Produces
  @Singleton
  def appConfig(): AppConfig =
    ConfigSource.default.at("app").loadOrThrow[AppConfig]

  @Produces
  @Singleton
  def transactor(config: AppConfig): Transactor[IO] =
    Transactor.fromDriverManager[IO](
      driver = "org.postgresql.Driver",
      url = config.database.url,
      user = config.database.user,
      password = config.database.password,
      logHandler = None
    )

  @Produces
  @Named("mockAccessToken")
  def mockAccessToken(config: AppConfig): String =
    config.mockAuth.accessToken

  @Produces
  def authenticatedUser(config: AppConfig): AuthenticatedUser =
    AuthenticatedUser(config.mockAuth.userId, config.mockAuth.userName)
}

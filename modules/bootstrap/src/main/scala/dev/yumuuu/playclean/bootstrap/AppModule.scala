package dev.yumuuu.playclean.bootstrap

import cats.effect.IO
import com.typesafe.config.{Config, ConfigFactory}
import com.zaxxer.hikari.HikariConfig
import dev.yumuuu.playclean.application.auth.AuthenticatedUser
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.{Produces, Typed}
import jakarta.inject.{Named, Singleton}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.scaladsl.Http.ServerBinding
import org.typelevel.doobie.Transactor
import pureconfig.ConfigSource

import scala.concurrent.ExecutionContextExecutorService

@ApplicationScoped
class AppModule {
  @Produces
  @Singleton
  def environment(): Environment =
    Environment(
      Option(Thread.currentThread().getContextClassLoader)
        .getOrElse(classOf[AppModule].getClassLoader)
    )

  @Produces
  @Singleton
  def configuration(environment: Environment): Config =
    ConfigFactory.load(environment.classLoader)

  @Produces
  @Singleton
  def appConfig(): AppConfig =
    ConfigSource.default.at("app").loadOrThrow[AppConfig]

  @Produces
  @Singleton
  def hikariConfig(config: AppConfig): HikariConfig = {
    val hikariConfig = new HikariConfig()
    hikariConfig.setDriverClassName("org.postgresql.Driver")
    hikariConfig.setJdbcUrl(config.database.url)
    hikariConfig.setUsername(config.database.user)
    hikariConfig.setPassword(config.database.password)
    hikariConfig.validate()
    hikariConfig
  }

  @Produces
  @Singleton
  def transactor(managedTransactor: ManagedTransactor): Transactor[IO] =
    managedTransactor.transactor

  @Produces
  @Singleton
  def executionContextExecutorService(
      managedExecutionContext: DoobieTransactorExecutionContextExecutorService
  ): ExecutionContextExecutorService =
    managedExecutionContext.executionContextExecutorService

  @Produces
  @Singleton
  @Typed(Array(classOf[ActorSystem[Nothing]]))
  def actorSystem(managedActorSystem: ManagedActorSystem): ActorSystem[Nothing] =
    managedActorSystem.actorSystem

  @Produces
  @Singleton
  def serverBinding(pekkoHttpServer: PekkoHttpServer): ServerBinding =
    pekkoHttpServer.serverBinding

  @Produces
  @Named("mockAccessToken")
  def mockAccessToken(config: AppConfig): String =
    config.mockAuth.accessToken

  @Produces
  def authenticatedUser(config: AppConfig): AuthenticatedUser =
    AuthenticatedUser(config.mockAuth.userId, config.mockAuth.userName)
}

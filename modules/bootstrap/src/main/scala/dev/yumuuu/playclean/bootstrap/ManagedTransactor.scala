package dev.yumuuu.playclean.bootstrap

import cats.effect.IO
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import jakarta.annotation.{PostConstruct, PreDestroy}
import jakarta.inject.{Inject, Singleton}
import org.typelevel.doobie.Transactor
import org.typelevel.doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContextExecutorService

@Singleton
class ManagedTransactor @Inject() (
    hikariConfig: HikariConfig,
    connectExecutionContext: ExecutionContextExecutorService
) {
  private lazy val dataSource = new HikariDataSource(hikariConfig)

  lazy val transactor: Transactor[IO] =
    HikariTransactor[IO](dataSource, connectExecutionContext)

  @PostConstruct
  def start(): Unit =
    val _ = transactor

  @PreDestroy
  def stop(): Unit =
    dataSource.close()
}

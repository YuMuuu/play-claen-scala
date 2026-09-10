package dev.yumuuu.playclean.bootstrap

import com.zaxxer.hikari.HikariConfig
import jakarta.annotation.{PostConstruct, PreDestroy}
import jakarta.inject.{Inject, Singleton}

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

@Singleton
class DoobieTransactorExecutionContextExecutorService @Inject() (
    hikariConfig: HikariConfig
) {
  lazy val executionContextExecutorService: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(
      Executors.newFixedThreadPool(hikariConfig.getMaximumPoolSize)
    )

  @PostConstruct
  def start(): Unit =
    val _ = executionContextExecutorService

  @PreDestroy
  def stop(): Unit =
    executionContextExecutorService.shutdown()
}

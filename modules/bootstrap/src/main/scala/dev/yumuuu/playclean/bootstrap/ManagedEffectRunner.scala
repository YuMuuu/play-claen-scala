package dev.yumuuu.playclean.bootstrap

import cats.effect.IO
import cats.effect.std.Dispatcher
import cats.effect.unsafe.implicits.global
import dev.yumuuu.playclean.presentation.http.EffectRunner
import jakarta.annotation.PreDestroy
import jakarta.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class ManagedEffectRunner @Inject() () extends EffectRunner {
  private val (dispatcher, release) = Dispatcher.parallel[IO].allocated.unsafeRunSync()

  override def toFuture[A](effect: IO[A]): Future[A] =
    dispatcher.unsafeToFuture(effect)

  @PreDestroy
  def close(): Unit = {
    val wasInterrupted = Thread.interrupted()
    try release.unsafeRunSync()
    finally if wasInterrupted then Thread.currentThread().interrupt()
  }
}

package dev.yumuuu.playclean.presentation.http

import cats.effect.IO
import scala.concurrent.Future

trait EffectRunner {
  def toFuture[A](effect: IO[A]): Future[A]
}

package dev.yumuuu.playclean.usecase

import cats.effect.IO

trait UseCase[Input, Output] {
  def handle(input: Input): IO[Output]
}

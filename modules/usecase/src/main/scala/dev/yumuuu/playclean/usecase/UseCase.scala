package dev.yumuuu.playclean.usecase

import cats.effect.IO

/** 以下のようにした方がより厳密に型を定義できる？？ type UserResult[A] = Either[UserApplicationError, A]
  *
  * trait UseCase[Input, M[_]: Functor, Model, View] { protected def execute(input: Input):
  * IO[M[Model]] protected def toView(model: Model): View
  *
  * final def handle(input: Input): IO[M[View]] = execute(input).map(_.map(toView)) }
  */

trait UseCase[Input, Output] {
  def handle(input: Input): IO[Output]
}

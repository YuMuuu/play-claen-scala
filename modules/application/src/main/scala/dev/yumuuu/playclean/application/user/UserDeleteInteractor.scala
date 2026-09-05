package dev.yumuuu.playclean.application.user

import cats.effect.IO
import cats.syntax.all.*
import dev.yumuuu.playclean.domain.user.{UserId, UserRepository}
import dev.yumuuu.playclean.usecase.user.{UserApplicationError, UserDeleteUseCase}
import jakarta.inject.{Inject, Singleton}

@Singleton
class UserDeleteInteractor @Inject() (
    repository: UserRepository
) extends UserDeleteUseCase {
  override def handle(id: String): IO[Either[UserApplicationError, Unit]] =
    repository
      .delete(UserId.fromString(id))
      .map(_.leftMap(UserApplicationSupport.toApplicationError).void)
}

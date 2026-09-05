package dev.yumuuu.playclean.application.user

import cats.effect.IO
import cats.data.EitherT
import dev.yumuuu.playclean.domain.user.{UserId, UserRepository}
import dev.yumuuu.playclean.usecase.user.{UserApplicationError, UserGetDetailUseCase, UserView}
import jakarta.inject.{Inject, Singleton}

@Singleton
class UserGetDetailInteractor @Inject() (
    repository: UserRepository
) extends UserGetDetailUseCase {
  override def handle(id: String): IO[Either[UserApplicationError, UserView]] =
    (
      for {
        maybeUser <- EitherT(repository.find(UserId.fromString(id)))
          .leftMap(UserApplicationSupport.toApplicationError)
        user <- EitherT.fromOption[IO](maybeUser, UserApplicationError.NotFound(id))
      } yield UserApplicationSupport.toView(user)
    ).value
}

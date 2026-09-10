package dev.yumuuu.playclean.application.user

import cats.effect.IO
import cats.data.EitherT
import dev.yumuuu.playclean.domain.user.{UserId, UserRepository}
import dev.yumuuu.playclean.usecase.user.{UpdateUserCommand, UserApplicationError, UserUpdateUseCase, UserView}
import jakarta.inject.{Inject, Singleton}

@Singleton
class UserUpdateInteractor @Inject() (
    repository: UserRepository
) extends UserUpdateUseCase {
  override def handle(command: UpdateUserCommand): IO[Either[UserApplicationError, UserView]] =
    (
      for {
        name <- EitherT.fromEither[IO](UserApplicationSupport.validateName(command.name))
        maybeUser <- EitherT(repository.find(UserId.from(command.id)))
          .leftMap(UserApplicationSupport.toApplicationError)
        user <- EitherT.fromOption[IO](maybeUser, UserApplicationError.NotFound(command.id))
        updatedUser = user.changeName(name)
        updated <- EitherT(repository.update(updatedUser))
          .leftMap(UserApplicationSupport.toApplicationError)
        _ <- EitherT.cond[IO](updated, (), UserApplicationError.NotFound(command.id))
      } yield UserApplicationSupport.toView(updatedUser)
    ).value
}

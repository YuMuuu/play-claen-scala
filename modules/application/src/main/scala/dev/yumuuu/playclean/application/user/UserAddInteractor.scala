package dev.yumuuu.playclean.application.user

import cats.effect.IO
import cats.data.EitherT
import dev.yumuuu.playclean.domain.user.{User, UserId, UserRepository}
import dev.yumuuu.playclean.usecase.user.{CreateUserCommand, UserAddUseCase, UserApplicationError, UserView}
import jakarta.inject.{Inject, Singleton}
import java.util.UUID

@Singleton
class UserAddInteractor @Inject() (
    repository: UserRepository
) extends UserAddUseCase {
  override def handle(command: CreateUserCommand): IO[Either[UserApplicationError, UserView]] =
    (
      for {
        validated <- EitherT.fromEither[IO](
          UserApplicationSupport.validateUser(command.name, command.role)
        )
        (name, role) = validated
        id <- EitherT.liftF(IO.delay(UserId.fromString(UUID.randomUUID().toString)))
        user = User.create(id, name, role)
        _ <- EitherT(repository.insert(user))
          .leftMap(UserApplicationSupport.toApplicationError)
      } yield UserApplicationSupport.toView(user)
    ).value
}

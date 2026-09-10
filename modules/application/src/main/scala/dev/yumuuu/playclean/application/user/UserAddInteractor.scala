package dev.yumuuu.playclean.application.user

import cats.effect.IO
import cats.data.EitherT
import dev.yumuuu.playclean.domain.user.{User, UserId, UserRepository}
import dev.yumuuu.playclean.usecase.user.{
  CreateUserCommand,
  UserAddUseCase,
  UserApplicationError,
  UserView
}
import jakarta.inject.{Inject, Singleton}
import java.util.UUID

@Singleton
class UserAddInteractor @Inject() (
    repository: UserRepository
) extends UserAddUseCase {
  override def handle(command: CreateUserCommand): IO[Either[UserApplicationError, UserView]] =
    (
      for {
        (name, role) <- EitherT.fromEither[IO](
          UserApplicationSupport.validateUser(command.name, command.role)
        )
        id <- EitherT.liftF(IO.delay(UserId.from(UUID.randomUUID().toString)))
        // サンプルコードなのでInteractorでIDを生成しているが、実際はUUIDGenaratorを作成してInjectして利用する
        user = User.create(id, name, role)
        _ <- EitherT(repository.insert(user))
          .leftMap(UserApplicationSupport.toApplicationError)
      } yield UserApplicationSupport.toView(user)
    ).value
}

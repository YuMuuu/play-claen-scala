package dev.yumuuu.playclean.application.user

import cats.effect.IO
import cats.syntax.either.*
import dev.yumuuu.playclean.domain.user.UserRepository
import dev.yumuuu.playclean.usecase.user.{UserApplicationError, UserGetListUseCase, UserView}
import jakarta.inject.{Inject, Singleton}

@Singleton
class UserGetListInteractor @Inject() (
    repository: UserRepository
) extends UserGetListUseCase {
  override def handle(input: Unit): IO[Either[UserApplicationError, List[UserView]]] =
    repository.findAll.map(
      _.bimap(UserApplicationSupport.toApplicationError, _.map(UserApplicationSupport.toView))
    )
}

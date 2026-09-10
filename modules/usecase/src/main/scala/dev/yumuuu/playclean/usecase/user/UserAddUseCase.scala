package dev.yumuuu.playclean.usecase.user

import dev.yumuuu.playclean.usecase.UseCase

trait UserAddUseCase extends UseCase[CreateUserCommand, Either[UserApplicationError, UserView]]

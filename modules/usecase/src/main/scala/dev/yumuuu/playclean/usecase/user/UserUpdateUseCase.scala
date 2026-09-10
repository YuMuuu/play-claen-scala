package dev.yumuuu.playclean.usecase.user

import dev.yumuuu.playclean.usecase.UseCase

trait UserUpdateUseCase extends UseCase[UpdateUserCommand, Either[UserApplicationError, UserView]]

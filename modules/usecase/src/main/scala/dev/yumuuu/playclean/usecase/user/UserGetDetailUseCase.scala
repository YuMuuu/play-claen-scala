package dev.yumuuu.playclean.usecase.user

import dev.yumuuu.playclean.usecase.UseCase

trait UserGetDetailUseCase extends UseCase[String, Either[UserApplicationError, UserView]]

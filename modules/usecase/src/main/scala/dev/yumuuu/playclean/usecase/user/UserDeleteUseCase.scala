package dev.yumuuu.playclean.usecase.user

import dev.yumuuu.playclean.usecase.UseCase

trait UserDeleteUseCase extends UseCase[String, Either[UserApplicationError, Unit]]

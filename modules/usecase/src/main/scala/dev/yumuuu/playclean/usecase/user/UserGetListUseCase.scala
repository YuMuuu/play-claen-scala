package dev.yumuuu.playclean.usecase.user

import dev.yumuuu.playclean.usecase.UseCase

trait UserGetListUseCase extends UseCase[Unit, Either[UserApplicationError, List[UserView]]]

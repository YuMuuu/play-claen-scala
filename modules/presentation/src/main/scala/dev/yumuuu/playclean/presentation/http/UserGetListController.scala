package dev.yumuuu.playclean.presentation.http

import dev.yumuuu.playclean.usecase.user.UserGetListUseCase
import jakarta.inject.{Inject, Singleton}
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server.Route

@Singleton
class UserGetListController @Inject() (
    useCase: UserGetListUseCase,
    protected val effectRunner: EffectRunner
) extends UserControllerSupport {
  val route: Route =
    get {
      completeEffect(useCase.handle(())) {
        case Right(result) => jsonResponse(StatusCodes.OK, JsonModels.usersJson(result))
        case Left(error)   => userErrorResponse(error)
      }
    }
}

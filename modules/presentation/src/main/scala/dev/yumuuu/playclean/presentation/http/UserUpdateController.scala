package dev.yumuuu.playclean.presentation.http

import dev.yumuuu.playclean.usecase.user.{UpdateUserCommand, UserUpdateUseCase}
import io.circe.syntax.*
import jakarta.inject.{Inject, Singleton}
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server.Route

@Singleton
class UserUpdateController @Inject() (
    useCase: UserUpdateUseCase,
    protected val effectRunner: EffectRunner
) extends UserControllerSupport {
  import JsonModels.given

  def route(id: String): Route =
    put {
      decodeEntity[UpdateUserRequest] { request =>
        completeEffect(useCase.handle(UpdateUserCommand(id, request.name))) {
          case Right(result) => jsonResponse(StatusCodes.OK, result.asJson)
          case Left(error)   => userErrorResponse(error)
        }
      }
    }
}

package dev.yumuuu.playclean.presentation.http

import dev.yumuuu.playclean.usecase.user.{CreateUserCommand, UserAddUseCase}
import io.circe.syntax.*
import jakarta.inject.{Inject, Singleton}
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server.Route

@Singleton
class UserAddController @Inject() (
    useCase: UserAddUseCase,
    protected val effectRunner: EffectRunner
) extends UserControllerSupport {
  import JsonModels.given

  val route: Route =
    post {
      decodeEntity[CreateUserRequest] { request =>
        completeEffect(useCase.handle(CreateUserCommand(request.name, request.role))) {
          case Right(result) => jsonResponse(StatusCodes.Created, result.asJson)
          case Left(error)   => userErrorResponse(error)
        }
      }
    }
}

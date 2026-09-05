package dev.yumuuu.playclean.presentation.http

import dev.yumuuu.playclean.application.auth.{AuthError, AuthenticatedUser, LoginCommand, MockAuthService}
import io.circe.{Decoder, Encoder, Json}
import io.circe.parser.decode
import io.circe.syntax.*
import jakarta.inject.{Inject, Singleton}
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCode, StatusCodes}
import org.apache.pekko.http.scaladsl.server.{Directives, Route}

@Singleton
class ApiRoutes @Inject() (
    userAddController: UserAddController,
    userDeleteController: UserDeleteController,
    userGetDetailController: UserGetDetailController,
    userGetListController: UserGetListController,
    userUpdateController: UserUpdateController,
    auth: MockAuthService
) extends Directives {
  import JsonModels.given

  val route: Route =
    pathPrefix("api") {
      concat(
        path("health") {
          get {
            complete(jsonResponse(StatusCodes.OK, Json.obj("status" -> Json.fromString("ok"))))
          }
        },
        authRoutes,
        userRoutes
      )
    }

  private val authRoutes: Route =
    pathPrefix("auth") {
      concat(
        path("login") {
          post {
            decodeEntity[LoginRequest] { request =>
              auth.login(LoginCommand(request.id, request.password)) match {
                case Right(result) => completeJson(StatusCodes.OK, result)
                case Left(error)   => complete(authErrorResponse(error))
              }
            }
          }
        },
        path("me") {
          get {
            authenticated((_, user) => completeJson(StatusCodes.OK, user))
          }
        },
        path("logout") {
          post {
            authenticated { (token, _) =>
              auth.logout(token) match {
                case Right(())    => complete(HttpResponse(StatusCodes.NoContent))
                case Left(error)  => complete(authErrorResponse(error))
              }
            }
          }
        }
      )
    }

  private val userRoutes: Route =
    pathPrefix("users") {
      concat(
        pathEndOrSingleSlash {
          concat(
            userGetListController.route,
            userAddController.route
          )
        },
        path(Segment) { id =>
          concat(
            userGetDetailController.route(id),
            userUpdateController.route(id),
            userDeleteController.route(id)
          )
        }
      )
    }

  private def authenticated(onSuccess: (String, AuthenticatedUser) => Route): Route =
    optionalHeaderValueByName("Authorization") {
      case Some(value) if value.startsWith("Bearer ") =>
        val token = value.stripPrefix("Bearer ").trim
        auth.authenticate(token) match {
          case Right(user) => onSuccess(token, user)
          case Left(error) => complete(authErrorResponse(error))
        }
      case _ =>
        complete(authErrorResponse(AuthError.InvalidToken))
    }

  private def decodeEntity[A: Decoder](onSuccess: A => Route): Route =
    entity(as[String]) { body =>
      decode[A](body) match {
        case Right(request) => onSuccess(request)
        case Left(_) =>
          complete(
            errorResponse(StatusCodes.BadRequest, "invalid_json", List("invalid JSON request body"))
          )
      }
    }

  private def completeJson[A: Encoder](status: StatusCode, value: A): Route =
    complete(jsonResponse(status, value.asJson))

  private def authErrorResponse(error: AuthError): HttpResponse =
    error match {
      case AuthError.InvalidRequest =>
        errorResponse(StatusCodes.BadRequest, "invalid_login_request", Nil)
      case AuthError.InvalidToken =>
        errorResponse(StatusCodes.Unauthorized, "invalid_bearer_token", Nil)
    }

  private def errorResponse(
      status: StatusCode,
      code: String,
      messages: List[String]
  ): HttpResponse =
    jsonResponse(status, ErrorResponse(code, messages).asJson)

  private def jsonResponse(status: StatusCode, json: Json): HttpResponse =
    HttpResponse(status, entity = HttpEntity(ContentTypes.`application/json`, json.noSpaces))
}

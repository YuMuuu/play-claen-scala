package dev.yumuuu.playclean.presentation.http

import cats.effect.IO
import dev.yumuuu.playclean.usecase.user.UserApplicationError
import io.circe.{Decoder, Json}
import io.circe.parser.decode
import io.circe.syntax.*
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import org.apache.pekko.http.scaladsl.server.{Directives, Route}
import org.slf4j.LoggerFactory
import scala.util.{Failure, Success}

private[http] trait UserControllerSupport extends Directives {
  import JsonModels.given

  protected def effectRunner: EffectRunner

  private val logger = LoggerFactory.getLogger(getClass)

  protected def decodeEntity[A: Decoder](onSuccess: A => Route): Route =
    entity(as[String]) { body =>
      decode[A](body) match {
        case Right(request) => onSuccess(request)
        case Left(_) =>
          complete(
            errorResponse(StatusCodes.BadRequest, "invalid_json", List("invalid JSON request body"))
          )
      }
    }

  protected def completeEffect[A](effect: IO[A])(toResponse: A => HttpResponse): Route =
    onComplete(effectRunner.toFuture(effect)) {
      case Success(value) => complete(toResponse(value))
      case Failure(error) =>
        logger.error("unexpected request failure", error)
        complete(errorResponse(StatusCodes.InternalServerError, "internal_error", Nil))
    }

  protected def userErrorResponse(error: UserApplicationError): HttpResponse =
    error match {
      case UserApplicationError.Validation(messages) =>
        errorResponse(StatusCodes.BadRequest, "validation_error", messages)
      case UserApplicationError.NotFound(_) =>
        errorResponse(StatusCodes.NotFound, "user_not_found", Nil)
      case UserApplicationError.Conflict =>
        errorResponse(StatusCodes.Conflict, "user_conflict", Nil)
      case UserApplicationError.RepositoryUnavailable =>
        errorResponse(StatusCodes.ServiceUnavailable, "repository_unavailable", Nil)
    }

  protected def jsonResponse(status: org.apache.pekko.http.scaladsl.model.StatusCode, json: Json)
      : HttpResponse =
    HttpResponse(status, entity = HttpEntity(ContentTypes.`application/json`, json.noSpaces))

  private def errorResponse(
      status: org.apache.pekko.http.scaladsl.model.StatusCode,
      code: String,
      messages: List[String]
  ): HttpResponse =
    jsonResponse(status, ErrorResponse(code, messages).asJson)
}

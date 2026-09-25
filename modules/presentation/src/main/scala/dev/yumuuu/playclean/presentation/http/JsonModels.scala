package dev.yumuuu.playclean.presentation.http

import dev.yumuuu.playclean.application.auth.{AuthenticatedUser, LoginResult}
import dev.yumuuu.playclean.usecase.user.UserView
import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

private[http] object JsonModels {
  given Decoder[LoginRequest] = deriveDecoder
  given Decoder[CreateUserRequest] = deriveDecoder
  given Decoder[UpdateUserRequest] = deriveDecoder
  given Encoder[AuthenticatedUser] = deriveEncoder
  given Encoder[LoginResult] = deriveEncoder
  given Encoder[UserView] = deriveEncoder
  given Encoder[ErrorResponse] = deriveEncoder

  def usersJson(users: List[UserView]): Json =
    Json.obj("users" -> Encoder.encodeList[UserView].apply(users))
}

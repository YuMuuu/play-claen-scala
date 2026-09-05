package dev.yumuuu.playclean.application.auth

import jakarta.inject.{Inject, Named, Singleton}

@Singleton
class MockAuthService @Inject() (
    @Named("mockAccessToken") accessToken: String,
    authenticatedUser: AuthenticatedUser
) {
  def login(command: LoginCommand): Either[AuthError, LoginResult] =
    if command.id.trim.isEmpty || command.password.isEmpty then Left(AuthError.InvalidRequest)
    else Right(LoginResult(accessToken, "Bearer", authenticatedUser))

  def authenticate(token: String): Either[AuthError, AuthenticatedUser] =
    Either.cond(token == accessToken, authenticatedUser, AuthError.InvalidToken)

  def logout(token: String): Either[AuthError, Unit] =
    authenticate(token).map(_ => ())
}

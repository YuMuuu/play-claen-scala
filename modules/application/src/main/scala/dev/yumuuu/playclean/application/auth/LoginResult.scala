package dev.yumuuu.playclean.application.auth

final case class LoginResult(accessToken: String, tokenType: String, user: AuthenticatedUser)

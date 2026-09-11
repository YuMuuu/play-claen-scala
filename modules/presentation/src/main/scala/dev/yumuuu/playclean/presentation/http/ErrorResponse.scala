package dev.yumuuu.playclean.presentation.http

private[http] final case class ErrorResponse(code: String, messages: List[String])

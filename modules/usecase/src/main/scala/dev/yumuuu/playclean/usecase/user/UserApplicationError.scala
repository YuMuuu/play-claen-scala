package dev.yumuuu.playclean.usecase.user

enum UserApplicationError {
  case Validation(messages: List[String])
  case NotFound(id: String)
  case Conflict
  case RepositoryUnavailable
}

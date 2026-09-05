package dev.yumuuu.playclean.domain.user

enum RepositoryError {
  case Conflict
  case InvalidStoredData(message: String)
  case Unavailable(message: String)
}

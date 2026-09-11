package dev.yumuuu.playclean.domain.user

enum UserValidationError {
  case NameTooShort(minLength: Int)
  case NameTooLong(maxLength: Int)
  case InvalidRole(value: String)

  def message: String = this match {
    case NameTooShort(minLength) => s"name must be at least $minLength characters"
    case NameTooLong(maxLength)  => s"name must be at most $maxLength characters"
    case InvalidRole(value)      => s"invalid user role: $value"
  }
}

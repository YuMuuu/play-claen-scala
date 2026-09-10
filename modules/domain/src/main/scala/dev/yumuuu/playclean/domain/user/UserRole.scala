package dev.yumuuu.playclean.domain.user

enum UserRole {
  case Admin
  case Member
}

object UserRole {
  def fromString(value: String): Either[UserValidationError, UserRole] =
    value.trim.toUpperCase match {
      case "ADMIN"  => Right(UserRole.Admin)
      case "MEMBER" => Right(UserRole.Member)
      case _        => Left(UserValidationError.InvalidRole(value))
    }

  extension (role: UserRole) {
    def value: String = role match {
      case UserRole.Admin  => "ADMIN"
      case UserRole.Member => "MEMBER"
    }
  }
}

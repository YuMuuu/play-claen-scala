package dev.yumuuu.playclean.domain.user

opaque type UserName = String

object UserName {
  private val MinLength = 3
  private val MaxLength = 10

  def fromString(value: String): Either[UserValidationError, UserName] =
    if value.length < MinLength then Left(UserValidationError.NameTooShort(MinLength))
    else if value.length > MaxLength then Left(UserValidationError.NameTooLong(MaxLength))
    else Right(value)

  extension (userName: UserName) {
    def value: String = userName
  }
}

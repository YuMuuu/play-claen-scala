package dev.yumuuu.playclean.application.user

import cats.syntax.all.*
import dev.yumuuu.playclean.domain.user.{RepositoryError, User, UserName, UserRole}
import dev.yumuuu.playclean.usecase.user.{UserApplicationError, UserView}

private[user] object UserApplicationSupport {
  def validateUser(
      rawName: String,
      rawRole: String
  ): Either[UserApplicationError, (UserName, UserRole)] =
    (
      UserName.fromString(rawName).toValidatedNel,
      UserRole.fromString(rawRole).toValidatedNel
    ).mapN((name, role) => (name, role))
      .toEither
      .leftMap(errors => UserApplicationError.Validation(errors.toList.map(_.message)))

  def validateName(rawName: String): Either[UserApplicationError, UserName] =
    UserName
      .fromString(rawName)
      .leftMap(error => UserApplicationError.Validation(List(error.message)))

  def toApplicationError(error: RepositoryError): UserApplicationError =
    error match {
      case RepositoryError.Conflict             => UserApplicationError.Conflict
      case RepositoryError.InvalidStoredData(_) => UserApplicationError.RepositoryUnavailable
      case RepositoryError.Unavailable(_)       => UserApplicationError.RepositoryUnavailable
    }

  def toView(user: User): UserView =
    UserView(
      id = user.id.value,
      name = user.name.value,
      role = user.role.value
    )
}

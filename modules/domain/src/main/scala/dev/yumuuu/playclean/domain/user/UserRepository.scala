package dev.yumuuu.playclean.domain.user

import cats.effect.IO

trait UserRepository {
  def findAll: IO[Either[RepositoryError, List[User]]]

  def find(id: UserId): IO[Either[RepositoryError, Option[User]]]

  def insert(user: User): IO[Either[RepositoryError, Unit]]

  def update(user: User): IO[Either[RepositoryError, Boolean]]

  def delete(id: UserId): IO[Either[RepositoryError, Boolean]]
}

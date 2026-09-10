package dev.yumuuu.playclean.infrastructure.persistence

import cats.data.EitherT
import cats.effect.IO
import cats.syntax.all.*
import dev.yumuuu.playclean.domain.user.{RepositoryError, User, UserId, UserName, UserRepository, UserRole}
import jakarta.inject.{Inject, Singleton}
import java.sql.SQLException
import org.typelevel.doobie.{ConnectionIO, Transactor}
import org.typelevel.doobie.implicits.*

@Singleton
class DoobieUserRepository @Inject() (transactor: Transactor[IO]) extends UserRepository {
  override def findAll: IO[Either[RepositoryError, List[User]]] =
    (
      for {
        rows <- attempt(
          sql"""
            SELECT id, name, role
            FROM users
            ORDER BY name, id
          """.queryWithLabel[UserRow]("users.find-all").to[List]
        )
        users <- EitherT.fromEither[ConnectionIO](rows.traverse(toDomain))
      } yield users
    )
      .transact(transactor)
      .value

  override def find(id: UserId): IO[Either[RepositoryError, Option[User]]] =
    (
      for {
        row <- attempt(
          sql"""
            SELECT id, name, role
            FROM users
            WHERE id = ${id.value}
          """.queryWithLabel[UserRow]("users.find").option
        )
        user <- EitherT.fromEither[ConnectionIO](row.traverse(toDomain))
      } yield user
    )
      .transact(transactor)
      .value

  override def insert(user: User): IO[Either[RepositoryError, Unit]] =
    attempt(
      sql"""
        INSERT INTO users (id, name, role)
        VALUES (${user.id.value}, ${user.name.value}, ${user.role.value})
      """.updateWithLabel("users.insert").run.void
    )
      .transact(transactor)
      .value

  override def update(user: User): IO[Either[RepositoryError, Boolean]] =
    attempt(
      sql"""
        UPDATE users
        SET name = ${user.name.value}, role = ${user.role.value}
        WHERE id = ${user.id.value}
      """.updateWithLabel("users.update").run.map(_ == 1)
    )
      .transact(transactor)
      .value

  override def delete(id: UserId): IO[Either[RepositoryError, Boolean]] =
    attempt(
      sql"""
        DELETE FROM users
        WHERE id = ${id.value}
      """.updateWithLabel("users.delete").run.map(_ == 1)
    )
      .transact(transactor)
      .value

  private def attempt[A](connection: ConnectionIO[A]): EitherT[ConnectionIO, RepositoryError, A] =
    EitherT(connection.attempt.map(_.leftMap(toRepositoryError)))

  private def toDomain(row: UserRow): Either[RepositoryError, User] =
    (
      UserName.fromString(row.name),
      UserRole.fromString(row.role)
    ).mapN((name, role) => User.create(UserId.from(row.id), name, role))
      .leftMap(error => RepositoryError.InvalidStoredData(error.message))

  private def toRepositoryError(error: Throwable): RepositoryError =
    error match {
      case sqlError: SQLException if sqlError.getSQLState == "23505" =>
        RepositoryError.Conflict
      case _ =>
        RepositoryError.Unavailable(error.getClass.getSimpleName)
    }
}

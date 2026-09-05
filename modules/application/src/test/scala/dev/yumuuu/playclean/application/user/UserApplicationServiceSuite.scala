package dev.yumuuu.playclean.application.user

import cats.effect.{IO, Ref}
import cats.effect.unsafe.implicits.global
import dev.yumuuu.playclean.domain.user.{RepositoryError, User, UserId, UserRepository}
import dev.yumuuu.playclean.usecase.user.{CreateUserCommand, UserApplicationError}

class UserAddInteractorSuite extends munit.FunSuite {
  test("create validates and persists a user") {
    val repository = new RecordingRepository
    val useCase = new UserAddInteractor(repository)

    val result = useCase.handle(CreateUserCommand("Alice", "admin")).unsafeRunSync()

    assert(result.exists(user => user.name == "Alice" && user.role == "ADMIN"))
    assertEquals(repository.inserted.unsafeRunSync().map(_.name.value), Some("Alice"))
  }

  test("create accumulates independent validation errors") {
    val repository = new RecordingRepository
    val useCase = new UserAddInteractor(repository)

    val result = useCase.handle(CreateUserCommand(" ", "unknown")).unsafeRunSync()

    assertEquals(
      result,
      Left(
        UserApplicationError.Validation(
          List("name must be at least 3 characters", "invalid user role: unknown")
        )
      )
    )
    assertEquals(repository.inserted.unsafeRunSync(), None)
  }

  private final class RecordingRepository extends UserRepository {
    private val recorded = Ref.unsafe[IO, Option[User]](None)

    def inserted: IO[Option[User]] = recorded.get

    override def findAll: IO[Either[RepositoryError, List[User]]] =
      recorded.get.map(users => Right(users.toList))

    override def find(id: UserId): IO[Either[RepositoryError, Option[User]]] =
      recorded.get.map(user => Right(user.filter(_.id == id)))

    override def insert(user: User): IO[Either[RepositoryError, Unit]] =
      recorded.set(Some(user)).as(Right(()))

    override def update(user: User): IO[Either[RepositoryError, Boolean]] =
      IO.pure(Right(false))

    override def delete(id: UserId): IO[Either[RepositoryError, Boolean]] =
      IO.pure(Right(false))
  }
}

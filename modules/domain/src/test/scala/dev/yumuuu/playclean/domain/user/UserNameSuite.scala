package dev.yumuuu.playclean.domain.user

class UserNameSuite extends munit.FunSuite {
  test("accepts a name from 3 to 10 characters") {
    assertEquals(UserName.fromString("Alice").map(_.value), Right("Alice"))
  }

  test("rejects a name shorter than 3 characters") {
    assertEquals(UserName.fromString("ab"), Left(UserValidationError.NameTooShort(3)))
  }

  test("rejects a name over 10 characters") {
    assertEquals(
      UserName.fromString("a" * 11),
      Left(UserValidationError.NameTooLong(10))
    )
  }
}

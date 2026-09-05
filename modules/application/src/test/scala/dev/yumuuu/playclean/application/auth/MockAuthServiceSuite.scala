package dev.yumuuu.playclean.application.auth

class MockAuthServiceSuite extends munit.FunSuite {
  private val user = AuthenticatedUser("mock-id", "Mock User")
  private val service = new MockAuthService("mock-token", user)

  test("accepts any non-empty mock credentials") {
    assertEquals(
      service.login(LoginCommand("anything", "anything")),
      Right(LoginResult("mock-token", "Bearer", user))
    )
  }

  test("rejects an invalid bearer token") {
    assertEquals(service.authenticate("wrong-token"), Left(AuthError.InvalidToken))
  }
}

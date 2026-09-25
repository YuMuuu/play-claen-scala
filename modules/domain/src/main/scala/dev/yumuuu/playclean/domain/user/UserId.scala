package dev.yumuuu.playclean.domain.user

opaque type UserId = String

object UserId {
  def from(value: String): UserId = value

  extension (userId: UserId) {
    def value: String = userId
  }
}

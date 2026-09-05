package dev.yumuuu.playclean.domain.user

opaque type UserId = String

object UserId {
  def fromString(value: String): UserId = value

  extension (userId: UserId) {
    def value: String = userId
  }
}

package dev.yumuuu.playclean.domain.user

final case class User private (
    id: UserId,
    name: UserName,
    role: UserRole
) {
  def changeName(name: UserName): User =
    copy(name = name)
}

object User {
  def create(id: UserId, name: UserName, role: UserRole): User =
    new User(id, name, role)
}

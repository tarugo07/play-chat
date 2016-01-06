package domain.model.user

import domain.model.Entity

case class User(id: UserId, name: String) extends Entity

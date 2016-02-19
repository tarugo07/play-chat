package domain.model.chat.member

import domain.model.{Entity, ValueObject}

case class MemberId(value: Long) extends ValueObject {
  def isUndefined: Boolean = {
    value == MemberId.undefinedValue
  }
}

object UndefinedId {
  def toMemberId: MemberId = MemberId(value = MemberId.undefinedValue)
}

object MemberId {
  val undefinedValue = 0L
}

case class MemberName(name: String) extends ValueObject {
  require(name.length > 0 && name.length <= 32)
}

case class Member(id: MemberId, name: MemberName) extends Entity {
  def changeName(name: MemberName): Member = {
    this.copy(name = name)
  }
}

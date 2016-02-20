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

class Member(val id: MemberId, val name: MemberName) extends Entity {

  def changeName(name: MemberName): Member = {
    this.copy(name = name)
  }

  def copy(id: MemberId = this.id, name: MemberName = this.name): Member = {
    new Member(id, name)
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Member]

  override def equals(other: Any): Boolean = other match {
    case that: Member =>
      (that canEqual this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(id)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

}

object Member {

  def apply(id: MemberId, name: MemberName): Member = {
    new Member(id, name)
  }

}

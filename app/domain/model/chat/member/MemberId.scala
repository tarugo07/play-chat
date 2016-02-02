package domain.model.chat.member

import domain.model.ValueObject

case class MemberId(value: Long) extends ValueObject {

  def isUndefined: Boolean = {
    value == MemberId.undefinedValue
  }

}

case class UndefinedMemberId() {

  def toMemberId: MemberId = {
    MemberId(value = MemberId.undefinedValue)
  }

}

object MemberId {

  val undefinedValue = 0

}

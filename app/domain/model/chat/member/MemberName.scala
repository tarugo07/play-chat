package domain.model.chat.member

import domain.model.ValueObject

case class MemberName(name: String) extends ValueObject {

  require(name.length > 0 && name.length <= 32)

}

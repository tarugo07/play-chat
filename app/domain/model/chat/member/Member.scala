package domain.model.chat.member

import domain.model.Entity

case class Member(id: MemberId, name: MemberName) extends Entity {

  def changeName(name: MemberName): Member = {
    this.copy(name = name)
  }

}

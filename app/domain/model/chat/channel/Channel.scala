package domain.model.chat.channel

import domain.model.chat.member.{Member, MemberId}
import domain.model.{Entity, ValueObject}

case class ChannelId(value: Long) extends ValueObject {
  def isUndefined: Boolean = {
    value == ChannelId.undefinedValue
  }
}

case class UndefinedChannelId() {
  def toChannelId: ChannelId = ChannelId(value = ChannelId.undefinedValue)
}

object ChannelId {
  val undefinedValue = 0
}

case class ChannelName(name: String) extends ValueObject {
  require(name.length > 0 && name.length <= 128)
}

case class Channel(id: ChannelId, name: ChannelName, participants: Set[MemberId]) extends Entity {

  def join(member: Member): Channel = {
    this.copy(participants = participants + member.id)
  }

  def leave(member: Member): Channel = {
    this.copy(participants = participants - member.id)
  }

  def changeName(name: ChannelName): Channel = {
    this.copy(name = name)
  }

}

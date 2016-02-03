package domain.model.chat.channel

import domain.model.ValueObject

case class ChannelId(value: Long) extends ValueObject {

  def isUndefined: Boolean = {
    value == ChannelId.undefinedValue
  }

}

case class UndefinedChannelId() {

  def toChatRoomId: ChannelId = {
    ChannelId(value = ChannelId.undefinedValue)
  }

}

object ChannelId {

  val undefinedValue = 0

}

package domain.model.chat.room

import domain.model.ValueObject

case class ChatRoomId(value: Long) extends ValueObject {

  def isUndefined: Boolean = {
    value == ChatRoomId.undefinedValue
  }

}

case class UndefinedChatRoomId() {

  def toChatRoomId: ChatRoomId = {
    ChatRoomId(value = ChatRoomId.undefinedValue)
  }

}

object ChatRoomId {

  val undefinedValue = 0

}

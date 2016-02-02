package domain.model.chat.message

import domain.model.ValueObject

case class MessageId(value: Long) extends ValueObject {

  def isUndefined: Boolean = {
    value == MessageId.undefinedValue
  }

}

case class UndefinedMessageId() {

  def toMessageId: MessageId = {
    MessageId(value = MessageId.undefinedValue)
  }

}

object MessageId {

  val undefinedValue = 0

}

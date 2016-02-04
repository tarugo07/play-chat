package domain.model.chat.message

import java.time.ZonedDateTime

import domain.model.chat.channel.ChannelId
import domain.model.chat.member.Member
import domain.model.{Entity, ValueObject}

case class MessageId(value: Long) extends ValueObject {
  def isUndefined: Boolean = {
    value == MessageId.undefinedValue
  }
}

case class UndefinedMessageId() {
  def toMessageId: MessageId = MessageId(value = MessageId.undefinedValue)
}

object MessageId {
  val undefinedValue = 0
}

case class MessageContent(content: String) extends ValueObject {
  require(content.length > 0 && content.length <= 255)
}

case class Message(id: MessageId, text: MessageContent, channelId: ChannelId, sender: Member, occurredOn: ZonedDateTime) extends Entity

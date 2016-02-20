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

object UndefinedId {
  def toMessageId: MessageId = MessageId(value = MessageId.undefinedValue)
}

object MessageId {
  val undefinedValue = 0L
}

case class MessageContent(content: String) extends ValueObject {
  require(content.length > 0 && content.length <= 255)
}

class Message(val id: MessageId, val text: MessageContent, val channelId: ChannelId, val sender: Member, val occurredOn: ZonedDateTime) extends Entity {

  def copy(id: MessageId = this.id,
           text: MessageContent = this.text,
           channelId: ChannelId = this.channelId,
           sender: Member = this.sender,
           occurredOn: ZonedDateTime = this.occurredOn): Message = {
    new Message(id, text, channelId, sender, occurredOn)
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Message]

  override def equals(other: Any): Boolean = other match {
    case that: Message =>
      (that canEqual this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(id)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

}

object Message {

  def apply(id: MessageId, text: MessageContent, channelId: ChannelId, sender: Member, occurredOn: ZonedDateTime): Message = {
    new Message(id, text, channelId, sender, occurredOn)
  }

}

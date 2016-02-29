package domain.model.chat.channel

import domain.model.chat.member.{Member, MemberId}
import domain.model.{Entity, ValueObject}

case class ChannelId(value: Long) extends ValueObject {
  def isUndefined: Boolean = {
    value == ChannelId.undefinedValue
  }
}

object ChannelId {
  val undefinedValue = 0L
}

case class ChannelName(name: String) extends ValueObject {
  require(name.length > 0 && name.length <= 128)
}

class Channel(val id: ChannelId, val name: ChannelName, val participants: Set[MemberId]) extends Entity {

  def join(member: Member): Channel = {
    this.copy(participants = participants + member.id)
  }

  def leave(member: Member): Channel = {
    this.copy(participants = participants - member.id)
  }

  def changeName(name: ChannelName): Channel = {
    this.copy(name = name)
  }

  def copy(id: ChannelId = this.id,
           name: ChannelName = this.name,
           participants: Set[MemberId] = this.participants): Channel = {
    new Channel(id, name, participants)
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Channel]

  override def equals(other: Any): Boolean = other match {
    case that: Channel =>
      (that canEqual this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(id)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

}

object Channel {

  def apply(id: ChannelId, name: ChannelName, participants: Set[MemberId]): Channel = {
    new Channel(id, name, participants)
  }

}

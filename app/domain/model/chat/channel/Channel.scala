package domain.model.chat.channel

import domain.model.Entity
import domain.model.chat.member.{Member, MemberId}

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

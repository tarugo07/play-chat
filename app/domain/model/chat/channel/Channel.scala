package domain.model.chat.channel

import domain.model.Entity
import domain.model.chat.member.MemberId
import domain.model.chat.message.MessageId

case class Channel(id: ChannelId, name: String, participants: Set[MemberId], messages: Seq[MessageId]) extends Entity

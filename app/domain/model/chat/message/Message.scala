package domain.model.chat.message

import java.time.ZonedDateTime

import domain.model.Entity
import domain.model.chat.channel.ChannelId
import domain.model.chat.member.Member

case class Message(id: MessageId, text: String, channelId: ChannelId, sender: Member, occurredOn: ZonedDateTime) extends Entity

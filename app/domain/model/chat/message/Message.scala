package domain.model.chat.message

import java.time.LocalDateTime

import domain.model.Entity
import domain.model.chat.member.Member

case class Message(id: MessageId, text: String, sender: Member, occurredOn: LocalDateTime) extends Entity

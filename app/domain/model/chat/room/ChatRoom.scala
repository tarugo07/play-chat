package domain.model.chat.room

import domain.model.chat.member.MemberId
import domain.model.chat.message.MessageId

case class ChatRoom(id: ChatRoomId, name: String, participants: Set[MemberId], messages: Seq[MessageId])

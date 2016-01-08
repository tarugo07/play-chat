package domain.model.chat.room

import domain.model.chat.member.Member

case class ChatRoom(id: ChatRoomId, name: String, participants: Set[Member])

package domain.model.chat.room

trait ChatRoomRepository {

  def chatRoomOfIdentity(id: ChatRoomId): ChatRoom

}

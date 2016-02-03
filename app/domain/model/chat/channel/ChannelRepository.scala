package domain.model.chat.channel

trait ChannelRepository {

  def chatRoomOfIdentity(id: ChannelId): Channel

}

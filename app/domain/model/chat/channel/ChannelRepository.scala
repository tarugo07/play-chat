package domain.model.chat.channel

trait ChannelRepository {

  def channelOfIdentity(id: ChannelId): Channel

}

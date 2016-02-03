package domain.model.chat.channel

import domain.model.ValueObject

case class ChannelName(name: String) extends ValueObject {

  require(name.length > 0 && name.length <= 128)

}

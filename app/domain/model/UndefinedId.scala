package domain.model

import domain.model.account.{AccountSessionId, AccountId}
import domain.model.chat.channel.ChannelId
import domain.model.chat.member.MemberId
import domain.model.chat.message.MessageId

object UndefinedId {

  def toAccountId: AccountId = AccountId(value = AccountId.undefinedValue)

  def toAccountSessionId: AccountSessionId = AccountSessionId(value = AccountSessionId.undefinedValue)

  def toChannelId: ChannelId = ChannelId(value = ChannelId.undefinedValue)

  def toMemberId: MemberId = MemberId(value = MemberId.undefinedValue)

  def toMessageId: MessageId = MessageId(value = MessageId.undefinedValue)

}

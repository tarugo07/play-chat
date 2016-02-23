package domain.model.account

import java.time.ZonedDateTime

import domain.model.ValueObject

case class AccessToken(accountId: AccountId, hash: String, occurredOn: ZonedDateTime) extends ValueObject {
  override def toString = accountId.value.toString + "_" + hash + "_" + occurredOn.toEpochSecond
}

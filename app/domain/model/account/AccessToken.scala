package domain.model.account

import java.security.MessageDigest
import java.time.{Instant, ZoneId, ZonedDateTime}

import domain.model.ValueObject

case class AccessToken(accountId: AccountId, hash: String, occurredOn: ZonedDateTime) extends ValueObject {
  override def toString = accountId.value.toString + "_" + hash + "_" + occurredOn.toEpochSecond
}

object AccessToken {

  val ExpireMinutes = 30L

  def apply(accountSession: AccountSession): AccessToken = {
    val seed = accountSession.accountId.value.toString + accountSession.salt + accountSession.expire.time.toEpochSecond
    val hash = MessageDigest.getInstance("SHA-256").digest(seed.getBytes("UTF-8")).map("%02x".format(_)).mkString
    AccessToken(accountSession.accountId, hash, accountSession.expire.time)
  }

  def parse(token: String): Option[AccessToken] = {
    token.split("_") match {
      case Array(accountId, hash, occurredOn) =>
        val accessToken = AccessToken(
          accountId = AccountId(accountId.toLong),
          hash = hash,
          occurredOn = ZonedDateTime.ofInstant(Instant.ofEpochSecond(occurredOn.toLong), ZoneId.of("UTC"))
        )
        Some(accessToken)
      case _ => None
    }
  }

}

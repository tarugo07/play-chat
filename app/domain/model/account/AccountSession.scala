package domain.model.account

import java.time.{ZoneId, ZonedDateTime}

import domain.model.{Entity, ValueObject}

case class AccountSessionId(value: Long) extends ValueObject {
  def isUndefined: Boolean = {
    value == AccountSessionId.undefinedValue
  }
}

object AccountSessionId {
  val undefinedValue = 0L
}

case class AccountSessionSalt(value: String) extends ValueObject {
  require(value.length > 0 && value.length <= 256)
}

case class AccountSessionExpire(time: ZonedDateTime) extends ValueObject {
  def isExpired: Boolean = time.isBefore(ZonedDateTime.now(ZoneId.of("UTC")))
}

class AccountSession(val id: AccountSessionId,
                     val accountId: AccountId,
                     val salt: AccountSessionSalt,
                     val expire: AccountSessionExpire) extends Entity {

  def copy(id: AccountSessionId = this.id,
           accountId: AccountId = this.accountId,
           salt: AccountSessionSalt = this.salt,
           expire: AccountSessionExpire = this.expire) = {
    new AccountSession(id, accountId, salt, expire)
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[AccountSession]

  override def equals(other: Any): Boolean = other match {
    case that: AccountSession =>
      (that canEqual this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(id)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString = s"AccountSession(id=$id, accountId=$accountId, salt=$salt, expire=$expire)"

}

object AccountSession {

  def apply(id: AccountSessionId,
            accountId: AccountId,
            salt: AccountSessionSalt,
            expire: AccountSessionExpire): AccountSession = {
    new AccountSession(id, accountId, salt, expire)
  }

}

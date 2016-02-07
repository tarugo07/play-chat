package domain.model.account

import domain.model.{Entity, ValueObject}

case class AccountId(value: Long) extends ValueObject {
  def isUndefined: Boolean = {
    value == AccountId.undefinedValue
  }
}

case class UndefinedId() {
  def toAccountId: AccountId = AccountId(value = AccountId.undefinedValue)
}

object AccountId {
  val undefinedValue = 0L
}

case class AccountName(value: String) extends ValueObject {
  require(value.length > 0 && value.length <= 128)
}

case class Password(value: String) extends ValueObject {
  val pattern = """\w{8,128}"""
  require(value.matches(pattern))
}

case class MailAddress(address: String) extends ValueObject {
  val pattern = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"""
  require(address.length <= 256)
  require(address.matches(address))
}

// TODO: 後ほど、case classをやめる
case class Account(id: AccountId, name: AccountName, password: Password, address: MailAddress) extends Entity

package domain.model.account

import domain.model.{Entity, ValueObject}

case class AccountId(value: Long) extends ValueObject {
  def isUndefined: Boolean = {
    value == AccountId.undefinedValue
  }
}

object UndefinedId {
  def toAccountId: AccountId = AccountId(value = AccountId.undefinedValue)
}

object AccountId {
  val undefinedValue = 0L
}

case class AccountName(value: String) extends ValueObject {
  require(value.length > 0 && value.length <= 128)
}

case class AccountPassword(value: String) extends ValueObject {
  val pattern = """\w{8,128}"""
  require(value.matches(pattern))
}

case class AccountMail(value: String) extends ValueObject {
  val pattern = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"""
  require(value.length <= 256)
  require(value.matches(value))
}

// TODO: 後ほど、case classをやめる
case class Account(id: AccountId, name: AccountName, password: AccountPassword, mail: AccountMail) extends Entity {

  def changeAccountName(newName: AccountName): Account = {
    this.copy(name = newName)
  }

  def changePassword(newPassword: AccountPassword): Account = {
    this.copy(password = newPassword)
  }

  def changeMailAddress(newMailAddress: AccountMail): Account = {
    this.copy(mail = newMailAddress)
  }

}

package domain.model.account

import domain.model.{Entity, ValueObject}

case class AccountId(value: Long) extends ValueObject {
  def isUndefined: Boolean = {
    value == AccountId.undefinedValue
  }
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

  lazy val localPart = value.split("@").head
}

class Account(val id: AccountId, val name: AccountName, val password: AccountPassword, val mail: AccountMail) extends Entity {

  def changeName(newName: AccountName): Account = {
    this.copy(name = newName)
  }

  def changePassword(newPassword: AccountPassword): Account = {
    this.copy(password = newPassword)
  }

  def changeMail(newMail: AccountMail): Account = {
    this.copy(mail = newMail)
  }

  def copy(id: AccountId = this.id,
           name: AccountName = this.name,
           password: AccountPassword = this.password,
           mail: AccountMail = this.mail) = {
    new Account(id, name, password, mail)
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Account]

  override def equals(other: Any): Boolean = other match {
    case that: Account =>
      (that canEqual this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(id)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

}

object Account {

  def apply(id: AccountId, name: AccountName, password: AccountPassword, mail: AccountMail): Account = {
    new Account(id, name, password, mail)
  }

}

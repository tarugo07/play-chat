package domain.model.account

import scala.util.Try

trait AccountRepository {

  def nextIdentity(): Try[AccountId]

  def accountOfIdentity(id: AccountId): Try[Account]

  def accountOfMailAddress(mailAddress: MailAddress): Try[Account]

  def save(account: Account): Try[Account]

}

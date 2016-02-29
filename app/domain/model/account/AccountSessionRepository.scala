package domain.model.account

import scala.util.Try

trait AccountSessionRepository {

  def nextIdentity(): Try[AccountSessionId]

  def accountSessionOfAccountId(accountId: AccountId): Try[AccountSession]

  def save(accountSession: AccountSession): Try[AccountSession]

}

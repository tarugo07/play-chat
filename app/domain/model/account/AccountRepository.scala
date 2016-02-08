package domain.model.account

import scala.util.Try

trait AccountRepository {

  def accountOfIdentity(id: AccountId): Try[Account]
  
}

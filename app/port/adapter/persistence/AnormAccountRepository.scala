package port.adapter.persistence

import domain.model.account._
import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._

import scala.util.Try

class AnormAccountRepository extends AccountRepository {

  private val accountIdSample: RowParser[AccountId] = {
    long("id").map {
      case id => AccountId(value = id)
    }
  }

  override def nextIdentity(): Try[AccountId] = Try {
    DB.withConnection { implicit conn =>
      SQL("SELECT auto_increment AS id FROM information_schema.tables WHERE table_name = 'account'")
        .as(accountIdSample.singleOpt).getOrElse(UndefinedId.toAccountId)
    }
  }

  override def accountOfIdentity(id: AccountId): Try[Account] = ???

  override def accountOfMailAddress(mailAddress: MailAddress): Try[Account] = ???

  override def save(account: Account): Try[Account] = ???

}

package port.adapter.persistence

import anorm.SqlParser._
import anorm._
import domain.model.EntityNotFoundException
import domain.model.account._
import play.api.Play.current
import play.api.db._

import scala.util.Try

class AnormAccountRepository extends AccountRepository {

  private val accountIdParser: RowParser[AccountId] = {
    long("id").map {
      case id => AccountId(value = id)
    }
  }

  private val accountParser: RowParser[Account] = {
    (long("id") ~ str("name") ~ str("password") ~ str("mail")).map {
      case id ~ name ~ password ~ mail =>
        Account(
          id = AccountId(value = id),
          name = AccountName(value = name),
          password = AccountPassword(value = password),
          mail = AccountMail(mail)
        )
    }
  }

  private def create(account: Account): Try[Int] = Try {
    DB.withConnection { implicit conn =>
      SQL(
        """
          |INSERT INTO account
          |  (id, name, password, mail, create_time, update_time)
          |VALUES
          |  ({id}, {name}, {password}, {mail}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """.stripMargin
      ).on(
        'id -> account.id.value,
        'name -> account.name.value,
        'password -> account.password.value,
        'mail -> account.mail.value
      ).executeUpdate()
    }
  }

  private def update(account: Account): Try[Int] = Try {
    DB.withConnection { implicit conn =>
      SQL(
        """
          |UPDATE
          | account
          |SET
          | name = {name},
          | password = {password},
          | mail = {mail},
          | update_time = CURRENT_TIMESTAMP
          |WHERE
          | id = {id}
        """.stripMargin
      ).on(
        'id -> account.id.value,
        'name -> account.name.value,
        'password -> account.password.value,
        'mail -> account.mail.value
      ).executeUpdate()
    }
  }

  override def nextIdentity(): Try[AccountId] = Try {
    DB.withConnection() { implicit conn =>
      SQL("SELECT auto_increment AS id FROM information_schema.tables WHERE table_name = 'account'")
        .as(accountIdParser.single)
    }
  }

  override def accountOfIdentity(id: AccountId): Try[Account] = Try {
    val result = DB.withConnection() { implicit conn =>
      SQL("SELECT * FROM account WHERE id = {id}")
        .on('id -> id.value).as(accountParser.singleOpt)
    }
    result.getOrElse(throw EntityNotFoundException(message = s"id = ${id.value}"))
  }

  override def accountOfMailAddress(mail: AccountMail): Try[Account] = Try {
    val result = DB.withConnection() { implicit conn =>
      SQL("SELECT * FROM account WHERE mail = {mail}")
        .on('mail -> mail.value).as(accountParser.singleOpt)
    }
    result.getOrElse(throw EntityNotFoundException(message = s"mail = ${mail.value}"))
  }

  override def save(account: Account): Try[Account] = Try {
    val newAccount = if (account.id.isUndefined) {
      account.copy(id = nextIdentity().get)
    } else {
      account
    }
    val result = if (account.id.isUndefined) create(newAccount) else update(newAccount)
    result.map(_ => newAccount).get
  }

}

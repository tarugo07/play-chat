package port.adapter.persistence

import java.time.{ZoneId, ZonedDateTime}
import java.util.Date

import anorm.SqlParser._
import anorm._
import domain.model.EntityNotFoundException
import domain.model.account._
import play.api.Play.current
import play.api.db._

import scala.util.Try

class AnormAccountSessionRepository extends AccountSessionRepository {

  private val accountSessionIdParser: RowParser[AccountSessionId] = {
    long("id").map {
      case id => AccountSessionId(value = id)
    }
  }

  private val accountSessionParser: RowParser[AccountSession] = {
    (long("id") ~ long("account_id") ~ str("salt") ~ date("expire_time")).map {
      case id ~ accountId ~ salt ~ expire =>
        AccountSession(
          id = AccountSessionId(value = id),
          accountId = AccountId(value = accountId),
          salt = AccountSessionSalt(value = salt),
          expire = AccountSessionExpire(time = ZonedDateTime.ofInstant(expire.toInstant, ZoneId.of("UTC")))
        )
    }
  }

  private def create(accountSession: AccountSession): Int = {
    DB.withConnection { implicit conn =>
      SQL(
        """
          |INSERT INTO account_session
          | (id, account_id, salt, expire_time, create_time, update_time)
          |VALUES
          | ({id}, {account_id}, {salt}, {expire}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """.stripMargin
      ).on(
        'id -> accountSession.id.value,
        'account_id -> accountSession.accountId.value,
        'salt -> accountSession.salt.value,
        'expire -> accountSession.expire.time.toString
      ).executeUpdate()
    }
  }

  private def update(accountSession: AccountSession): Int = {
    DB.withConnection { implicit conn =>
      SQL(
        """
          |UPDATE
          | account_session
          |SET
          | account_id = {account_id},
          | salt = {salt},
          | expire_time = {expire},
          | update_time = CURRENT_TIMESTAMP
          |WHERE
          | id = {id}
        """.stripMargin
      ).on(
        'id -> accountSession.id.value,
        'account_id -> accountSession.accountId.value,
        'salt -> accountSession.salt.value,
        'expire -> Date.from(accountSession.expire.time.toInstant)
      ).executeUpdate()
    }
  }

  override def nextIdentity(): Try[AccountSessionId] = Try {
    DB.withConnection() { implicit conn =>
      SQL("SELECT auto_increment AS id FROM information_schema.tables WHERE table_name = 'account_session'")
        .as(accountSessionIdParser.single)
    }
  }

  override def accountSessionOfAccountId(accountId: AccountId): Try[AccountSession] = Try {
    val result = DB.withConnection() { implicit conn =>
      SQL("SELECT * FROM account_session WHERE account_id = {accountId}")
        .on('accountId -> accountId.value).as(accountSessionParser.singleOpt)
    }
    result.getOrElse(throw EntityNotFoundException(message = s"accountId = ${accountId.value}"))
  }

  override def save(accountSession: AccountSession): Try[AccountSession] = Try {
    if (accountSession.id.isUndefined) {
      val newAccountSession = accountSession.copy(id = nextIdentity().get)
      this.create(newAccountSession)
      newAccountSession
    } else {
      this.update(accountSession)
      accountSession
    }
  }

}

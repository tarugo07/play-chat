package application.account

import java.security.MessageDigest
import java.time.{ZoneId, ZonedDateTime}
import java.util.UUID

import domain.model.account._
import domain.model.{EntityNotFoundException, UndefinedId}

import scala.util.{Failure, Success, Try}

case class SignInAccountCommand(mail: String, password: String)

case class SignUpAccountCommand(mail: String, password: String)

case class ChangeAccountNameCommand(id: Long, name: String)

case class ChangeAccountPasswordCommand(id: Long, password: String)

case class ChangeAccountMailCommand(id: Long, mail: String)

class AccountApplicationService(accountRepository: AccountRepository, accountSessionRepository: AccountSessionRepository) {

  def signIn(command: SignInAccountCommand): Try[AccessToken] = {
    def createAccount(account: Account): Try[Account] = {
      accountRepository.accountOfMail(account.mail).map { _ =>
        throw new scala.Exception(s"this mail has already been registered: mail = ${command.mail}")
      } recoverWith {
        case ex: EntityNotFoundException => accountRepository.save(account)
      }
    }

    // TODO: ハッシュ化見直し
    val password = MessageDigest.getInstance("SHA-512")
      .digest(command.password.getBytes).map("%02x".format(_)).mkString

    val accountMail = AccountMail(value = command.mail)

    val newAccount = Account(
      id = UndefinedId.toAccountId,
      name = AccountName(value = accountMail.localPart),
      password = AccountPassword(value = password),
      mail = accountMail
    )

    for {
      account <- createAccount(newAccount)
      newAccountSession = AccountSession(
        id = UndefinedId.toAccountSessionId,
        accountId = account.id,
        salt = AccountSessionSalt(UUID.randomUUID().toString),
        expire = AccountSessionExpire(ZonedDateTime.now(ZoneId.of("UTC")).plusMinutes(AccessToken.ExpireMinutes))
      )
      accountSession <- accountSessionRepository.save(newAccountSession)
    } yield AccessToken(accountSession)
  }

  def signUp(command: SignUpAccountCommand): Try[AccessToken] = Try {
    // TODO: ハッシュ化見直し
    val password = MessageDigest.getInstance("SHA-512")
      .digest(command.password.getBytes).map("%02x".format(_)).mkString

    accountRepository.accountOfMail(AccountMail(command.mail)) match {
      case Success(account) =>
        if (account.password == AccountPassword(password)) {
          val newAccountSession = for {
            accountSession <- accountSessionRepository.accountSessionOfAccountId(account.id)
            newAccountSession = accountSession.copy(
              salt = AccountSessionSalt(UUID.randomUUID().toString),
              expire = AccountSessionExpire(ZonedDateTime.now(ZoneId.of("UTC")).plusMinutes(AccessToken.ExpireMinutes))
            )
            _ <- accountSessionRepository.save(newAccountSession)
          } yield newAccountSession
          AccessToken(newAccountSession.get)
        } else {
          throw new Exception(s"authentication failure: mail = ${command.mail}")
        }
      case Failure(ex) => throw ex
    }
  }

  def changeName(command: ChangeAccountNameCommand): Try[Account] = {
    for {
      account <- accountRepository.accountOfIdentity(AccountId(command.id))
      newAccount <- accountRepository.save(account.changeName(AccountName(command.name)))
    } yield newAccount
  }

  def changePassword(command: ChangeAccountPasswordCommand): Try[Account] = {
    for {
      account <- accountRepository.accountOfIdentity(AccountId(command.id))
      newAccount <- accountRepository.save(account.changePassword(AccountPassword(command.password)))
    } yield newAccount
  }

  private def isRegisteredMail(id: AccountId, mail: AccountMail): Try[Boolean] = Try {
    accountRepository.accountOfMail(mail) match {
      case Success(account) =>
        if (account.id != id) true
        else false
      case Failure(ex) =>
        ex match {
          case _: EntityNotFoundException => false
          case _ => throw ex
        }
    }
  }

  def changeMail(command: ChangeAccountMailCommand): Try[Account] = {
    for {
      account <- accountRepository.accountOfIdentity(AccountId(command.id))
      exist <- isRegisteredMail(AccountId(command.id), AccountMail(command.mail))
      _ = if (exist) Failure(throw new Exception(s"this mail has already been registered: mail = ${command.mail}"))
      newAccount <- accountRepository.save(account.changeMail(AccountMail(command.mail)))
    } yield newAccount
  }

}

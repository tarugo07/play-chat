package application.account

import java.security.MessageDigest
import java.time.{ZoneId, ZonedDateTime}
import java.util.UUID

import domain.model.account._
import domain.model.authentication.AccessToken
import domain.model.{EntityNotFoundException, UndefinedId}

import scala.util.{Failure, Success, Try}

case class SignUpAccountCommand(mail: String, password: String)

case class SignInAccountCommand(mail: String, password: String)

case class ChangeAccountNameCommand(id: Long, name: String)

case class ChangeAccountPasswordCommand(id: Long, currentPassword: String, newPassword: String)

case class ChangeAccountMailCommand(id: Long, mail: String)

class AccountApplicationService(accountRepository: AccountRepository, accountSessionRepository: AccountSessionRepository) {

  def signUp(command: SignUpAccountCommand): Try[AccessToken] = {
    def createAccount(account: Account): Try[Account] = {
      accountRepository.accountOfMail(account.mail).map { _ =>
        throw new Exception(s"this mail has already been registered: mail = ${command.mail}")
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

  def signIn(command: SignInAccountCommand): Try[AccessToken] = {
    // TODO: ハッシュ化見直し
    val password = MessageDigest.getInstance("SHA-512")
      .digest(command.password.getBytes).map("%02x".format(_)).mkString

    for {
      account <- accountRepository.accountOfMail(AccountMail(command.mail))
        .filter(_.password == AccountPassword(password))
        .recoverWith {
          case _: NoSuchElementException =>
            Failure(new Exception(s"authentication failure: mail = ${command.mail}"))
        }
      accountSession <- accountSessionRepository.accountSessionOfAccountId(account.id)
      newAccountSession = accountSession.copy(
        salt = AccountSessionSalt(UUID.randomUUID().toString),
        expire = AccountSessionExpire(ZonedDateTime.now(ZoneId.of("UTC")).plusMinutes(AccessToken.ExpireMinutes))
      )
      _ <- accountSessionRepository.save(newAccountSession)
    } yield AccessToken(newAccountSession)
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
        .filter(_.password.value == command.currentPassword)
        .recoverWith {
          case _: NoSuchElementException =>
            Failure(new Exception(s"invalid current password: id = ${command.id}"))
        }
      newAccount <- accountRepository.save(account.changePassword(AccountPassword(command.newPassword)))
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

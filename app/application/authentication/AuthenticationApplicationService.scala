package application.authentication

import java.security.MessageDigest
import java.time.{ZoneId, ZonedDateTime}
import java.util.UUID

import domain.model.account._
import domain.model.authentication.AccessToken
import domain.model.{EntityNotFoundException, UndefinedId}

import scala.util.{Failure, Try}

case class SignUpAccountCommand(mail: String, password: String)

case class SignInAccountCommand(mail: String, password: String)

class AuthenticationApplicationService(accountRepository: AccountRepository,
                                       accountSessionRepository: AccountSessionRepository) {

  def authenticate(accessToken: AccessToken): Try[Account] = {
    for {
      accessSession <- accountSessionRepository.accountSessionOfAccountId(accessToken.accountId)
        .filter(accountSession => AccessToken(accountSession) == accessToken && !accountSession.expire.isExpired)
        .recoverWith {
          case _: NoSuchElementException =>
            Failure(new Exception(s"invalid access token: token = $accessToken"))
        }
      account <- accountRepository.accountOfIdentity(accessSession.accountId)
    } yield account
  }

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

}

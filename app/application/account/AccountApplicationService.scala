package application.account

import java.security.MessageDigest

import domain.model.EntityNotFoundException
import domain.model.account._

import scala.util.{Failure, Success, Try}

case class ChangeAccountNameCommand(id: Long, name: String)

case class ChangeAccountPasswordCommand(id: Long, currentPassword: String, newPassword: String)

case class ChangeAccountMailCommand(id: Long, mail: String)

class AccountApplicationService(accountRepository: AccountRepository) {

  def changeName(command: ChangeAccountNameCommand): Try[Account] = {
    for {
      account <- accountRepository.accountOfIdentity(AccountId(command.id))
      newAccount <- accountRepository.save(account.changeName(AccountName(command.name)))
    } yield newAccount
  }

  def changePassword(command: ChangeAccountPasswordCommand): Try[Account] = {
    // TODO: ハッシュ化見直し
    val currentPassword = MessageDigest.getInstance("SHA-512")
        .digest(command.currentPassword.getBytes).map("%02x".format(_)).mkString
    val newPassword = MessageDigest.getInstance("SHA-512")
      .digest(command.newPassword.getBytes).map("%02x".format(_)).mkString

    for {
      account <- accountRepository.accountOfIdentity(AccountId(command.id))
        .filter(_.password.value == currentPassword)
        .recoverWith {
          case _: NoSuchElementException =>
            Failure(new Exception(s"invalid current password: id = ${command.id}"))
        }
      newAccount <- accountRepository.save(account.changePassword(AccountPassword(newPassword)))
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

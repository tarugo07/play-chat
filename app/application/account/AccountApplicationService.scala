package application.account

import domain.model.EntityNotFoundException
import domain.model.account._

import scala.util.{Failure, Success, Try}

case class ChangeAccountNameCommand(id: Long, name: String)

case class ChangeAccountPasswordCommand(id: Long, currentPassword: String, newPassword: String)

case class ChangeAccountMailCommand(id: Long, mail: String)

class AccountApplicationService(accountRepository: AccountRepository, accountSessionRepository: AccountSessionRepository) {

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

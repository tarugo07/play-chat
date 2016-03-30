package application.account

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
    val currentPassword = AccountPassword.digest(command.currentPassword)
    val newPassword = AccountPassword.digest(command.newPassword)

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

  def changeMail(command: ChangeAccountMailCommand): Try[Account] = {
    def isRegisteredMail(id: AccountId, mail: AccountMail): Try[Boolean] = {
      accountRepository.accountOfMail(mail)
        .filter(_.id != id).map(_ => true)
        .recover {
          case _: NoSuchElementException => false
          case _: EntityNotFoundException => false
        }
    }

    for {
      account <- accountRepository.accountOfIdentity(AccountId(command.id))
      exist <- isRegisteredMail(AccountId(command.id), AccountMail(command.mail))
      _ = if (exist) Failure(throw new Exception(s"this mail has already been registered: mail = ${command.mail}"))
      newAccount <- accountRepository.save(account.changeMail(AccountMail(command.mail)))
    } yield newAccount
  }

}

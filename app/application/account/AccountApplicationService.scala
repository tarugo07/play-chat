package application.account

import java.security.MessageDigest

import domain.model.EntityNotFoundException
import domain.model.account._

import scala.util.Try

case class RegisterAccountCommand(name: String, password: String, mail: String)

class AccountApplicationService(accountRepository: AccountRepository) {

  def registerAccount(command: RegisterAccountCommand): Try[Account] = {
    // TODO: ハッシュ化見直し
    val password = MessageDigest.getInstance("SHA-512")
      .digest(command.password.getBytes).map("%02x".format(_)).mkString

    val account = Account(
      id = UndefinedId.toAccountId,
      name = AccountName(value = command.name),
      password = AccountPassword(value = password),
      mail = AccountMail(value = command.mail)
    )

    accountRepository.accountOfMail(account.mail).map { account =>
      throw new Exception(s"this mail address that has already been registered: id = ${account.id.value}, mail = ${account.mail.value}")
    } recoverWith {
      case ex: EntityNotFoundException =>
        accountRepository.save(account)
    }
  }

}

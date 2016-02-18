package application.account

import java.security.MessageDigest

import domain.model.EntityNotFoundException
import domain.model.account._

import scala.util.{Failure, Success, Try}

case class SignInAccountCommand(name: String, password: String, mail: String)

case class SignUpAccountCommand(mail: String, password: String)

class AccountApplicationService(accountRepository: AccountRepository) {

  def signIn(command: SignInAccountCommand): Try[Account] = {
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
      throw new Exception(s"this mail has already been registered: mail = ${account.mail.value}")
    } recoverWith {
      case ex: EntityNotFoundException => accountRepository.save(account)
    }
  }

  def signUp(command: SignUpAccountCommand): Try[Account] = Try {
    // TODO: ハッシュ化見直し
    val password = MessageDigest.getInstance("SHA-512")
      .digest(command.password.getBytes).map("%02x".format(_)).mkString

    accountRepository.accountOfMail(AccountMail(command.mail)) match {
      case Success(account) =>
        if (account.password != AccountPassword(password)) {
          throw new Exception(s"authentication failure: mail = ${command.mail}")
        } else
          account
      case Failure(ex) => throw ex
    }
  }

}

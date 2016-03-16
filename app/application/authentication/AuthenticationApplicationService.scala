package application.authentication

import domain.model.account.{Account, AccountRepository, AccountSessionRepository}
import domain.model.authentication.AccessToken

import scala.util.{Failure, Try}

class AuthenticationApplicationService(accountRepository: AccountRepository,
                                       accountSessionRepository: AccountSessionRepository) {

  def authenticate(token: String): Try[Account] = {
    AccessToken.parse(token) match {
      case Some(accessToken) =>
        for {
          accessSession <- accountSessionRepository.accountSessionOfAccountId(accessToken.accountId)
            .filter(accountSession => AccessToken(accountSession) == accessToken && !accountSession.expire.isExpired)
            .recoverWith {
              case _: NoSuchElementException =>
                Failure(new Exception(s"invalid access token: token = $token"))
            }
          account <- accountRepository.accountOfIdentity(accessSession.accountId)
        } yield account
      case None => Failure(new Exception(s"illegal access token: token = $token"))
    }
  }

}

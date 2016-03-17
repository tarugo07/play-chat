package port.adapter.api.controllers

import application.authentication.AuthenticationApplicationService
import domain.model.account.Account
import org.apache.commons.codec.binary.{Base64, StringUtils}
import play.api.mvc._
import port.adapter.persistence.{AnormAccountRepository, AnormAccountSessionRepository}
import util.{Cipher, Configuration}

import scala.concurrent.Future

trait ControllerSupport {

  this: Controller =>

  val accessTokenConfig = Configuration.accessTokenConfig

  val accountRepository = new AnormAccountRepository
  val accountSessionRepository = new AnormAccountSessionRepository
  val applicationService = new AuthenticationApplicationService(accountRepository, accountSessionRepository)

  class AuthenticatedRequest[A](val account: Account, val request: Request[A]) extends WrappedRequest[A](request)

  object AuthAction extends ActionBuilder[AuthenticatedRequest] {

    override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]): Future[Result] = {
      request.headers.get("Authorization").map { token =>
        val decrypted = Cipher.decrypt(
          Base64.decodeBase64(token),
          "Blowfish",
          accessTokenConfig.privateKey,
          accessTokenConfig.initVector,
          "CBC",
          "PKCS5Padding"
        )
        applicationService.authenticate(StringUtils.newStringUtf8(decrypted.get)).toOption.map { account =>
          block(new AuthenticatedRequest(account, request))
        }.getOrElse(Future.successful(Unauthorized))
      }.getOrElse(Future.successful(Forbidden))
    }

  }

}

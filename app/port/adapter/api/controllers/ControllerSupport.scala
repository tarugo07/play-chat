package port.adapter.api.controllers

import application.authentication.AuthenticationApplicationService
import domain.model.account.Account
import domain.model.authentication.AccessToken
import org.apache.commons.codec.binary.{Base64, StringUtils}
import play.api.mvc._
import port.adapter.persistence.{AnormAccountRepository, AnormAccountSessionRepository}
import util.{Cipher, Configuration}

import scala.concurrent.Future
import scala.util.Try

trait ControllerSupport {

  this: Controller =>

  val accessTokenConfig = Configuration.accessTokenConfig

  val accountRepository = new AnormAccountRepository
  val accountSessionRepository = new AnormAccountSessionRepository
  val authenticationApplicationService = new AuthenticationApplicationService(accountRepository, accountSessionRepository)

  def encryptAccessToken(accessToken: AccessToken): Try[String] = {
    Cipher.encrypt(
      accessToken.toString.getBytes("UTF-8"),
      "Blowfish",
      accessTokenConfig.privateKey,
      accessTokenConfig.initVector,
      "CBC",
      "PKCS5Padding"
    ).map(Base64.encodeBase64String)
  }

  def decryptAccessToken(token: String): Try[AccessToken] = {
    Cipher.decrypt(
      Base64.decodeBase64(token),
      "Blowfish",
      accessTokenConfig.privateKey,
      accessTokenConfig.initVector,
      "CBC",
      "PKCS5Padding"
    ).map(StringUtils.newStringUtf8).map { value =>
      AccessToken.parse(value) match {
        case Some(accessToken) => accessToken
        case None => throw new Exception(s"illegal access token: token = $token")
      }
    }
  }

  class AuthenticatedRequest[A](val account: Account, val request: Request[A]) extends WrappedRequest[A](request)

  object AuthAction extends ActionBuilder[AuthenticatedRequest] {

    override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]): Future[Result] = {
      request.headers.get("Authorization").map { token =>
        val accountTry = for {
          accessToken <- decryptAccessToken(token)
          account <- authenticationApplicationService.authenticate(accessToken)
        } yield account

        accountTry.toOption.map { account =>
          block(new AuthenticatedRequest(account, request))
        }.getOrElse(Future.successful(Unauthorized))
      }.getOrElse(Future.successful(Forbidden))
    }

  }

}

package port.adapter.api.controllers

import application.account.{AccountApplicationService, SignInAccountCommand, SignUpAccountCommand}
import org.apache.commons.codec.binary.Base64
import play.api.libs.functional.syntax._
import util.{Configuration, Encryption}
import play.api.libs.json._
import play.api.mvc._
import port.adapter.persistence.{AnormAccountRepository, AnormAccountSessionRepository}

import scala.util.{Failure, Success}

class AccountController extends Controller {
  val accessTokenConfig = Configuration.accessTokenConfig

  val accountRepository = new AnormAccountRepository
  val accountSessionRepository = new AnormAccountSessionRepository

  val applicationService = new AccountApplicationService(accountRepository, accountSessionRepository)

  implicit val reads: Reads[(String, String)] = (
    (__ \ "mail").read[String] and
      (__ \ "password").read[String]
    ).tupled


  def signUp = Action(parse.json) { implicit request =>
    request.body.validate[(String, String)].map {
      case (mail, password) =>
        applicationService.signUp(SignUpAccountCommand(mail, password)) match {
          case Success(accessToken) =>
            val encrypted = Encryption.encrypt(
              accessToken.toString.getBytes("UTF-8"),
              "Blowfish",
              accessTokenConfig.privateKey,
              accessTokenConfig.initVector,
              "CBC",
              "PKCS5Padding"
            )

            Ok(Json.obj(
              "result" -> "OK",
              "access_token" -> Base64.encodeBase64String(encrypted.get)
            ))
          case Failure(ex) =>
            BadRequest(Json.obj("result" -> "NG"))
        }
    }.recoverTotal { e =>
      BadRequest(Json.obj("status" -> "NG"))
    }
  }

  def signIn = Action(parse.json) { implicit request =>
    request.body.validate[(String, String)].map {
      case (mail, password) =>
        applicationService.signIn(SignInAccountCommand(mail, password)) match {
          case Success(accessToken) =>
            val encrypted = Encryption.encrypt(
              accessToken.toString.getBytes("UTF-8"),
              "Blowfish",
              accessTokenConfig.privateKey,
              accessTokenConfig.initVector,
              "CBC",
              "PKCS5Padding"
            )

            Ok(Json.obj(
              "result" -> "OK",
              "access_token" -> Base64.encodeBase64String(encrypted.get)
            ))
          case Failure(ex) =>
            BadRequest(Json.obj("result" -> "NG"))
        }
    }.recoverTotal { e =>
      BadRequest(Json.obj("status" -> "NG"))
    }
  }

}

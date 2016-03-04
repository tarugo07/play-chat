package port.adapter.api.controllers

import application.account.{AccountApplicationService, SignInAccountCommand, SignUpAccountCommand}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import port.adapter.persistence.{AnormAccountRepository, AnormAccountSessionRepository}

import scala.util.{Failure, Success}

class AccountController extends Controller {

  val accountRepository = new AnormAccountRepository
  val accountSessionRepository = new AnormAccountSessionRepository

  val applicationService = new AccountApplicationService(accountRepository, accountSessionRepository)

  implicit val reads: Reads[(String, String)] = (
    (__ \ "mail").read[String] and
      (__ \ "password").read[String]
    ).tupled


  def signIn = Action(parse.json) { implicit request =>
    request.body.validate[(String, String)].map {
      case (mail, password) =>
        applicationService.signIn(SignInAccountCommand(mail, password)) match {
          case Success(accessToken) =>
            Ok(Json.obj(
              "result" -> "OK",
              "access_token" -> accessToken.toString
            ))
          case Failure(ex) =>
            BadRequest(Json.obj("result" -> "NG"))
        }
    }.recoverTotal { e =>
      BadRequest(Json.obj("status" -> "NG"))
    }
  }

  def signUp = Action(parse.json) { implicit request =>
    request.body.validate[(String, String)].map {
      case (mail, password) =>
        applicationService.signUp(SignUpAccountCommand(mail, password)) match {
          case Success(accessToken) =>
            Ok(Json.obj(
              "result" -> "OK",
              "access_token" -> accessToken.toString
            ))
          case Failure(ex) =>
            BadRequest(Json.obj("result" -> "NG"))
        }
    }.recoverTotal { e =>
      BadRequest(Json.obj("status" -> "NG"))
    }
  }

}

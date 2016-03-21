package port.adapter.api.controllers

import application.authentication.{SignInAccountCommand, SignUpAccountCommand}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

import scala.util.{Failure, Success}

class AuthenticationController extends Controller with ControllerSupport {

  implicit val reads: Reads[(String, String)] = (
    (__ \ "mail").read[String] and
      (__ \ "password").read[String]
    ).tupled

  def signUp = Action(parse.json) { implicit request =>
    request.body.validate[(String, String)].map {
      case (mail, password) =>
        authenticationApplicationService.signUp(SignUpAccountCommand(mail, password)) match {
          case Success(accessToken) =>
            Ok(Json.obj(
              "result" -> "OK",
              "access_token" -> encryptAccessToken(accessToken).get
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
        authenticationApplicationService.signIn(SignInAccountCommand(mail, password)) match {
          case Success(accessToken) =>
            Ok(Json.obj(
              "result" -> "OK",
              "access_token" -> encryptAccessToken(accessToken).get
            ))
          case Failure(ex) =>
            BadRequest(Json.obj("result" -> "NG"))
        }
    }.recoverTotal { e =>
      BadRequest(Json.obj("status" -> "NG"))
    }
  }

}

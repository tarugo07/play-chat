package port.adapter.api.controllers

import application.account.{ChangeAccountNameCommand, AccountApplicationService, ChangeAccountPasswordCommand}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

import scala.util.{Failure, Success}

class AccountController extends Controller with ControllerSupport {

  implicit val changePasswordReads: Reads[(String, String)] = (
    (__ \ "currentPassword").read[String] and
      (__ \ "newPassword").read[String]
    ).tupled

  implicit val changeNameReads: Reads[(String)] = (__ \ "name").read[String]

  val accountApplicationService = new AccountApplicationService(accountRepository)

  def changePassword = AuthAction(parse.json) { implicit authenticated =>
    authenticated.request.body.validate[(String, String)].map {
      case (currentPassword, newPassword) =>
        val command = ChangeAccountPasswordCommand(authenticated.account.id.value, currentPassword, newPassword)
        accountApplicationService.changePassword(command) match {
          case Success(_) => Ok(Json.obj("result" -> "OK"))
          case Failure(ex) => BadRequest(Json.obj("result" -> "NG"))
        }
    }.recoverTotal { _ => BadRequest(Json.obj("status" -> "NG")) }
  }
  
  def changeName = AuthAction(parse.json) { implicit authenticated =>
    authenticated.request.body.validate[(String)].map {
      case (name) =>
        val command = ChangeAccountNameCommand(authenticated.account.id.value, name)
        accountApplicationService.changeName(command) match {
          case Success(_) => Ok(Json.obj("result" -> "OK"))
          case Failure(ex) => InternalServerError(Json.obj("result" -> "NG"))
        }
    }.recoverTotal { _ => BadRequest(Json.obj("status" -> "NG")) }
    
  }

}

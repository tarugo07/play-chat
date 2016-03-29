package port.adapter.api.controllers

import application.account._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

import scala.util.{Failure, Success}

case class ChangePassword(currentPassword: String, newPassword: String)

case class ChangeName(name: String)

case class ChangeMail(mail: String)

class AccountController extends Controller with ControllerSupport {

  implicit val changePasswordReads: Reads[ChangePassword] = (
    (__ \ "current_password").read[String] and
      (__ \ "new_password").read[String]
    ) (ChangePassword.apply _)

  implicit val changeNameReads: Reads[ChangeName] = (__ \ "name").read[String].map(ChangeName)

  implicit val changeMailReads: Reads[ChangeMail] = (__ \ "mail").read[String].map(ChangeMail)

  val accountApplicationService = new AccountApplicationService(accountRepository)

  def changePassword = AuthAction(parse.json) { implicit authenticated =>
    authenticated.request.body.validate[ChangePassword].map {
      case changePassword =>
        val command = ChangeAccountPasswordCommand(
          authenticated.account.id.value, changePassword.currentPassword, changePassword.newPassword
        )
        accountApplicationService.changePassword(command) match {
          case Success(_) =>
            Ok(Json.obj("result" -> "OK"))
          case Failure(ex) =>
            BadRequest(Json.obj("result" -> "NG"))
        }
    }.recoverTotal { _ =>
      BadRequest(Json.obj("status" -> "NG"))
    }
  }

  def changeName = AuthAction(parse.json) { implicit authenticated =>
    authenticated.request.body.validate[ChangeName].map {
      case changeName =>
        val command = ChangeAccountNameCommand(authenticated.account.id.value, changeName.name)
        accountApplicationService.changeName(command) match {
          case Success(_) =>
            Ok(Json.obj("result" -> "OK"))
          case Failure(ex) =>
            InternalServerError(Json.obj("result" -> "NG"))
        }
    }.recoverTotal { _ =>
      BadRequest(Json.obj("status" -> "NG"))
    }
  }

  def changeMail = AuthAction(parse.json) { implicit authenticated =>
    authenticated.request.body.validate[ChangeMail].map {
      case changeMail =>
        val command = ChangeAccountMailCommand(authenticated.account.id.value, changeMail.mail)
        accountApplicationService.changeMail(command) match {
          case Success(_) =>
            Ok(Json.obj("result" -> "OK"))
          case Failure(ex) =>
            InternalServerError(Json.obj("result" -> "NG"))
        }
    }.recoverTotal { _ =>
      BadRequest(Json.obj("status" -> "NG"))
    }

  }

}

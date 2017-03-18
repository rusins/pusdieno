package controllers

import java.util.UUID
import javax.inject.Inject

import auth.CookieEnv
import com.mohiva.play.silhouette.api.Silhouette
import models.db.User
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc._
import views.EateriesView
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.UserServiceOld

import scala.concurrent.ExecutionContext

case class EateryForm(eatery: String, status: String)

class EateriesController @Inject()(val messagesApi: MessagesApi, silhouette: Silhouette[CookieEnv], eateries: EateriesView)
  extends Controller with I18nSupport {

  def eaterySelection: Action[AnyContent] = silhouette.UserAwareAction.async {
    implicit request =>
      eateries.index("eateries", request.identity).map(Ok(_))
  }

  def cafeSelection: Action[AnyContent] = silhouette.UserAwareAction.async {
    implicit request =>
      eateries.index("cafes", request.identity).map(Ok(_))
  }

  def eat() = silhouette.SecuredAction { implicit request =>
    val template = Form(mapping("eatery" -> nonEmptyText, "status" -> nonEmptyText)(EateryForm.apply)(EateryForm.unapply))
    template.bindFromRequest.fold(
      erroneousForm => {
        println("Eatery form with wrong data sent: " + erroneousForm.data)
        BadRequest("Invalid eatery form sent")
      },
      form => {
        println(s"User ${request.identity.name} Changed ${form.eatery} status to ${form.status}")
        Ok(form.eatery + " " + form.status)
      } // TODO: actually do server side stuff with the data from the user
    )
  }
}

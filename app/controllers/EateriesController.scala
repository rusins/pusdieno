package controllers

import javax.inject.Inject

import utils.CookieEnv
import com.mohiva.play.silhouette.api.Silhouette
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import services.daos.Choices
import views.EateriesView

import scala.concurrent.Future
import scala.util.Failure

case class EateryForm(eatery: String, status: String)

class EateriesController @Inject()(val messagesApi: MessagesApi, silhouette: Silhouette[CookieEnv],
                                   eateries: EateriesView, choices: Choices)
  extends Controller with I18nSupport {

  def eaterySelection: Action[AnyContent] = silhouette.UserAwareAction.async {
    implicit request =>
      eateries.index("eateries", request.identity).map(Ok(_))
  }

  def cafeSelection: Action[AnyContent] = silhouette.UserAwareAction.async {
    implicit request =>
      eateries.index("cafes", request.identity).map(Ok(_))
  }

  def eat() = silhouette.SecuredAction.async { implicit request =>
    val template = Form(mapping("eatery" -> nonEmptyText, "status" -> nonEmptyText)(EateryForm.apply)(EateryForm.unapply))
    template.bindFromRequest.fold(
      erroneousForm => {
        println("Eatery form with wrong data sent: " + erroneousForm.data)
        Future.successful(BadRequest("Invalid eatery form sent"))
      },
      form => {
        println(s"User ${request.identity.name} Changed ${form.eatery} status to ${form.status}")
        (form.status match {
          case "yes" =>
            choices.clearChoices(request.identity.id).flatMap(
              _ => choices.makeChoice(request.identity.id, form.eatery)
            )
          case "maybe" =>
            choices.makeChoice(request.identity.id, form.eatery)
          case "no" =>
            choices.deleteChoice(request.identity.id, form.eatery)
          case _ => Future.failed(new RuntimeException("Unknown eatery status received!"))
        }).map(_ => Ok(form.eatery + " " + form.status)).recover({
          case (throwable: Throwable) => BadRequest("Invalid eatery form sent. Exception: " + throwable.toString)
        })
      }
    )
  }
}

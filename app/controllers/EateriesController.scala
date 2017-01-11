package controllers

import javax.inject.Inject

import com.sun.imageio.plugins.common.I18N
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc._
import views.EateriesView
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.ExecutionContext

case class EateryForm(eatery: String, status: String)

class EateriesController @Inject()(val messagesApi: MessagesApi, eateries: EateriesView) extends Controller with I18nSupport {

  def index: Action[AnyContent] = Action.async {
    implicit request =>
    eateries.eaterySelection().map(Ok(_))
  }

  def eat() = Action { implicit request =>
    val template = Form(mapping("eatery" -> nonEmptyText, "status" -> nonEmptyText)(EateryForm.apply)(EateryForm.unapply))
    template.bindFromRequest.fold(
      erroneousForm => {
        println("Eatery form with wrong data sent: " + erroneousForm.data)
        BadRequest("Invalid eatery form sent")
      },
      form => {
        println(s"Changed ${form.eatery} status to ${form.status}")
        Ok(form.eatery + " " + form.status)
      } // TODO: actually do server side stuff with the data from the user
    )
  }
}

package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

case class EateryForm(eatery: String, status: String)

class EateriesController @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action{ implicit request =>
    Ok(views.html.eateries())
  }

  def eat() = Action { implicit request =>
    val template = Form(mapping("eatery" -> nonEmptyText, "status" -> nonEmptyText)(EateryForm.apply)(EateryForm.unapply))
    template.bindFromRequest.fold(
      erroneousForm => {println("Eatery form with wrong data sent: " + erroneousForm.data); BadRequest("Invalid eatery form sent")},
      form => Ok(form.eatery + " " + form.status) // TODO: actually do server side stuff with the data from the user
    )
  }
}

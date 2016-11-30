package controllers

import javax.inject._

import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import views.html.{formTest, main}

case class formData(name: String, age: Option[Int])

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  val index = Action { implicit request =>
    Ok(views.html.formTest(form))
  }

  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "age" -> optional(number(min = 18, max = 69))
    )(formData.apply)(formData.unapply)
  )

  val submitForm = Action { implicit request =>
  form.bindFromRequest().fold(
    erroneousForm => {println("Form had errors – returning user to the form");BadRequest(views.html.formTest(erroneousForm))},
    validForm =>  {println("Form submitted successfully");Ok("Awesome!!!! :D ")}
  )}
}

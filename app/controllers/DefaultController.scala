package controllers

import javax.inject.{Inject, Singleton}

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.filters.csrf.CSRFAddToken

@Singleton
class DefaultController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def welcome = Action{ implicit request => Ok(views.html.welcome())}

  def index = Action { implicit request =>
    if (false) // TODO: User logged in
      Redirect(routes.OverviewController.index())
    else
      Redirect(routes.DefaultController.welcome())
  }
}

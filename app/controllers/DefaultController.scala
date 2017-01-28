package controllers

import javax.inject.{Inject, Singleton}

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import views.WelcomeView
import play.api.libs.concurrent.Execution.Implicits.defaultContext

@Singleton
class DefaultController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def welcome: Action[AnyContent] = Action.async {
    implicit request =>
      WelcomeView.index().map(Ok(_))
  }

  def index = Action { implicit request =>
    if (false) // TODO: User logged in
      Redirect(routes.OverviewController.index())
    else
      Redirect(routes.DefaultController.welcome())
  }
}

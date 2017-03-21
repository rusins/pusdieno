package controllers

import javax.inject.Inject

import auth.CookieEnv
import com.mohiva.play.silhouette.api.Silhouette
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import views.WelcomeView

import scala.concurrent.Future

class DefaultController @Inject()(val messagesApi: MessagesApi, silhouette: Silhouette[CookieEnv]) extends Controller with I18nSupport {
  def welcome: Action[AnyContent] = Action.async {
    implicit request =>
      WelcomeView.index().map(Ok(_))
  }

  def index: Action[AnyContent] = silhouette.UserAwareAction.async {
    implicit request => request.identity match {
        case Some(user) => Future.successful(Redirect("/overview"))
        case None => Future.successful(Redirect("/welcome"))
      }
  }
}

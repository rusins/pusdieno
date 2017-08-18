package controllers

import javax.inject.Inject

import utils.CookieEnv
import com.mohiva.play.silhouette.api.Silhouette
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import views.WelcomeView

import scala.concurrent.{ExecutionContext, Future}

class DefaultController @Inject()(silhouette: Silhouette[CookieEnv])
                                 (implicit ex: ExecutionContext)
  extends InjectedController with I18nSupport {

  def welcome: Action[AnyContent] = Action {
    implicit request =>
      Ok(WelcomeView.index())
  }

  def index: Action[AnyContent] = silhouette.UserAwareAction.async {
    implicit request =>
      request.identity match {
        case Some(_) => Future.successful(Redirect("/overview"))
        case None => Future.successful(Redirect("/welcome"))
      }
  }
}

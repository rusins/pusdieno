package controllers

import javax.inject.Inject

import utils.CookieEnv
import com.mohiva.play.silhouette.api.Silhouette
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import views.FriendsView

import scala.concurrent.{ExecutionContext, Future}

class FriendsController @Inject()(friends: FriendsView, silhouette: Silhouette[CookieEnv])
                                 (implicit ex: ExecutionContext)
  extends InjectedController with I18nSupport {

  def index: Action[AnyContent] = silhouette.UserAwareAction.async {
    implicit request => request.identity match {
      case Some(user) => friends.index(user).map(Ok(_))
      case None => Future.successful(Redirect(routes.AuthController.signIn()))
    }
  }
}

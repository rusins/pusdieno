package controllers

import javax.inject.Inject

import utils.CookieEnv
import com.mohiva.play.silhouette.api.Silhouette
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import views.FriendsView

import scala.concurrent.Future

class FriendsController @Inject()(val messagesApi: MessagesApi, friends: FriendsView, silhouette: Silhouette[CookieEnv])
  extends Controller with I18nSupport {

  def index: Action[AnyContent] = silhouette.UserAwareAction.async {
    implicit request => request.identity match {
      case Some(user) => friends.index(user).map(Ok(_))
      case None => Future.successful(Redirect(routes.AuthController.signIn()))
    }
  }
}

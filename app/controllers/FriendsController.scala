package controllers

import java.util.UUID
import javax.inject.Inject

import auth.CookieEnv
import com.mohiva.play.silhouette.api.Silhouette
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import views.FriendsView

class FriendsController @Inject()(val messagesApi: MessagesApi, friends: FriendsView, silhouette: Silhouette[CookieEnv])
  extends Controller with I18nSupport {

  def index: Action[AnyContent] = silhouette.SecuredAction.async {
    implicit request => friends.index(request.identity).map(Ok(_))
  }
}

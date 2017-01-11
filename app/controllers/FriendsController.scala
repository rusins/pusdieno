package controllers

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import views.FriendsView

class FriendsController @Inject()(val messagesApi: MessagesApi, friends: FriendsView) extends Controller with I18nSupport {

  def index: Action[AnyContent] = Action.async {
    implicit request => friends.index().map(Ok(_))
  }
}

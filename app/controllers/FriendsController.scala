package controllers

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import views.Friends

import scala.concurrent.Future

class FriendsController @Inject()(val messagesApi: MessagesApi, friends: Friends) extends Controller with I18nSupport {

  def index = Action.async {
    implicit request => friends.index().map(Ok(_))
  }
}

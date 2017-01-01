package controllers

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

class FriendsController @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action{ implicit request =>
    Ok(views.Friends())
  }
}

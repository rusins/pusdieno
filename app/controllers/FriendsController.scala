package controllers

import java.util.UUID
import javax.inject.Inject

import models.WeekPlan
import models.db.User
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import views.FriendsView

class FriendsController @Inject()(val messagesApi: MessagesApi, friends: FriendsView) extends Controller with I18nSupport {

  val user = User(id = UUID.fromString("00000000-0000-0000-0000-000000000000"), name = "Public", eatsAt = WeekPlan.empty)

  def index: Action[AnyContent] = Action.async {
    implicit request => friends.index(user).map(Ok(_))
  }
}

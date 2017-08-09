package controllers

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import views.ErrorView

class AboutController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index: Action[AnyContent] = Action { implicit request =>
    Ok(ErrorView.unimplemented())
  }
}

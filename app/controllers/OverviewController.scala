package controllers

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import views.ErrorView

class OverviewController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index: Action[AnyContent] = Action.async { implicit request =>
    ErrorView.unimplemented().map(Ok(_))
  }
}


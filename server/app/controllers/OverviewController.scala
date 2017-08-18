package controllers

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.filters.csrf.{CSRF, CSRFAddToken}
import views.ErrorView

class OverviewController @Inject()() extends InjectedController with I18nSupport {

  def index: Action[AnyContent] = Action { implicit request =>
    Ok(ErrorView.unimplemented())
  }
}


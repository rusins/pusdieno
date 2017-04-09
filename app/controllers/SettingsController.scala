package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller
import utils.CookieEnv
import views.ErrorView

class SettingsController @Inject() (implicit val messagesApi: MessagesApi, silhouette: Silhouette[CookieEnv])
extends Controller with I18nSupport{

  def index = silhouette.SecuredAction { implicit request =>
    Ok(ErrorView.unimplemented())
  }
}

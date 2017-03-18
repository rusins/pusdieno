package controllers

import javax.inject.Inject

import auth.CookieEnv
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import views.SignInView

import scala.concurrent.Future

class SignInController @Inject()(silhouette: Silhouette[CookieEnv], socialProviderRegistry: SocialProviderRegistry,
                                 val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  def index: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request =>
    SignInView(socialProviderRegistry).map(Ok(_))
  }
}

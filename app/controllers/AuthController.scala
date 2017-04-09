package controllers

import javax.inject.Inject

import utils.CookieEnv
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.{Logger, LoginEvent, LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.impl.providers.{CommonSocialProfileBuilder, SocialProvider, SocialProviderRegistry}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Controller}
import services.daos.Users
import views.SignInView

import scala.concurrent.Future

class AuthController @Inject()(val messagesApi: MessagesApi,
                               silhouette: Silhouette[CookieEnv],
                               userService: Users,
                               authInfoRepository: AuthInfoRepository,
                               socialProviderRegistry: SocialProviderRegistry,
                               ws: WSClient)
  extends Controller with I18nSupport with Logger {

  def signIn: Action[AnyContent] = silhouette.UnsecuredAction { implicit request =>
    Ok(SignInView(socialProviderRegistry))
  }

  def signOut: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.DefaultController.index())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }

  /**
    * Authenticates a user against a social provider.
    *
    * @param provider The ID of the provider to authenticate against.
    * @return The result to display.
    */
  def authenticate(provider: String): Action[AnyContent] = Action.async { implicit request =>
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(provider: SocialProvider with CommonSocialProfileBuilder) =>
        provider.authenticate().flatMap {
          case Left(result) => Future.successful(result) // Redirect user to social provider
          case Right(authInfo) => for {
            profile <- provider.retrieveProfile(authInfo)
            user <- userService.save(profile)
            authInfo <- authInfoRepository.save(profile.loginInfo, authInfo)
            authenticator <- silhouette.env.authenticatorService.create(profile.loginInfo)
            cookie <- silhouette.env.authenticatorService.init(authenticator)
            result <- silhouette.env.authenticatorService.embed(cookie, Redirect(routes.EateriesController.eaterySelection()))
          } yield {
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            println(user.name + " logged in!")
            result
          }
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
    }).recover {
      case e: ProviderException =>
        logger.error("Unexpected provider error", e)
        Redirect(routes.AuthController.signIn()).flashing("error" -> Messages("could.not.authenticate"))
    }
  }
}

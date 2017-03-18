package auth

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.db.User
import play.api.mvc.Request

import scala.concurrent.Future

case class WithProvider(provider: ProviderEnum) extends Authorization[User, CookieAuthenticator] {

  // I think this class will never be used

  def isAuthorized[B](user: User, authenticator: CookieAuthenticator)(implicit request: Request[B]): Future[Boolean] =
    Future.successful(
      provider match {
        case provider.GOOGLE => user.googleID.contains(authenticator.loginInfo.providerID)
        case provider.FACEBOOK => user.facebookID.contains(authenticator.loginInfo.providerID)
      }
    )
}

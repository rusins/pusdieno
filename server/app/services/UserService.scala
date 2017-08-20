package services

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.User

import scala.concurrent.Future

/**
  * This trait is used both in Silhouette magic, and throughout the entire app
  */
trait UserService extends IdentityService[User] {

  def save(profile: CommonSocialProfile): Future[User]

}

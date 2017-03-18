package services

import java.util.UUID
import javax.inject.{Inject, Singleton}

import auth.ProviderEnum
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.WeekPlan
import models.db.{User, UserTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future
import scala.util.Random

@Singleton
class UserServiceOld @Inject()(dbConfigProvider: DatabaseConfigProvider) extends IdentityService[User] {

  private val db = dbConfigProvider.get[JdbcProfile].db

  private val users = TableQuery[UserTable]

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] =
    db.run(users.filter(users =>
      (users.googleID === loginInfo.providerID && users.googleKey === loginInfo.providerKey) ||
        (users.facebookID === loginInfo.providerID && users.facebookKey === loginInfo.providerKey)
    ).result.headOption)

  private def fromSocialProfile(provider: ProviderEnum, profile: CommonSocialProfile): User = {
    val (googleID, googleKey, facebookID, facebookKey) = provider match {
      case provider.GOOGLE => (Some(profile.loginInfo.providerID), Some(profile.loginInfo.providerKey), None, None)
      case provider.FACEBOOK => (None, None, Some(profile.loginInfo.providerID), Some(profile.loginInfo.providerKey))
    }
    User(
      name = profile.fullName.getOrElse("User" + Random.nextInt()),
      email = profile.email,
      eatsAt = WeekPlan.empty,
      googleID = googleID,
      googleKey = googleKey,
      facebookID = facebookID,
      facebookKey = facebookKey,
      avatarURL = profile.avatarURL
    )
  }

 // def save(profile: CommonSocialProfile):

  def add(user: User): Future[Int] = db.run(users += user)

  def del(id: UUID): Future[Int] = db.run(users.filter(_.id === id).delete)

  def retrieve(id: UUID): Future[Option[User]] = db.run(users.filter(_.id === id).result.headOption)

  def retrieveAll(): Future[Seq[User]] = db.run(users.result)
}

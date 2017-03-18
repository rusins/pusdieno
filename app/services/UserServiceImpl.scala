package services
import java.util.UUID
import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.{CommonSocialProfile, SocialProviderRegistry}
import models.WeekPlan
import models.db.{User, UserTable}
import slick.driver.PostgresDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import scala.util.Random

@Singleton
class UserServiceImpl @Inject()(dbConfigProvider: DatabaseConfigProvider, socialProviderRegistry: SocialProviderRegistry) extends UserService {

  private val db = dbConfigProvider.get[JdbcProfile].db

  private val users = TableQuery[UserTable]

  override def retrieve(id: UUID): Future[Option[User]] = db.run(users.filter(_.id === id).result.headOption)

  override def save(user: User): Future[User] = db.run(users += user).map(_ => user)


  override def save(profile: CommonSocialProfile): Future[User] = {
    val user = User(name = profile.fullName.getOrElse(profile.firstName.getOrElse(
      profile.lastName.getOrElse(Random.nextInt().toString))),
      mobile = None,
      email = profile.email,
      eatsAt = WeekPlan.empty,
      googleID = Some(profile.loginInfo.providerID),
      googleKey = Some(profile.loginInfo.providerKey),
      facebookID = None, facebookKey = None) // TODO: CRITICAL: ASSUMING ALL LOGINS HAPPEN THROUGH GOOGLE!!!
    save(user)
  }

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = ???
}

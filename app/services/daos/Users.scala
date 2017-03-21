package services.daos

import java.util.UUID
import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.{Logger, LoginInfo}
import com.mohiva.play.silhouette.impl.providers.{CommonSocialProfile, SocialProviderRegistry}
import models.db._
import models.{EatsAt, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.daos.Users._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future
import scala.util.Random

@Singleton
class Users @Inject()(dbConfigProvider: DatabaseConfigProvider, socialProviderRegistry: SocialProviderRegistry)
  extends IdentityService[User] with Logger {

  private val db = dbConfigProvider.get[JdbcProfile].db

  def retrieve(id: UUID): Future[Option[User]] = db.run(getFromId(id).result.headOption).map(
    _.map((User.fromDB _).tupled))

  def save(user: User): Future[User] =
    db.run(users += user.toDB).map(_ => user)


  def save(profile: CommonSocialProfile): Future[User] = {

    // Not gonna bother with updating, just gonna save

    val user = User(name = profile.fullName.getOrElse(profile.firstName.getOrElse(
      profile.lastName.getOrElse(Random.nextInt().toString))),
      mobile = None,
      email = profile.email,
      eatsAt = EatsAt(None, None, None),
      avatarURL = profile.avatarURL)

    save(user)
  }

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = db.run(
    (for {
      id <- logins.filter(l => l.providerKey === loginInfo.providerKey &&
        l.providerID === loginInfo.providerID).map(_.userID)
      userInfo <- getFromId(id)
    } yield userInfo).result.headOption).map(_.map((User.fromDB _).tupled))

}

object Users {

  private val users = TableQuery[DBUserTable]
  private val logins = TableQuery[DBLoginInfoTable]
  private val weekTimes = TableQuery[DBWeekTimesTable]

  def getFromId(id: Rep[UUID]):
  Query[(DBUserTable, Rep[Option[DBWeekTimesTable]], Rep[Option[DBWeekTimesTable]], Rep[Option[DBWeekTimesTable]]),
    (DBUser, Option[DBWeekTimes], Option[DBWeekTimes], Option[DBWeekTimes]), Seq] = for {
    dbUser <- users.filter(_.id === id)
    breakfast <- users.joinLeft(weekTimes).on(_.breakfastFK === _.id).map { case (u, t) => t }
    lunch <- users.joinLeft(weekTimes).on(_.lunchFK === _.id).map { case (u, t) => t }
    dinner <- users.joinLeft(weekTimes).on(_.dinnerFK === _.id).map { case (u, t) => t }
  } yield (dbUser, breakfast, lunch, dinner)

}
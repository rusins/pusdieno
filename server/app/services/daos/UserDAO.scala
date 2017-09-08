package services.daos

import java.util.UUID
import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.{Logger, LoginInfo}
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.db._
import models.{EatsAt, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.{ContactService, UserService}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future
import scala.util.Random

@Singleton
final class UserDAO @Inject()(dbConfigProvider: DatabaseConfigProvider, contacts: ContactService,
                              oAuth2InfoDAO: OAuth2InfoDAO) extends UserService
  with Logger with TimeTable {

  protected[daos] def getFromID(id: Rep[UUID]) = for {
    dbUser <- users.filter(_.id === id)
    breakfast <- users.joinLeft(weekTimes).on(_.breakfastFK === _.id).map { case (u, t) => t }
    lunch <- users.joinLeft(weekTimes).on(_.lunchFK === _.id).map { case (u, t) => t }
    dinner <- users.joinLeft(weekTimes).on(_.dinnerFK === _.id).map { case (u, t) => t }
  } yield (dbUser, breakfast, lunch, dinner)

  private val db = dbConfigProvider.get[JdbcProfile].db

  protected[daos] val users = TableQuery[DBUserTable]
  private val logins = oAuth2InfoDAO.logins

  def retrieve(id: UUID): Future[Option[User]] = db.run(getFromID(id).result.headOption).map(
    _.map((User.fromDB _).tupled))

  def save(user: User): Future[User] =
    db.run(users += user.toDB).map(_ => user)


  def save(profile: CommonSocialProfile): Future[User] = {

    println("Saving user...")
    // Not gonna bother with updating, just gonna save
    // Also no idea how multi-auth login works ¯\_(ツ)_/¯
    // TODO: This code probably doesn't work in the long-run

    db.run(
      (for {
        l <- logins.filter(login => login.providerKey === profile.loginInfo.providerKey &&
          login.providerID === profile.loginInfo.providerID)
      } yield l).result.headOption
    ).flatMap {
      case Some(dbLoginInfo) => db.run(getFromID(dbLoginInfo.userID).result.head).map((User.fromDB _).tupled)
      case None => {

        val user = User(name = profile.fullName.getOrElse(profile.firstName.getOrElse(
          profile.lastName.getOrElse(Random.nextInt().toString))),
          phone = None,
          email = profile.email,
          eatsAt = EatsAt(None, None, None),
          avatarURL = profile.avatarURL)

        println("New user! " + user.name)

        val dbLoginInfo = DBLoginInfo(UUID.randomUUID(), profile.loginInfo.providerID, profile.loginInfo.providerKey, user.id)

        db.run(
          (for {
            _ <- users += user.toDB
            _ <- logins += dbLoginInfo
          } yield ()).transactionally
        ).flatMap(_ => contacts.befriendNewUser(user.id, user.phone, user.email)).map(_ => user)
      }
    }
  }

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = db.run(
    (for {
      id <- logins.filter(l => l.providerKey === loginInfo.providerKey &&
        l.providerID === loginInfo.providerID).map(_.userID)
      userInfo <- getFromID(id)
    } yield userInfo).result.headOption).map(_.map((User.fromDB _).tupled))

  /*
  old code that should work but fucking didn't because of Slick :(
  def getFromID(id: Rep[UUID]) = for {
    (((dbUser, bk), lu), di) <- users.filter(_.id === id).joinLeft(weekTimes).on(_.breakfastFK === _.id).
      joinLeft(weekTimes).on(_._1.lunchFK === _.id).joinLeft(weekTimes).on(_._1._1.dinnerFK === _.id)
  } yield (dbUser, bk, lu, di)
  */

}
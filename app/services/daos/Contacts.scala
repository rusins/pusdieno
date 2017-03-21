package services.daos

import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.User
import models.db._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.daos.Contacts._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

@Singleton
class Contacts @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  private val db = dbConfigProvider.get[JdbcProfile].db

  def contactsOfUser(userID: UUID): Future[Seq[Contact]] = db.run(contacts.filter(_.ownerID === userID).result)

  def friendsWithContactInfo(userID: UUID): Future[Seq[(Contact, User)]] =
    db.run(friendsWithContactInfoQuery(userID).result).map(_.map {
      case (contact, userInfo) =>
        (contact, (User.fromDB _).tupled(userInfo))
    })

  def friendsWithStatusInfo(userID: UUID): Future[Seq[(Contact, User, Boolean, Boolean)]] =
    db.run(
      (for {
        (contact, userInfo) <- friendsWithContactInfoQuery(userID)
      } yield (contact, userInfo, Choices.wantsFood(userID), Choices.wantsCoffee(userID))).result
    ).map(_.map {
      case (contact, userInfo, wantsFood, wantsCoffee) => (contact, (User.fromDB _).tupled(userInfo), wantsFood, wantsCoffee)
    })

}

object Contacts {
  private val contacts = TableQuery[ContactTable]

  def friendsOfUserAction(userID: Rep[UUID]): Query[ContactTable, Contact, Seq] = for {
    c <- contacts.filter(_.ownerID === userID)
    f <- contacts.filter(friend => friend.ownerID === c.contactID && friend.contactID === c.ownerID)
  } yield f

  private def friendsWithContactInfoQuery(userID: UUID):
  Query[(ContactTable,
    (DBUserTable, Rep[Option[DBWeekTimesTable]], Rep[Option[DBWeekTimesTable]], Rep[Option[DBWeekTimesTable]])),
    (Contact, (DBUser, Option[DBWeekTimes], Option[DBWeekTimes], Option[DBWeekTimes])), Seq] = for {
    contact <- friendsOfUserAction(userID)
    userInfo <- Users.getFromId(contact.ownerID)
  } yield (contact, userInfo)

}

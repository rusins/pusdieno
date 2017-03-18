package services.daos

import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.db.{Contact, ContactTable, User, UserTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

@Singleton
class Contacts @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  private val db = dbConfigProvider.get[JdbcProfile].db

  private val contacts = TableQuery[ContactTable]

  def contactsOfUser(userID: UUID): Future[Seq[User]] = db.run(
    (for {
      c <- contacts.filter(_.ownerID === userID)
      u <- c.pointsTo
    } yield u).result
  )

  def friendsOfUser(userID: UUID): Future[Seq[User]] = db.run(
    (for {
      (_, u) <- friendsOfUserAction(userID)
    } yield u).result)

  def friendsOfUserAction(userID: UUID): Query[(ContactTable, UserTable), (Contact, User), Seq] = for {
    c <- contacts.filter(_.ownerID === userID)
    u <- c.pointsTo if contacts.filter(friend => friend.ownerID === c.contactID && friend.contactID === c.ownerID).exists
  } yield (c, u)

}

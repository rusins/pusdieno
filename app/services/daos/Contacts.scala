package services.daos

import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.User
import models.db.{Contact, ContactTable, DBUserTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.QueryBase

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

@Singleton
class Contacts @Inject()(dbConfigProvider: DatabaseConfigProvider, users: Users) {

  private val db = dbConfigProvider.get[JdbcProfile].db

  private val contacts = TableQuery[ContactTable]

  def contactsOfUser(userID: UUID): Future[Seq[Contact]] = db.run(contacts.filter(_.ownerID === userID).result)

  def friendsOfUserAction(userID: Rep[UUID]): Query[ContactTable, Contact, Seq] = for {
    c <- contacts.filter(_.ownerID === userID)
    f <- contacts.filter(friend => friend.ownerID === c.contactID && friend.contactID === c.ownerID)
  } yield f

  def friendsOfUser(userID: UUID): Future[Seq[User]] = db.run(
    (for {
      c <- friendsOfUserAction(userID)
      u <- users.getFromId(c.ownerID)
    } yield u).result)

}

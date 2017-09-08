package services.daos

import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.{Contact, User}
import models.db._
import models.helpers.Lusts
import play.api.db.slick.DatabaseConfigProvider
import services.{ChoiceService, ContactService}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import utils.LoggingSupport

import scala.concurrent.{ExecutionContext, Future}

// TODO: Figure out how to not be dependent on User table

@Singleton
final class ContactDAO @Inject()(dbConfigProvider: DatabaseConfigProvider, userDAO: UserDAO, choiceDAO: ChoiceDAO,
                                 ex: ExecutionContext) extends ContactService with LoggingSupport {

  // BEGIN helper queries

  private def contactQuery(userID: Rep[UUID]) = contacts.filter(_.ownerID === userID)

  protected[daos] def friendQuery(userID: Rep[UUID]): Query[ContactTable, Contact, Seq] = for {
    c <- contactQuery(userID)
    f <- contacts.filter(friend => friend.ownerID === c.contactID && friend.contactID === userID)
  } yield f

  private def friendsWithContactInfoQuery(userID: UUID) = for {
    contact <- friendQuery(userID)
    userInfo <- userDAO.getFromID(contact.ownerID)
  } yield (userInfo, contact)


  // END helper queries

  private val db = dbConfigProvider.get[JdbcProfile].db

  protected[daos] val contacts = TableQuery[ContactTable]
  protected[daos] val users = userDAO.users

  override def contactsOfUser(userID: UUID): Future[Seq[Contact]] = db.run(contactQuery(userID).result)

  override def friendsOfUser(userID: UUID): Future[Seq[User]] = db.run(
    (for {
      friend <- friendQuery(userID)
      userInfo <- userDAO.getFromID(friend.ownerID)
    } yield userInfo).result
  ).map(_.map((User.fromDB _).tupled))

  /*
  Note: this function can probably be opimized by having SQL figure out how to take all users, and then the user info
  for the contacts that are friends. It seems complicated, which is why I just did it in Scala.
   */
  def contactsWithFriendsOfUser(userID: UUID): Future[Map[Contact, Option[User]]] = {
    val futures = for {
      c <- contactsOfUser(userID)
      f <- friendsWithContactInfo(userID)
    } yield (c, f)

    futures.map { case (allContacts: Seq[Contact], friendlyContacts: Map[User, Contact]) =>
      allContacts.map(c => (c, None)).toMap ++ friendlyContacts.map(_.swap).mapValues(user => Some(user))
    }
  }

  override def friendsWithContactInfo(userID: UUID): Future[Map[User, Contact]] =
    db.run(friendsWithContactInfoQuery(userID).result).map(_.map {
      case (userInfo, contact) =>
        ((User.fromDB _).tupled(userInfo), contact)
    }).map(_.toMap)

  override def friendsWithStatusInfo(userID: UUID): Future[Map[User, Lusts]] =
    db.run(
      (for {
        (userInfo, _) <- friendsWithContactInfoQuery(userID)
      } yield (userInfo, choiceDAO.wantsFoodQuery(userInfo._1.id), choiceDAO.wantsCoffeeQuery(userInfo._1.id))).result
    ).map(_.map {
      case (userInfo, wantsFood, wantsCoffee) => ((User.fromDB _).tupled(userInfo), Lusts(wantsFood, wantsCoffee, wantsAlcohol = false))
    }).map(_.toMap)

  override def belongsTo(contactID: UUID): Future[Option[UUID]] =
    db.run(contacts.filter(c => c.id === contactID).map(_.ownerID).result.headOption)

  override def get(contactID: UUID): Future[Option[Contact]] = db.run(contacts.filter(_.id === contactID).result.headOption)

  override def save(contact: Contact): Future[Boolean] = {
    db.run(contacts.insertOrUpdate(contact)).flatMap { affectedRowCount =>
      logger.debug("Saved (inserted or updated) a contact")
      contact.phone match {
        case None => Future.successful(None)
        case Some(phone) => db.run(users.filter(_.phone === phone).map(_.id).result.headOption)
      }
    } flatMap { idO =>
      contact.email match {
        case None => Future(idO)
        case Some(email) => db.run(users.filter(_.email === email).map(_.id).result.headOption)
      }
    } flatMap {
      case None => Future.successful(false)
      case Some(id) => db.run(contacts.filter(_.id === contact.id).map(_.contactID).update(Some(id)))
        .map(_ => true)
    }
  }

  override def delete(contactID: UUID): Future[Int] = db.run(contacts.filter(_.id === contactID).delete)

  override def befriendNewUser(userID: UUID, phoneO: Option[Int], emailO: Option[String]): Future[Int] =
    (phoneO, emailO) match {
      case (None, None) => Future.successful(0)
      case (Some(phone), None) => db.run(contacts.filter(_.contactPhone === phone).map(_.contactID).update(Some(userID)))
      case (None, Some(email)) => db.run(contacts.filter(_.contactEmail === email).map(_.contactID).update(Some(userID)))
      case (Some(phone), Some(email)) => db.run((
        contacts.filter(_.contactPhone === phone) union
          contacts.filter(_.contactEmail == email)).map(_.contactID).update(Some(userID)))
    }
}

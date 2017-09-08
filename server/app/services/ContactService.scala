package services

import java.util.UUID

import models.helpers.Lusts
import models.{Contact, User}

import scala.concurrent.Future

/**
  * Warning! I suggest never implementing a method that would allow accessing the contacts of a user alongside the
  * actual User profiles associated with that contact, unless it's a friend! Such a method could be accidentally used
  * and cause a 1-sided friendship exploit, where you could see the other person's sensitive info before them friending
  * you back. Tl;DR: When accessing user data through this service, make sure the user is a friend!
  */
trait ContactService {

  def contactsOfUser(userID: UUID): Future[Seq[Contact]]

  def friendsOfUser(userID: UUID): Future[Seq[User]]

  /**
    *
    * @param userID The ID of the user who's contacts are queried.
    * @return Map of contacts, that may or not may have a User associated with them, depending on whether or not the
    *         users are friends.
    */
  def contactsWithFriendsOfUser(userID: UUID): Future[Map[Contact, Option[User]]]

  def friendsWithContactInfo(userID: UUID): Future[Map[User, Contact]]

  def friendsWithStatusInfo(userID: UUID): Future[Map[User, Lusts]]

  def belongsTo(contactID: UUID): Future[Option[UUID]]

  def get(contactID: UUID): Future[Option[Contact]]

  /**
    *
    * @param contact The contact to save
    * @return Whether or not the contact got matched to an existing user
    **/
  def save(contact: Contact): Future[Boolean]

  def delete(contactID: UUID): Future[Unit]

  /**
    * When a new user joins the site, his info gets matched against existing contacts and linked up.
    *
    * @param userID The user's ID
    * @param phone  The user's phone number
    * @param email  The user's email
    * @return The number of contacts that were matched
    */
  def befriendNewUser(userID: UUID, phone: Option[Int], email: Option[String]): Future[Int]
}

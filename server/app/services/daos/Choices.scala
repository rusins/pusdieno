package services.daos

import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.User
import models.db._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.daos.Choices._
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

@Singleton
class Choices @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  private val db = dbConfigProvider.get[JdbcProfile].db

  /*

  Slick bug: it isn't smart enough to figure this out

  def friendEateryChoiceMap(user: User): Future[Map[String, Seq[User]]] = db.run(
    (for {
      friend <- Contacts.friendsOfUserAction(user.id)
      choice <- eateryChoices.filter(_.user === friend.ownerID)
      eatery <- choice.pointsTo
      userInfo <- Users.getFromId(friend.ownerID)
    } yield (eatery.chainID, userInfo)).result.transactionally).map(_.map {
    case (chain, userInfo) => (chain, (User.fromDB _).tupled(userInfo))
  }).map(
    (seq: Seq[(String, User)]) => seq.groupBy(_._1).mapValues(_.map(_._2))
  )
   */

  def friendEateryChoiceMap(userID: UUID): Future[Map[String, Seq[User]]] = db.run(
    (for {
      f <- Contacts.friendsOfUserQuery(userID)
    } yield f).result).flatMap(seq =>
    Future(seq.flatMap(friend =>
      Await.result(db.run(
        (for {
          choice <- eateryChoices.filter(_.user === friend.ownerID)
          eatery <- TableQuery[DBEateryTable].filter(_.id === choice.eatery)
          userInfo <- Users.getFromID(friend.ownerID)
        } yield (eatery.chainID, userInfo)).result
      ), 1 second)
    ).map {
      case (chain, userInfo) => (chain, (User.fromDB _).tupled(userInfo))
    })
  ).map {
    (seq: Seq[(String, User)]) => seq.distinct.groupBy(_._1).mapValues(_.map(_._2))
  }

  def findEateryID(chain: String): Future[Option[UUID]] =
    db.run(eateries.filter(_.chainID === chain).map(_.id).result.headOption)

  def makeChoice(userID: UUID, chain: String): Future[AnyVal] = findEateryID(chain).flatMap {
    case None => Future.failed(new RuntimeException("Unknown chain ID!"))
    case Some(eateryID) => db.run(eateryChoices += Choice(user = userID, eatery = eateryID))
  }

  def deleteChoice(userID: UUID, chain: String): Future[AnyVal] = findEateryID(chain).flatMap {
    case None => Future.failed(new RuntimeException("Unknown chain ID!"))
    case Some(eateryID) => db.run(eateryChoices.filter(c => c.user === userID && c.eatery === eateryID).delete)
  }

  def clearChoices(userID: UUID): Future[Int] = db.run(eateryChoices.filter(_.user === userID).delete)

  def getChoices(userID: UUID): Future[Seq[String]] = db.run((
    for {
      choice <- eateryChoices.filter(_.user === userID)
      eatery <- choice.pointsTo
    } yield eatery.chainID).result)

}

object Choices {
  private val eateryChoices = TableQuery[EateryChoiceTable]
  private val cafeChoices = TableQuery[CafeChoiceTable]
  private val eateries = TableQuery[DBEateryTable]

  def wantsFood(userID: Rep[UUID]): Rep[Boolean] = eateryChoices.filter(_.user === userID).exists

  def wantsCoffee(userID: Rep[UUID]): Rep[Boolean] = cafeChoices.filter(_.user === userID).exists
}
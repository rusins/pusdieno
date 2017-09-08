package services.daos

import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.{Choice, User}
import models.db._
import models.helpers.Lusts
import play.api.db.slick.DatabaseConfigProvider
import services.{ChoiceService, UserService}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class ChoiceDAO @Inject()(dbConfigProvider: DatabaseConfigProvider, contactDAO: ContactDAO, userService: UserService,
                          ex: ExecutionContext) extends ChoiceService {

  private val choices = TableQuery[ChoiceTable]

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
    .flatMap(seq =>
    Future(seq.flatMap(friend =>
      Await.result(db.run(
        (for {
          choice <- eateryChoices.filter(_.user === friend.ownerID)
          eatery <- eateries.filter(_.id === choice.eatery)
          userInfo <- UserDAO.getFromID(friend.ownerID)
        } yield (eatery.chainID, userInfo)).result
      ), 1 second)
    ).map {
      case (chain, userInfo) => (chain, (User.fromDB _).tupled(userInfo))
    })
  ).map {
    (seq: Seq[(String, User)]) => seq.distinct.groupBy(_._1).mapValues(_.map(_._2))
  }

  def makeChoice(userID: UUID, chain: String): Future[AnyVal] = findEateryID(chain).flatMap {
    case None => Future.failed(new RuntimeException("Unknown chain ID!"))
    case Some(eateryID) => db.run(eateryChoices += Choice(user = userID, eatery = eateryID))
  }

  def deleteChoice(userID: UUID, chain: String): Future[AnyVal] = findEateryID(chain).flatMap {
    case None => Future.failed(new RuntimeException("Unknown chain ID!"))
    case Some(eateryID) => db.run(eateries.filter(c => c.user === userID && c.eatery === eateryID).delete)
  }

  def clearChoices(userID: UUID): Future[Int] = db.run(eateryChoices.filter(_.user === userID).delete)

  def getChoices(userID: UUID): Future[Seq[String]] = db.run((
    for {
      choice <- eateryChoices.filter(_.user === userID)
      eatery <- choice.pointsTo
    } yield eatery.chainID).result)

  protected[daos] def wantsFoodQuery(userID: Rep[UUID]): Rep[Boolean] = for {
    choice <- choices.filter(_.user === userID).filter(_.)

  }

  protected[daos] def wantsCoffeeQuery(userID: Rep[UUID]): Rep[Boolean] = ???

    def getLusts(userID: UUID): Future[Lusts]
}
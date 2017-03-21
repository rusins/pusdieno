package services.daos

import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.User
import models.db.{CafeChoiceTable, EateryChoiceTable}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.daos.Choices._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

@Singleton
class Choices @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  private val db = dbConfigProvider.get[JdbcProfile].db

  def friendEateryChoiceMap(user: User): Future[Map[String, Seq[User]]] = db.run(
    (for {
      friend <- Contacts.friendsOfUserAction(user.id)
      choice <- eateryChoices.filter(_.user === friend.ownerID)
      eatery <- choice.pointsTo
      user <- Users.getFromId(choice.user)
    } yield (eatery.chainID, user)).result).map(_.map {
    case (chain, userInfo) => (chain, (User.fromDB _).tupled(userInfo))
  }).map(
    (seq: Seq[(String, User)]) => seq.groupBy(_._1).mapValues(_.map(_._2))
  )
}

object Choices {
  private val eateryChoices = TableQuery[EateryChoiceTable]
  private val cafeChoices = TableQuery[CafeChoiceTable]

  def wantsFood(userID: UUID): Rep[Boolean] = eateryChoices.filter(_.user === userID).exists

  def wantsCoffee(userID: UUID): Rep[Boolean] = cafeChoices.filter(_.user === userID).exists
}
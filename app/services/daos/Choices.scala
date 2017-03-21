package services.daos

import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.User
import models.db.{ContactTable, EateryChoiceTable}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

@Singleton
class Choices @Inject()(dbConfigProvider: DatabaseConfigProvider, contacts: Contacts, users: Users) {

  private val db = dbConfigProvider.get[JdbcProfile].db

  private val choicesT = TableQuery[EateryChoiceTable]

  def friendChoiceMap(user: User): Future[Map[String, Seq[User]]] = db.run(
    (for {
      friend <- contacts.friendsOfUserAction(user.id)
      choice <- choicesT.filter(_.user === friend.ownerID)
      eatery <- choice.pointsTo
    } yield (eatery.chainID, users.getFromId(choice.user))).result
  ).map((seq: Seq[(String, User)]) =>
    seq.groupBy(_._1).mapValues(_.map(_._2))
  )
}
package services.daos

import javax.inject.{Inject, Singleton}

import models.db.{EateryChoiceTable, User}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

@Singleton
class Choices @Inject()(dbConfigProvider: DatabaseConfigProvider, contacts: Contacts) {

  private val db = dbConfigProvider.get[JdbcProfile].db

  private val choicesT = TableQuery[EateryChoiceTable]

  def friendChoiceMap(user: User): Future[Map[String, Seq[User]]] = db.run(
    (for {
      (_, f) <- contacts.friendsOfUserAction(user.id)
      c <- choicesT.filter(_.user === f.id)
      u <- c.belongsTo
      e <- c.pointsTo
    } yield (e.chainID, u)).result
  ).map( (seq: Seq[(String, User)]) =>
    seq.groupBy(_._1).mapValues(_.map(_._2))
  )
}
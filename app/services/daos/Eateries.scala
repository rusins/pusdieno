package services.daos

import javax.inject.{Inject, Singleton}

import models.Eatery
import models.db._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.daos.Eateries._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

@Singleton
class Eateries @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  private val db = dbConfigProvider.get[JdbcProfile].db

  def retrieveAll(): Future[Seq[Eatery]] = db.run(
    (for {
      e <- eateries
      opens <- e.openTimes
      closes <- e.closeTimes
    } yield (e, opens, closes)).result
  ).map(_.map((Eatery.fromDB _).tupled))

  def add(eatery: Eatery): Future[Unit] = db.run(eatery.toDB match {
    case (dbEatery, opens, closes) =>
      DBIO.seq(
        times += opens,
        times += closes,
        eateries += dbEatery
      )
  })
}

object Eateries {
  private val eateries = TableQuery[DBEateryTable]
  private val times = TableQuery[DBWeekTimesTable]
}
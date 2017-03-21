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
  ).map(_.map((toEatery _).tupled))

  def add(eatery: Eatery): Future[Unit] = db.run(toDBEatery(eatery) match {
    case (dbEatery, opens, closes) =>
      DBIO.seq(
        eateries += dbEatery,
        times.insertOrUpdate(opens),
        times.insertOrUpdate(closes)
      )
  })
}

object Eateries {
  private val eateries = TableQuery[DBEateryTable]
  private val times = TableQuery[DBWeekTimesTable]

  private def toEatery(dbEatery: DBEatery, opens: DBWeekTimes, closes: DBWeekTimes) =
    Eatery(dbEatery.id, dbEatery.chainID, dbEatery.address, (WeekTimes.fromDB(opens), WeekTimes.fromDB(closes)))

  private def toDBEatery(eatery: Eatery): (DBEatery, DBWeekTimes, DBWeekTimes) = (
    DBEatery(eatery.id, eatery.chainID, eatery.address, eatery.openHours._1.id, eatery.openHours._2.id),
    eatery.openHours._1.toDB,
    eatery.openHours._2.toDB
  )
}
package services.daos

import javax.inject.{Inject, Singleton}

import models.Eatery
import models.db._
import play.api.db.slick.DatabaseConfigProvider
import services.EateryService
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EateryDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)
                         (implicit ex: ExecutionContext) extends EateryService {

  private val db = dbConfigProvider.get[JdbcProfile].db

  private val eateries = TableQuery[DbEateryTable]
  private val times = TableQuery[DBWeekTimesTable]

  override def retrieveAll(): Future[Seq[Eatery]] = db.run(
    (for {
      e <- eateries
      opens <- e.openTimes
      closes <- e.closeTimes
    } yield (e, opens, closes)).result
  ).map(_.map((Eatery.fromDbEatery _).tupled))

  override def add(eatery: Eatery): Future[Unit] = db.run(eatery.toDbEatery match {
    case (dbEatery, opens, closes) =>
      DBIO.seq(
        times += opens,
        times += closes,
        eateries += dbEatery
      )
  })
}
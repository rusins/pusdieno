package services.daos

import javax.inject.{Inject, Singleton}

import models.{Cafe, Restaurant}
import models.db.{DbCafeTable, DBWeekTimesTable}
import play.api.db.slick.DatabaseConfigProvider
import services.CafeService
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

// TODO: Abstract with trait for DI

@Singleton
class CafeDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ex: ExecutionContext) extends CafeService with TimeTable {

  private val db = dbConfigProvider.get[JdbcProfile].db

  protected[daos] val cafes = TableQuery[DbCafeTable]

  override def retrieveAll(): Future[Seq[Cafe]] = db.run(
    (for {
      cafe <- cafes
      opens <- cafe.openTimes
      closes <- cafe.closeTimes
    } yield (cafe, opens, closes)).result
  ).map(_.map((Cafe.fromDbCafe _).tupled))

  override def add(cafe: Cafe): Future[Unit] = db.run(cafe.toDbCafe match {
    case (dbCafe, opens, closes) =>
      DBIO.seq(
        weekTimes += opens,
        weekTimes += closes,
        cafes += dbCafe
      )
  })
}
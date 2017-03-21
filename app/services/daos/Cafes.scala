package services.daos

import javax.inject.{Inject, Singleton}

import models.Cafe
import models.db.{DBCafe, DBCafeTable, DBWeekTimes, WeekTimes}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

@Singleton
class Cafes @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  private val cafes = TableQuery[DBCafeTable]

  private val db = dbConfigProvider.get[JdbcProfile].db

  def fromDB(dbCafe: DBCafe, opens: DBWeekTimes, closes: DBWeekTimes): Cafe =
    Cafe(dbCafe.id, dbCafe.chainID, dbCafe.address, (WeekTimes.fromDB(opens), WeekTimes.fromDB(closes)))

  def retrieveAll(): Future[Seq[Cafe]] = db.run(
    (for {
      cafe <- cafes
      opens <- cafe.openTimes
      closes <- cafe.closeTimes
    } yield (cafe, opens, closes)).result
  ).map(_.map((fromDB _).tupled))
}
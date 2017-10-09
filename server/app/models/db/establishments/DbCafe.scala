package models.db.establishments

import java.util.UUID

import models.CafeInfo
import models.db.{DBWeekTimes, DBWeekTimesTable, WeekTimes}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

/**
  * Database model containing the extra information that cafes have but general establishments don't
  *
  * @param id the UUID of the cafe info, should be the same as its establishment's ID!
  * @param openTimes UUID that references the cafe's opening times in the WeekTimes table
  * @param closeTimes UUID that references the cafe's closing times in the WeekTimes table
  */
case class DbCafe(id: UUID, openTimes: UUID, closeTimes: UUID) {
  def toModel(openingHours: WeekTimes, closingHours: WeekTimes): CafeInfo = CafeInfo((openingHours, closingHours))

}

object DbCafe {
  def fromModel(id: UUID, cafeInfo: CafeInfo): DbCafe = DbCafe(id, cafeInfo.openHours._1.id, cafeInfo.openHours._2.id)
}

class DbCafeTable(tag: Tag) extends Table[DbCafe](tag, "restaurants") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def openTimes: Rep[UUID] = column[UUID]("open_times")

  def closeTimes: Rep[UUID] = column[UUID]("close_times")

  override def * : ProvenShape[DbCafe] = (id, openTimes, closeTimes) <> (DbCafe.tupled, DbCafe.unapply)

  def openTimesQuery: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] =
    foreignKey("open_times", openTimes, TableQuery[DBWeekTimesTable])(
      (dbWeekTimesTable: DBWeekTimesTable) => dbWeekTimesTable.id
    )

  def closeTimesQuery: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] =
    foreignKey("close_times", closeTimes, TableQuery[DBWeekTimesTable])(
      (dbWeekTimesTable: DBWeekTimesTable) => dbWeekTimesTable.id
    )
}

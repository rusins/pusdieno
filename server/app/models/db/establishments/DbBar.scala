package models.db.establishments

import java.util.UUID

import models.BarInfo
import models.db.{DBWeekTimes, DBWeekTimesTable, WeekTimes}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

/**
  * Database model containing the extra information that bars have but general establishments don't
  *
  * @param id the UUID of the bar info, should be the same as its establishment's ID!
  * @param openTimes UUID referencing the opening times of the bar in the WeekTimes table
  * @param closeTimes UUID referencing the closing times of the bar in the WeekTimes table
  */
case class DbBar(id: UUID, openTimes: UUID, closeTimes: UUID) {
  def toModel(openingHours: WeekTimes, closingHours: WeekTimes): BarInfo = BarInfo((openingHours,closingHours))
}

object DbBar {
  def fromModel(id: UUID, barInfo: BarInfo): DbBar = DbBar(id, barInfo.openHours._1.id, barInfo.openHours._2.id)
}

class DbBarTable(tag: Tag) extends Table[DbBar](tag, "restaurants") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def openTimes: Rep[UUID] = column[UUID]("open_times")

  def closeTimes: Rep[UUID] = column[UUID]("close_times")

  override def * : ProvenShape[DbBar] = (id, openTimes, closeTimes) <> (DbBar.tupled, DbBar.unapply)

  def openTimesQuery: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] =
    foreignKey("open_times", openTimes, TableQuery[DBWeekTimesTable])(
      (dbWeekTimesTable: DBWeekTimesTable) => dbWeekTimesTable.id
    )

  def closeTimesQuery: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] =
    foreignKey("close_times", closeTimes, TableQuery[DBWeekTimesTable])(
      (dbWeekTimesTable: DBWeekTimesTable) => dbWeekTimesTable.id
    )
}

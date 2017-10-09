package models.db.establishments

import java.util.UUID

import models.EateryInfo
import models.db.{DBWeekTimes, DBWeekTimesTable, WeekTimes}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

/**
  * Database model containing the extra information that restaurants have but general establishments don't
  *
  * @param id        the UUID of the restaurant info, should be the same as its establishment's ID!
  * @param veganOnly is false if the restaurant serves non-vegan food.
  * @param hasCoffee is true if the restaurant serves some form of coffee
  */
case class DbEatery(id: UUID, openTimes: UUID, closeTimes: UUID, veganOnly: Boolean, hasCoffee: Boolean) {
  def toModel(openingHours: WeekTimes, closingHours: WeekTimes): EateryInfo = EateryInfo((openingHours, closingHours),
    veganOnly, hasCoffee)
}

object DbEatery {
  def fromModel(id: UUID, eatery: EateryInfo): DbEatery = DbEatery(id, eatery.openHours._1.id, eatery.openHours._2.id, eatery.veganOnly, eatery.hasCoffee)
}

class DbEateryTable(tag: Tag) extends Table[DbEatery](tag, "restaurants") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def openTimes: Rep[UUID] = column[UUID]("open_times")

  def closeTimes: Rep[UUID] = column[UUID]("close_times")

  def vegan: Rep[Boolean] = column[Boolean]("vegan_only")

  def hasCoffee: Rep[Boolean] = column[Boolean]("serve_coffee")

  override def * : ProvenShape[DbEatery] = (id, openTimes, closeTimes, vegan, hasCoffee) <> (DbEatery.tupled, DbEatery.unapply)

  def openTimesQuery: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] =
    foreignKey("open_times", openTimes, TableQuery[DBWeekTimesTable])(
      (dbWeekTimesTable: DBWeekTimesTable) => dbWeekTimesTable.id
    )

  def closeTimesQuery: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] =
    foreignKey("close_times", closeTimes, TableQuery[DBWeekTimesTable])(
      (dbWeekTimesTable: DBWeekTimesTable) => dbWeekTimesTable.id
    )
}


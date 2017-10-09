package models.db

import java.sql.Time
import java.util.UUID

import models.db.WeekTimes.OT
import slick.driver.PostgresDriver.api._
import slick.lifted.ProvenShape

case class WeekTimes(id: UUID = UUID.randomUUID(),
                     monday: OT, tuesday: OT, wednesday: OT, thursday: OT, friday: OT, saturday: OT, sunday: OT)

object WeekTimes {

  type OT = Option[Time]

  def empty: WeekTimes = WeekTimes(UUID.randomUUID(), None, None, None, None, None, None, None)
}

case class DBWeekTimes(id: UUID, mo: OT, tu: OT, we: OT, th: OT, fr: OT, sa: OT, su: OT) {

  def toModel: WeekTimes = WeekTimes(id, mo, tu, we, th, fr, sa, su)
}

object DBWeekTimes {

  def fromModel(weekTimes: WeekTimes): DBWeekTimes = DBWeekTimes(weekTimes.id,
    weekTimes.monday, weekTimes.tuesday, weekTimes.wednesday, weekTimes.thursday, weekTimes.friday,
    weekTimes.saturday, weekTimes.sunday)
}

class DBWeekTimesTable(tag: Tag) extends Table[DBWeekTimes](tag, "week_times") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def mo: Rep[OT] = column[OT]("monday")

  def tu: Rep[OT] = column[OT]("tuesday")

  def we: Rep[OT] = column[OT]("wednesday")

  def th: Rep[OT] = column[OT]("thursday")

  def fr: Rep[OT] = column[OT]("friday")

  def sa: Rep[OT] = column[OT]("saturday")

  def su: Rep[OT] = column[OT]("sunday")

  def * : ProvenShape[DBWeekTimes] = (id, mo, tu, we, th, fr, sa, su) <>
    (DBWeekTimes.tupled, DBWeekTimes.unapply)
}
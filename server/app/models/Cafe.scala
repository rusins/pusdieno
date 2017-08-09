package models

import java.util.UUID

import models.db.{DBCafe, DBWeekTimes, WeekTimes}

case class Cafe(id: UUID = UUID.randomUUID(), chainID: String, address: String, openHours: (WeekTimes, WeekTimes)) {
  def toDB: (DBCafe, DBWeekTimes, DBWeekTimes) = (
    DBCafe(id, chainID, address, openHours._1.id, openHours._2.id),
    openHours._1.toDB,
    openHours._2.toDB
  )
}

object Cafe {
  def fromDB(dbCafe: DBCafe, opens: DBWeekTimes, closes: DBWeekTimes): Cafe =
    Cafe(dbCafe.id, dbCafe.chainID, dbCafe.address, (WeekTimes.fromDB(opens), WeekTimes.fromDB(closes)))
}

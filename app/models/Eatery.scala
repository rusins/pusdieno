package models

import java.util.UUID

import models.db.{DBEatery, DBWeekTimes, WeekTimes}

case class Eatery(id: UUID = UUID.randomUUID, chainID: String, address: String, openHours: (WeekTimes, WeekTimes)) {
  def toDB: (DBEatery, DBWeekTimes, DBWeekTimes) = (
    DBEatery(id, chainID, address, openHours._1.id, openHours._2.id),
    openHours._1.toDB,
    openHours._2.toDB
  )
}

object Eatery {
  def fromDB(dbEatery: DBEatery, opens: DBWeekTimes, closes: DBWeekTimes) =
    Eatery(dbEatery.id, dbEatery.chainID, dbEatery.address, (WeekTimes.fromDB(opens), WeekTimes.fromDB(closes)))
}

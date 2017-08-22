package models

import java.util.UUID

import models.db._

sealed trait Establishment {
  val id: UUID
  val chainID: String
  val address: String
  val openHours: (WeekTimes, WeekTimes)
}

sealed trait Eatery extends Establishment

sealed trait Bar extends Establishment

case class Restaurant(id: UUID = UUID.randomUUID(), chainID: String,
                      address: String, openHours: (WeekTimes, WeekTimes)) extends Eatery {
  def toDbRestaurant: (DbRestaurant, DBWeekTimes, DBWeekTimes) = (
    DbRestaurant(id, chainID, address, openHours._1.id, openHours._2.id),
    openHours._1.toDB,
    openHours._2.toDB
  )
}

object Restaurant {
  def fromDbRestaurant(dbRestaurant: DbRestaurant, opens: DBWeekTimes, closes: DBWeekTimes): Restaurant =
    Restaurant(dbRestaurant.id, dbRestaurant.chainID, dbRestaurant.address, (WeekTimes.fromDB(opens), WeekTimes.fromDB(closes)))
}

case class Cafe(id: UUID = UUID.randomUUID(), chainID: String, address: String, openHours: (WeekTimes, WeekTimes))
  extends Eatery(id, chainID, address, openHours) {
  def toDbCafe: (DbCafe, DBWeekTimes, DBWeekTimes) = (
    DbCafe(id, chainID, address, openHours._1.id, openHours._2.id),
    openHours._1.toDB,
    openHours._2.toDB
  )
}

object Cafe {
  def fromDbCafe(dbCafe: DbCafe, opens: DBWeekTimes, closes: DBWeekTimes): Cafe =
    new Cafe(dbCafe.id, dbCafe.chainID, dbCafe.address, (WeekTimes.fromDB(opens), WeekTimes.fromDB(closes)))
}

case class Pub(id: UUID = UUID.randomUUID(), chainID: String, address: String, openHours: (WeekTimes, WeekTimes))
  extends Bar with Eatery
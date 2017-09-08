package models

import java.util.UUID

import models.db._
import models.db.establishments.{DbCafe, DbRestaurant}

sealed trait Establishment {
  val id: UUID
  val chainID: String
  val address: String
  val openHours: (WeekTimes, WeekTimes)

  def toDbEatery: (DbEstablishment, DBWeekTimes, DBWeekTimes) = {
    val restaurantFk = this match {
      case _: Restaurant => Some(id)
      case _ => None
    }
    val cafeFk = this match {
      case _: Cafe => Some(id)
      case _ => None
    }
    val barFk = this match {
      case _: Bar => Some(id)
      case _ => None
    }

    (DbEstablishment(id, chainID, address, openHours._1.id, openHours._2.id, restaurantFk, cafeFk, barFk),
      openHours._1.toDB, openHours._2.toDB)
  }
}

sealed trait Eatery extends Establishment

sealed trait Bar extends Establishment

case class Restaurant(id: UUID = UUID.randomUUID(), chainID: String, address: String, openHours: (WeekTimes, WeekTimes),
                      veganOnly: Boolean, hasCoffee: Boolean) extends Eatery

object Restaurant {
  def fromDbRestaurant(dbEstablishment: DbEstablishment, opens: DBWeekTimes, closes: DBWeekTimes,
                       dbRestaurant: DbRestaurant): Restaurant =
    Restaurant(dbEstablishment.id, dbEstablishment.chainID, dbEstablishment.address,
      (WeekTimes.fromDB(opens), WeekTimes.fromDB(closes)), dbRestaurant.veganOnly, dbRestaurant.hasCoffee)
}

case class Cafe(id: UUID = UUID.randomUUID(), chainID: String, address: String, openHours: (WeekTimes, WeekTimes),
                hasWifi: Boolean)
  extends Eatery

object Cafe {
  def fromDbCafe(dbEstablishment: DbEstablishment, opens: DBWeekTimes, closes: DBWeekTimes, dbCafe: DbCafe): Cafe =
    new Cafe(dbEstablishment.id, dbEstablishment.chainID, dbEstablishment.address,
      (WeekTimes.fromDB(opens), WeekTimes.fromDB(closes)), dbCafe.wifi)
}

case class Pub(id: UUID = UUID.randomUUID(), chainID: String, address: String, openHours: (WeekTimes, WeekTimes))
  extends Bar with Eatery
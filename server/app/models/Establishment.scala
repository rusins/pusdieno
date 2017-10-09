package models

import java.util.UUID

import models.db.WeekTimes

sealed case class Establishment(id: UUID = UUID.randomUUID(), chainID: String, address: String,
                                freeWifi: Option[Boolean] = None, eatery: Option[EateryInfo] = None,
                                cafe: Option[CafeInfo] = None, bar: Option[BarInfo] = None)
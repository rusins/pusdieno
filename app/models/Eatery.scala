package models

import java.util.UUID

import models.db.WeekTimes

case class Eatery(id: UUID = UUID.randomUUID, chainID: String, address: String, openHours: (WeekTimes, WeekTimes))

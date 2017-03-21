package models

import java.util.UUID

import models.db.{DBCafe, WeekTimes}

case class Cafe(id: UUID = UUID.randomUUID(), chainID: String, address: String, openHours: (WeekTimes, WeekTimes))

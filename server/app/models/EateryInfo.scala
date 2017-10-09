package models

import models.db.WeekTimes

case class EateryInfo(openHours: (WeekTimes, WeekTimes), veganOnly: Boolean, hasCoffee: Boolean)

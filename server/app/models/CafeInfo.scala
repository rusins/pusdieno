package models

import models.db.WeekTimes

case class CafeInfo(openHours: (WeekTimes, WeekTimes))

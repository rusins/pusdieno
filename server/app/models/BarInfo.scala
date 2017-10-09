package models

import models.db.WeekTimes

case class BarInfo(openHours: (WeekTimes, WeekTimes))

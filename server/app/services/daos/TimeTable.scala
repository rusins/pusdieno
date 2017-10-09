package services.daos

import models.db.DBWeekTimesTable
import slick.lifted.TableQuery

trait TimeTable {
  val weekTimes = TableQuery[DBWeekTimesTable]
}

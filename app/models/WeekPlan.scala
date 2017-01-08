package models

import java.sql.Time

import slick.driver.PostgresDriver.api._

object WeekPlan {
  def empty: WeekPlan = WeekPlan(None, None, None, None, None, None, None)
}

// When a user eats at
case class WeekPlan(monday: Option[Time], tuesday: Option[Time], wednesday: Option[Time], thursday: Option[Time],
                    friday: Option[Time], saturday: Option[Time], sunday: Option[Time])

case class LiftedWeekPlan(monday: Rep[Option[Time]], tuesday: Rep[Option[Time]], wednesday: Rep[Option[Time]],
                          thursday: Rep[Option[Time]], friday: Rep[Option[Time]], saturday: Rep[Option[Time]],
                          sunday: Rep[Option[Time]])
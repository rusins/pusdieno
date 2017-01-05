package models

import java.sql.Time

object WeekPlan {
  def empty: WeekPlan = WeekPlan(None, None, None, None, None, None, None)
}

// When a user eats at
case class WeekPlan(monday: Option[Time], tuesday: Option[Time], wednesday: Option[Time], thursday: Option[Time],
                    friday: Option[Time], saturday: Option[Time], sunday: Option[Time])

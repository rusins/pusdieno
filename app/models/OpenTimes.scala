package models

import java.sql.Time

import play.api.i18n.{Lang, Messages}
import play.twirl.api.Html
import slick.driver.PostgresDriver.api._


// When an eatery is open

object OpenTimes {
  def apply(weekdays: (Time, Time), weekend: (Time, Time)): OpenTimes =
    OpenTimes(weekdays, weekdays, weekdays, weekdays, weekdays, weekend, weekend)
}

case class OpenTimes(monday: (Time, Time), tuesday: (Time, Time), wednesday: (Time, Time),
                     thursday: (Time, Time), friday: (Time, Time), saturday: (Time, Time), sunday: (Time, Time))
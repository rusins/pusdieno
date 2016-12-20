package models

import play.api.i18n.{Lang, Messages}
import play.twirl.api.Html

object WeekPlan {

  val dayToInt = Map("Monday" -> 0, "Tuesday" -> 1, "Wednesday" -> 2,
    "Thursday" -> 3, "Friday" -> 4, "Saturday" -> 5, "Sunday" -> 7)

  def printTime(time: (Int, Int)): String = {
    val res = StringBuilder.newBuilder
    res.append((time._1 / 100).toString)
    res.append(":")
    res.append(time._1 % 100)
    res.append(" – ")
    res.append(time._2 / 100)
    res.append(":")
    res.append(time._2 % 100)
    res.toString()
  }
}

class WeekPlan(data: Array[(Int, Int)]) {
  assert(data.length == 7, "There should be exactly 7 days in a week!")

  lazy val uniqueCount = data.map((x: (Int, Int)) => (x, 1)).toMap.size

  def display(today: Int, maxLines: Int = 2)(implicit messages: Messages, lang: Lang) = Html(
    if (uniqueCount > maxLines)
      Messages("date.today") + "<br>" + print(data(today))
    else {
      val used = Array.fill(7)(false)
      val res = StringBuilder.newBuilder
      def short(x: Int): String = x match {
        case 0 => Messages("date.short.monday")
        case 1 => Messages("date.short.tuesday")
        case 2 => Messages("date.short.wednesday")
        case 3 => Messages("date.short.thursday")
        case 4 => Messages("date.short.friday")
        case 5 => Messages("date.short.saturday")
        case 6 => Messages("date.short.sunday")
      }
      for (i <- data.indices) {
        if (!used(i)) {
          for (j <- 0 until i)
            res.append(short(j))
          for (j <- i until 7)
            if (data(j)._1 == data(i)._1 && data(j)._2 == data(i)._2) {
              used(j) = true
              res.append("<b>")
              res.append(short(j))
              res.append("</b>")
            } else
              res.append(short(j))
          res.append("<br>")
          res.append("<span style=\"color: white;\">")
          res.append(WeekPlan.printTime(data(i)))
          res.append("</span>")
          res.append("<br>")
        }
      }
      res.toString()
    }

  )
}

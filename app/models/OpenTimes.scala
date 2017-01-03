package models

import play.api.i18n.{Lang, Messages}
import play.twirl.api.Html


// When an eatery is open
// TODO: Replace with Java library types and make DB compatible
object OpenTimes {

  val dayToInt = Map("Monday" -> 0, "Tuesday" -> 1, "Wednesday" -> 2,
    "Thursday" -> 3, "Friday" -> 4, "Saturday" -> 5, "Sunday" -> 7)

  def open(time: (Int, Int)) = time._2 != 0

  def printTime(time: (Int, Int))(implicit messages: Messages): String = if (!open(time))
    messages("eateries.closed")
  else {
    val res = StringBuilder.newBuilder
    res.append((time._1 / 100).toString)
    res.append(":")
    time._1 % 100 match {
      case 0 => res.append("00")
      case x => res.append(x)
    }
    res.append(" – ")
    res.append(time._2 / 100)
    res.append(":")
    time._2 % 100 match {
      case 0 => res.append("00")
      case x => res.append(x)
    }
    res.toString()
  }
}

class OpenTimes(data: Array[(Int, Int)]) {
  assert(data.length == 7, "There should be exactly 7 days in a week!")

  lazy val uniqueCount = data.map((x: (Int, Int)) => (x, 1)).toMap.size

  def open(today: Int) = WeekPlan.open(data(today))

  private def short(x: Int)(implicit messages: Messages): String = x match {
    case 0 => messages("date.short.monday")
    case 1 => messages("date.short.tuesday")
    case 2 => messages("date.short.wednesday")
    case 3 => messages("date.short.thursday")
    case 4 => messages("date.short.friday")
    case 5 => messages("date.short.saturday")
    case 6 => messages("date.short.sunday")
  }

  def displayInRows(today: Int, maxLines: Int = 2)(implicit messages: Messages) = Html(
    if (uniqueCount > maxLines)
      messages("date.today") + "<br>" + WeekPlan.printTime(data(today))
    else {
      val used = Array.fill(7)(false)
      val res = StringBuilder.newBuilder
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

  def displayInTable(tableClasses: String = "")(implicit messages: Messages) = Html({

    val res = StringBuilder.newBuilder
    res.append("<table class='")
    res.append(tableClasses)
    res.append("'>\n")
    val used = Array.fill(7)(false)
    for (i <- data.indices) {
      res.append("<tr>\n<td>")

      res.append("</td>\n</tr>")
    }
    res.append("</table>")
    res.toString()
  })
}

package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import models.db.{DBUser, DBWeekTimes, WeekTimes}

case class EatsAt(breakfast: Option[WeekTimes], lunch: Option[WeekTimes], dinner: Option[WeekTimes])

case class User(id: UUID = UUID.randomUUID(),
                name: String,
                phone: Option[Int] = None,
                email: Option[String] = None,
                eatsAt: EatsAt = EatsAt(None, None, None),
                avatarURL: Option[String] = None) extends Identity {

  def toDB: DBUser = DBUser(id, name, phone, email,
    eatsAt.breakfast.map(_.id), eatsAt.lunch.map(_.id), eatsAt.dinner.map(_.id), avatarURL)
}

object User {
  def fromDB(dbUser: DBUser, breakfast: Option[DBWeekTimes], lunch: Option[DBWeekTimes], dinner: Option[DBWeekTimes]): User =
    User(dbUser.id, dbUser.name, dbUser.phone, dbUser.email,
      EatsAt(breakfast.map(WeekTimes.fromDB), lunch.map(WeekTimes.fromDB), dinner.map(WeekTimes.fromDB)),
      dbUser.avatarURL)
}

package models.db

import java.util.UUID

import slick.driver.PostgresDriver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

case class DBUser(id: UUID,
                  name: String,
                  phone: Option[Int],
                  email: Option[String],
                  breakfastFK: Option[UUID],
                  lunchFK: Option[UUID],
                  dinnerFK: Option[UUID],
                  avatarURL: Option[String])

class DBUserTable(tag: Tag) extends Table[DBUser](tag, "users") {
  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def name: Rep[String] = column[String]("name")

  def phone: Rep[Option[Int]] = column[Option[Int]]("mobile")

  def email: Rep[Option[String]] = column[Option[String]]("email")

  def breakfastFK: Rep[Option[UUID]] = column[Option[UUID]]("breakfast_fk")

  def lunchFK: Rep[Option[UUID]] = column[Option[UUID]]("lunch_fk")

  def dinnerFK: Rep[Option[UUID]] = column[Option[UUID]]("dinner_fk")

  def avatarURL: Rep[Option[String]] = column[Option[String]]("avatar_url")
  
  def * : ProvenShape[DBUser] =
    (id, name, phone, email, breakfastFK, lunchFK, dinnerFK, avatarURL) <>
    (DBUser.tupled, DBUser.unapply)

  private val weekTimes = TableQuery[DBWeekTimesTable]

  def breakfastTimes: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] = foreignKey("breakfast_fk", breakfastFK, weekTimes)(
    (weekTT: DBWeekTimesTable) => weekTT.id.?
  )

  def lunchTimes: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] = foreignKey("lunch_fk", lunchFK, weekTimes)(
    (weekTT: DBWeekTimesTable) => weekTT.id.?
  )

  def dinnerTimes: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] = foreignKey("dinner_fk", dinnerFK, weekTimes)(
    (weekTT: DBWeekTimesTable) => weekTT.id.?
  )
}
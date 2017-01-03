package models

import java.sql.Time
import java.util.UUID

import slick.driver.PostgresDriver.api._

case class User(id: UUID = UUID.randomUUID(), name: String, phoneNumber: Option[Int], eatsAt: WeekPlan)

class UserTable(tag: Tag) extends Table[User](tag, "USER") {
  def id: Rep[UUID] = column[UUID]("ID", O.PrimaryKey)

  def name: Rep[String] = column[String]("NAME")

  def phoneNumber: Rep[Option[Int]] = column[Option[Int]]("PHONE")

  def monday: Rep[Option[Time]] = column[Option[Time]]("MONDAY")

  def tuesday: Rep[Option[Time]] = column[Option[Time]]("TUESDAY")

  def wednesday: Rep[Option[Time]] = column[Option[Time]]("WEDNESDAY")

  def thursday: Rep[Option[Time]] = column[Option[Time]]("THURSDAY")

  def friday: Rep[Option[Time]] = column[Option[Time]]("FRIDAY")

  def saturday: Rep[Option[Time]] = column[Option[Time]]("SATURDAY")

  def sunday: Rep[Option[Time]] = column[Option[Time]]("SUNDAY")

  private type WeekTupleType =
    (Option[Time], Option[Time], Option[Time], Option[Time], Option[Time], Option[Time], Option[Time])
  private type UserTupleType = (UUID, String, Option[Int], WeekTupleType)

  private val userShapedValue = (id, name, phoneNumber, (
    monday, tuesday, wednesday, thursday, friday, saturday, sunday
  )).shaped[UserTupleType]

  private val toModel: UserTupleType => User = {
    case (id, name, phoneNumber, eatsAt) => User(id, name, phoneNumber, WeekPlan.tupled(eatsAt))
  }

  private val toTuple: User => Option[UserTupleType] =
    user => Some((user.id, user.name, user.phoneNumber, WeekPlan.unapply(week)))

  def * = userShapedValue <> (toModel, toTuple)

}
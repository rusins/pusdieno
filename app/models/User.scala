package models

import java.sql.Time
import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

case class User(id: UUID = UUID.randomUUID(), name: String, phoneNumber: Option[Int], eatsAt: WeekPlan)

class UserTable(tag: Tag) extends Table[User](tag, "user") {
  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def name: Rep[String] = column[String]("name")

  def phoneNumber: Rep[Option[Int]] = column[Option[Int]]("mobile")

  def monday: Rep[Option[Time]] = column[Option[Time]]("monday")

  def tuesday: Rep[Option[Time]] = column[Option[Time]]("tuesday")

  def wednesday: Rep[Option[Time]] = column[Option[Time]]("wednesday")

  def thursday: Rep[Option[Time]] = column[Option[Time]]("thursday")

  def friday: Rep[Option[Time]] = column[Option[Time]]("friday")

  def saturday: Rep[Option[Time]] = column[Option[Time]]("saturday")

  def sunday: Rep[Option[Time]] = column[Option[Time]]("sunday")

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
    user => Some((user.id, user.name, user.phoneNumber, WeekPlan.unapply(user.eatsAt).get))

  def * = userShapedValue <> (toModel, toTuple)

}

object Users {

  @Inject() private val dbConfigProvider: DatabaseConfigProvider = null

  private val db = dbConfigProvider.get[JdbcProfile].db

  private val users = TableQuery[UserTable]

  def add(user: User): Future[String] = {
    db.run(users += user).map(res => "User successfully added!").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def del(id: UUID): Future[Int] = db.run(users.filter(_.id === id).delete)

  def get(id: UUID): Future[Option[User]] = db.run(users.filter(_.id === id).result.headOption)

  def listAll(): Future[Seq[User]] = db.run(users.result)

}
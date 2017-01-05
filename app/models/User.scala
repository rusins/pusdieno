package models

import java.sql.Time
import java.util.UUID
import javax.inject.{Inject, Singleton}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.db.slick.DatabaseConfigProvider
import slick.backend.DatabasePublisher
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

case class User(id: UUID = UUID.randomUUID(), name: String, phoneNumber: Option[Int] = None, eatsAt: WeekPlan)

class UserTable(tag: Tag) extends Table[User](tag, "USER") {
  def id: Rep[UUID] = column[UUID]("ID", O.PrimaryKey)

  def name: Rep[String] = column[String]("NAME")

  def phoneNumber: Rep[Option[Int]] = column[Option[Int]]("MOBILE")

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
    case (id, name, phoneNumber, eatsAt) => User(id, name, phoneNumber, (WeekPlan.apply _).tupled(eatsAt))
  }

  private val toTuple: User => Option[UserTupleType] =
    user => Some((user.id, user.name, user.phoneNumber, WeekPlan.unapply(user.eatsAt).get))

  def * = userShapedValue <> (toModel, toTuple)

}

@Singleton
class Users @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  private val db = dbConfigProvider.get[JdbcProfile].db

  private val users = TableQuery[UserTable]

  private val setupAction = DBIO.seq(
    //users.schema.create,
    users += User(name = "Jānis Pupiņš", eatsAt = WeekPlan.empty)
  )

   val setupFuture: Future[Unit] = db.run(setupAction)

  def add(user: User): Future[String] = setupFuture.flatMap(_ =>
    db.run(users += user).map(res => "User successfully added!").recover {
      case ex: Exception => ex.getCause.getMessage
    })

  def del(id: UUID): Future[Int] = setupFuture.flatMap(_ =>
    db.run(users.filter(_.id === id).delete)
  )

  def retrieve(id: UUID): Future[Option[User]] = setupFuture.flatMap(_ =>
    db.run(users.filter(_.id === id).result.headOption)
  )

  def retrieveAll(): Future[Seq[User]] = setupFuture.flatMap(_ =>
    db.run(users.result)
  )
}
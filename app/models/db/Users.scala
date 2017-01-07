package models.db

import java.sql.Time
import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.{LiftedWeekPlan, WeekPlan}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

case class User(id: UUID = UUID.randomUUID(), name: String, mobile: Option[Int] = None, email: Option[String] = None, eatsAt: WeekPlan)

case class LiftedUser(id: Rep[UUID], name: Rep[String], mobile: Rep[Option[Int]], email: Rep[Option[String]], eatsAt: LiftedWeekPlan)

class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def name: Rep[String] = column[String]("name")

  def mobile: Rep[Option[Int]] = column[Option[Int]]("mobile")

  def email: Rep[Option[String]] = column[Option[String]]("email")


  implicit object WeekPlanShape extends CaseClassShape(LiftedWeekPlan.tupled, (WeekPlan.apply _).tupled)

  implicit object UserShape extends CaseClassShape(LiftedUser.tupled, User.tupled)

  def * = LiftedUser(id, name, mobile, email, LiftedWeekPlan(column("monday"), column("tuesday"),
    column("wednesday"), column("thursday"), column("friday"), column("saturday"), column("sunday")))

}

@Singleton
class Users @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  private val db = dbConfigProvider.get[JdbcProfile].db

  private val users = TableQuery[UserTable]

  val _ = db run DBIO.seq(
    users.delete,
    users += User(name = "Jānis Pupiņš", eatsAt = WeekPlan.empty),
    users += User(name = "Kārlis Logins", eatsAt = WeekPlan.empty),
    users += User(name = "Kiler Klauns", eatsAt = WeekPlan.empty)
  )

  def add(user: User): Future[Int] = db.run(users += user)

  def del(id: UUID): Future[Int] = db.run(users.filter(_.id === id).delete)

  def retrieve(id: UUID): Future[Option[User]] = db.run(users.filter(_.id === id).result.headOption)

  def retrieveAll(): Future[Seq[User]] = db.run(users.result)
}
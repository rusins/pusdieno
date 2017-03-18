package models.db

import java.sql.Time
import java.util.UUID
import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import models.{LiftedWeekPlan, WeekPlan}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}

case class User(id: UUID = UUID.randomUUID(), name: String, mobile: Option[Int] = None, email: Option[String] = None,
                eatsAt: WeekPlan, googleID: Option[String] = None, googleKey: Option[String] = None,
                facebookID: Option[String] = None, facebookKey: Option[String] = None,
                avatarURL: Option[String] = None) extends Identity

case class LiftedUser(id: Rep[UUID], name: Rep[String], mobile: Rep[Option[Int]], email: Rep[Option[String]],
                      eatsAt: LiftedWeekPlan, googleID: Rep[Option[String]] = None,
                      googleKey: Rep[Option[String]] = None, facebookID: Rep[Option[String]] = None,
                      facebookKey: Rep[Option[String]], avatarURL: Rep[Option[String]] = None)

class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def name: Rep[String] = column[String]("name")

  def mobile: Rep[Option[Int]] = column[Option[Int]]("mobile")

  def email: Rep[Option[String]] = column[Option[String]]("email")

  def googleID: Rep[Option[String]] = column[Option[String]]("google_id")

  def googleKey: Rep[Option[String]] = column[Option[String]]("google_key")

  def facebookID: Rep[Option[String]] = column[Option[String]]("facebook_id")

  def facebookKey: Rep[Option[String]] = column[Option[String]]("facebook_key")

  def avatarURL: Rep[Option[String]] = column[Option[String]]("avatar_url")

  implicit object WeekPlanShape extends CaseClassShape(LiftedWeekPlan.tupled, (WeekPlan.apply _).tupled)

  implicit object UserShape extends CaseClassShape(LiftedUser.tupled, User.tupled)

  def * = LiftedUser(id, name, mobile, email, LiftedWeekPlan(column("monday"), column("tuesday"),
    column("wednesday"), column("thursday"), column("friday"), column("saturday"), column("sunday")),
    googleID, googleKey, facebookID, facebookKey, avatarURL)

}
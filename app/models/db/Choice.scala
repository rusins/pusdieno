package models.db

import java.util.UUID
import javax.inject.{Inject, Singleton}

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

import scala.concurrent.Future

case class Choice(id: UUID = UUID.randomUUID(), user: UUID, eatery: UUID)

class EateryChoiceTable(tag: Tag) extends Table[Choice](tag, "eatery_choices") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def user: Rep[UUID] = column[UUID]("usr")

  def eatery: Rep[UUID] = column[UUID]("eatery")

  def * : ProvenShape[Choice] = (id, user, eatery) <> (Choice.tupled, Choice.unapply)

  def belongsTo: ForeignKeyQuery[UserTable, User] = foreignKey("usr_fk", user, TableQuery[UserTable])(
    (userT: UserTable) => userT.id,
    // We want to delete a user's choices if he deletes his account
    onDelete = ForeignKeyAction.Cascade
  )

  def pointsTo: ForeignKeyQuery[EateryTable, Eatery] = foreignKey("eatery_fk", eatery, TableQuery[EateryTable])(
    (eateryT: EateryTable) => eateryT.id,
    // We want to delete people going to that eatery if it gets deleted
    onDelete = ForeignKeyAction.Cascade
  )
}

class CafeChoiceTable(tag: Tag) extends Table[Choice](tag, "cafe_choices") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def user: Rep[UUID] = column[UUID]("usr")

  def cafe: Rep[UUID] = column[UUID]("cafe")

  def * : ProvenShape[Choice] = (id, user, cafe) <> (Choice.tupled, Choice.unapply)

  def belongsTo: ForeignKeyQuery[UserTable, User] = foreignKey("id", user, TableQuery[UserTable])(
    (userT: UserTable) => userT.id,
    // We want to delete a user's choices if he deletes his account
    onDelete = ForeignKeyAction.Cascade
  )

  def pointsTo: ForeignKeyQuery[CafeTable, Cafe] = foreignKey("id", cafe, TableQuery[CafeTable])(
    (cafe: CafeTable) => cafe.id,
    // We want to delete people going to that eatery if it gets deleted
    onDelete = ForeignKeyAction.Cascade
  )
}
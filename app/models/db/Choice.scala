package models.db

import java.util.UUID

import slick.driver.PostgresDriver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

case class Choice(id: UUID = UUID.randomUUID(), user: UUID, eatery: UUID)

class EateryChoiceTable(tag: Tag) extends Table[Choice](tag, "eatery_choices") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def user: Rep[UUID] = column[UUID]("usr")

  def eatery: Rep[UUID] = column[UUID]("eatery")

  def * : ProvenShape[Choice] = (id, user, eatery) <> (Choice.tupled, Choice.unapply)

  def belongsTo: ForeignKeyQuery[DBUserTable, DBUser] = foreignKey("usr", user, TableQuery[DBUserTable])(
    (userT: DBUserTable) => userT.id,
    // We want to delete a user's choices if he deletes his account
    onDelete = ForeignKeyAction.Cascade
  )

  def pointsTo: ForeignKeyQuery[DBEateryTable, DBEatery] = foreignKey("eatery", eatery, TableQuery[DBEateryTable])(
    (eateryT: DBEateryTable) => eateryT.id,
    // We want to delete people going to that eatery if it gets deleted
    onDelete = ForeignKeyAction.Cascade
  )
}

class CafeChoiceTable(tag: Tag) extends Table[Choice](tag, "cafe_choices") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def user: Rep[UUID] = column[UUID]("usr")

  def cafe: Rep[UUID] = column[UUID]("cafe")

  def * : ProvenShape[Choice] = (id, user, cafe) <> (Choice.tupled, Choice.unapply)

  def belongsTo: ForeignKeyQuery[DBUserTable, DBUser] = foreignKey("id", user, TableQuery[DBUserTable])(
    (userT: DBUserTable) => userT.id,
    // We want to delete a user's choices if he deletes his account
    onDelete = ForeignKeyAction.Cascade
  )

  def pointsTo: ForeignKeyQuery[DBCafeTable, DBCafe] = foreignKey("id", cafe, TableQuery[DBCafeTable])(
    (cafe: DBCafeTable) => cafe.id,
    // We want to delete people going to that eatery if it gets deleted
    onDelete = ForeignKeyAction.Cascade
  )
}
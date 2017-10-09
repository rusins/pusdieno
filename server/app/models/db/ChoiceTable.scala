package models.db

import java.util.UUID

import models.Choice
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

class ChoiceTable(tag: Tag) extends Table[Choice](tag, "choices") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def user: Rep[UUID] = column[UUID]("usr")

  def establishment: Rep[UUID] = column[UUID]("establishment")

  def * : ProvenShape[Choice] = (id, user, establishment) <> (Choice.tupled, Choice.unapply)

  def userQuery: ForeignKeyQuery[DBUserTable, DBUser] = foreignKey("usr", user, TableQuery[DBUserTable])(
    (userT: DBUserTable) => userT.id,
    // We want to delete a user's choices if he deletes his account
    onDelete = ForeignKeyAction.Cascade
  )

  def establishmentQuery: ForeignKeyQuery[DbEstablishmentTable, DbEstablishment] = foreignKey("establishment", establishment, TableQuery[DbEstablishmentTable])(
    (establishmentTable: DbEstablishmentTable) => establishmentTable.id,
    // We want to delete people going to that eatery if it gets deleted
    onDelete = ForeignKeyAction.Cascade
  )
}
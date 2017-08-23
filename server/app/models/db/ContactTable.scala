package models.db

import java.util.UUID

import models.Contact
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape, TableQuery}

class ContactTable(tag: Tag) extends Table[Contact](tag, "contacts") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def name: Rep[String] = column[String]("name")

  def ownerID: Rep[UUID] = column[UUID]("owner_id")

  def contactID: Rep[Option[UUID]] = column[Option[UUID]]("contact_id")

  def contactPhone: Rep[Option[Int]] = column[Option[Int]]("contact_phone")

  def contactEmail: Rep[Option[String]] = column[Option[String]]("contact_email")

  def favorite: Rep[Boolean] = column[Boolean]("favorite")

  def * : ProvenShape[Contact] = (id, name, ownerID, contactID, contactPhone, contactEmail, favorite) <>
    (Contact.tupled, Contact.unapply)

  def belongsTo: ForeignKeyQuery[DBUserTable, DBUser] =
    foreignKey("owner_id", ownerID, TableQuery[DBUserTable])(
      (userT: DBUserTable) => userT.id,
      // We want to delete a user's .contacts once the user had been deleted
      onDelete = ForeignKeyAction.Cascade
    )

  def pointsTo: ForeignKeyQuery[DBUserTable, DBUser] =
    foreignKey("contact_id", contactID, TableQuery[DBUserTable])(
      (userT: DBUserTable) => userT.id.?,
      // When a contact is deleted, we still want to keep the reference in case he joins back
      onDelete = ForeignKeyAction.SetNull
    )
}
package models.db

import java.util.UUID
import javax.inject.{Inject, Singleton}

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape, QueryBase, TableQuery}
import slick.model.Column

import scala.concurrent.Future

case class Contact(id: UUID = UUID.randomUUID(), ownerID: UUID, contactID: Option[UUID], contactPhone: Option[Int],
                   contactEmail: Option[String])

// TODO: Update with info if necessary when adding google and facebook APIs

class ContactTable(tag: Tag) extends Table[Contact](tag, "contacts") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def ownerID: Rep[UUID] = column[UUID]("owner_id")

  def contactID: Rep[Option[UUID]] = column[Option[UUID]]("contact_id")

  def contactPhone: Rep[Option[Int]] = column[Option[Int]]("contact_phone")

  def contactEmail: Rep[Option[String]] = column[Option[String]]("contact_email")


  def * : ProvenShape[Contact] = (id, ownerID, contactID, contactPhone, contactEmail) <> (Contact.tupled, Contact.unapply)

  def belongsTo: ForeignKeyQuery[UserTable, User] =
    foreignKey("id", ownerID, TableQuery[UserTable])(
      (userT: UserTable) => userT.id,
      // We want to delete a user's .contacts once the user had been deleted
      onDelete = ForeignKeyAction.Cascade
    )

  def pointsTo: ForeignKeyQuery[UserTable, User] =
    foreignKey("id", contactID, TableQuery[UserTable])(
      (userT: UserTable) => userT.id.?,
      // When a contact is deleted, we still want to keep the reference in case he joins back
      onDelete = ForeignKeyAction.SetNull
    )
}

@Singleton
class Contacts @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  private val db = dbConfigProvider.get[JdbcProfile].db

  private val contacts = TableQuery[ContactTable]

  implicit def userToId(u: User): UUID = u.id

  def ofUser(userID: UUID): Future[Seq[User]] = db.run(
    (for {
      c <- contacts
      u <- c.belongsTo
    } yield u).result
  )

}

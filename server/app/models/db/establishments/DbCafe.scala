package models.db.establishments

import java.util.UUID

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

/**
  * Database model containing the extra information that cafes have but general establishments don't
  *
  * @param id the UUID of the cafe info, should be the same as its establishment's ID!
  * @param wifi whether or not the Cafe OFFERS free wifi
  */
case class DbCafe(id: UUID, wifi: Boolean)

class DbCafeTable(tag: Tag) extends Table[DbCafe](tag, "restaurants") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def wifi: Rep[Boolean] = column[Boolean]("wifi")

  override def * : ProvenShape[DbCafe] = (id, wifi) <> (DbCafe.tupled, DbCafe.unapply)
}

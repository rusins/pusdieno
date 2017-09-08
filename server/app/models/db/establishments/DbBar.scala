package models.db.establishments

import java.util.UUID

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

/**
  * Database model containing the extra information that bars have but general establishments don't
  *
  * @param id the UUID of the bar info, should be the same as its establishment's ID!
  * @param servesToMinors kappa
  */
case class DbBar(id: UUID, servesToMinors: Boolean)

class DbBarTable(tag: Tag) extends Table[DbBar](tag, "restaurants") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def servesToMinors: Rep[Boolean] = column[Boolean]("serves_to_minors")

  override def * : ProvenShape[DbBar] = (id, servesToMinors) <> (DbBar.tupled, DbBar.unapply)
}

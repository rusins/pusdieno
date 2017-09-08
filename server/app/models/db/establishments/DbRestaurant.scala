package models.db.establishments

import java.util.UUID

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

/**
  * Database model containing the extra information that restaurants have but general establishments don't
  *
  * @param id         the UUID of the restaurant info, should be the same as its establishment's ID!
  * @param veganOnly  is false if the restaurant serves non-vegan food.
  * @param hasCoffee is true if the restaurant serves some form of coffee
  */
case class DbRestaurant(id: UUID, veganOnly: Boolean, hasCoffee: Boolean)

class DbRestaurantTable(tag: Tag) extends Table[DbRestaurant](tag, "restaurants") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def vegan: Rep[Boolean] = column[Boolean]("vegan_only")

  def hasCoffee: Rep[Boolean] = column[Boolean]("serve_coffee")

  override def * : ProvenShape[DbRestaurant] = (id, vegan, hasCoffee) <> (DbRestaurant.tupled, DbRestaurant.unapply)
}

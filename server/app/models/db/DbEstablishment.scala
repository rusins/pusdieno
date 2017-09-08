package models.db

import java.util.UUID

import models.Chain
import models.db.establishments.{DbBar, DbBarTable, DbRestaurant, DbRestaurantTable}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

case class DbEstablishment(id: UUID, chainID: String, address: String, openTimesFk: UUID, closeTimesFK: UUID,
                           restaurantFk: UUID, cafeFk: UUID, barFk: UUID)

class EstablishmentTable(tag: Tag) extends Table[DbEstablishment](tag, "eateries") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def chainID: Rep[String] = column[String]("chain")

  def address: Rep[String] = column[String]("address")

  def openTimesFk: Rep[UUID] = column[UUID]("open_times")

  def closeTimesFk: Rep[UUID] = column[UUID]("close_times")

  def restaurantFk: Rep[UUID] = column[UUID]("restaurant_info")

  def cafeFk: Rep[UUID] = column[UUID]("cafe_info")

  def barFk: Rep[UUID] = column[UUID]("bar_info")

  override def * : ProvenShape[DbEstablishment] = (id, chainID, address, openTimesFk, closeTimesFk, restaurantFk, cafeFk, barFk) <>
    (DbEstablishment.tupled, DbEstablishment.unapply)

  def chain: ForeignKeyQuery[ChainTable, Chain] = foreignKey("chain", chainID, TableQuery[ChainTable])(
    (chainT: ChainTable) => chainT.id
  )

  private val weekTimes = TableQuery[DBWeekTimesTable]
  private val restaurants = TableQuery[DbRestaurantTable]
  private val cafes = TableQuery[DbCafeTable]
  private val bars = TableQuery[DbBarTable]

  def openTimes: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] = foreignKey("open_times", openTimesFk, weekTimes)(
    (weekTT: DBWeekTimesTable) => weekTT.id,
    // We want to delete the times when an eatery gets deleted
    onDelete = ForeignKeyAction.Cascade,
    onUpdate = ForeignKeyAction.Cascade
  )

  def closeTimes: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] = foreignKey("close_times", closeTimesFk, weekTimes)(
    (weekTT: DBWeekTimesTable) => weekTT.id,
    // We want to delete the times when an eatery gets deleted
    onDelete = ForeignKeyAction.Cascade,
    onUpdate = ForeignKeyAction.Cascade
  )

  def restaurantInfo: ForeignKeyQuery[DbRestaurantTable, DbRestaurant] = foreignKey("restaurant_info", restaurantFk, restaurants)(
    (restaurantTable: DbRestaurantTable) => restaurantTable.id
  )
  
  def cafeInfo: ForeignKeyQuery[DbCafeTable, DbCafe] = foreignKey("cafe_info", cafeFk, cafes)(
    (cafeTable: DbCafeTable) => cafeTable.id
  )

  def barInfo: ForeignKeyQuery[DbBarTable, DbBar] = foreignKey("bar_info", barFk, bars)(
    (barTable: DbBarTable) => barTable.id
  )

}
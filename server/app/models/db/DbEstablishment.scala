package models.db

import java.util.UUID

import models._
import models.db.establishments._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

case class DbEstablishment(id: UUID, chainID: String, address: String, wifi: Option[Boolean],
                           eateryInfo: Option[UUID], cafeInfo: Option[UUID], barInfo: Option[UUID]) {

  def toModel(eatery: Option[(DbEatery, (WeekTimes, WeekTimes))], cafe: Option[(DbCafe, (WeekTimes, WeekTimes))],
              bar: Option[(DbBar, (WeekTimes, WeekTimes))]): Establishment =
    Establishment(id, chainID, address, wifi,
      eatery.map {
        case (dbEatery, (openingHours, closingHours)) => dbEatery.toModel(openingHours, closingHours)
      }, cafe.map {
        case (dbCafe, (openingHours, closingHours)) => dbCafe.toModel(openingHours, closingHours)
      }, bar.map {
        case (dbBar, (openingHours, closingHours)) => dbBar.toModel(openingHours, closingHours)
      })

  def toModel(eatery: Option[EateryInfo], cafe: Option[CafeInfo], bar: Option[BarInfo]): Establishment =
    Establishment(id, chainID, address, wifi, eatery, cafe, bar)
}


class DbEstablishmentTable(tag: Tag) extends Table[DbEstablishment](tag, "eateries") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def chainID: Rep[String] = column[String]("chain")

  def address: Rep[String] = column[String]("address")

  def wifi: Rep[Option[Boolean]] = column[Boolean]("wifi")

  def eateryInfo: Rep[Option[UUID]] = column[UUID]("restaurant_info")

  def cafeInfo: Rep[Option[UUID]] = column[UUID]("cafe_info")

  def barInfo: Rep[Option[UUID]] = column[UUID]("bar_info")

  override def * : ProvenShape[DbEstablishment] = (id, chainID, address, wifi, eateryInfo, cafeInfo, barInfo) <>
    (DbEstablishment.tupled, DbEstablishment.unapply)

  def chain: ForeignKeyQuery[ChainTable, Chain] = foreignKey("chain", chainID, TableQuery[ChainTable])(
    (chainT: ChainTable) => chainT.id
  )

  private val eateries = TableQuery[DbEateryTable]
  private val cafes = TableQuery[DbCafeTable]
  private val bars = TableQuery[DbBarTable]

  def eateryInfoQuery: ForeignKeyQuery[DbEateryTable, DbEatery] = foreignKey("restaurant_info", eateryInfo, eateries)(
    (restaurantTable: DbEateryTable) => restaurantTable.id
  )

  def cafeInfoQuery: ForeignKeyQuery[DbCafeTable, DbCafe] = foreignKey("cafe_info", cafeInfo, cafes)(
    (cafeTable: DbCafeTable) => cafeTable.id
  )

  def barInfoQuery: ForeignKeyQuery[DbBarTable, DbBar] = foreignKey("bar_info", barInfo, bars)(
    (barTable: DbBarTable) => barTable.id
  )

}
package models.db

import java.util.UUID

import models.Chain
import slick.driver.PostgresDriver.api._
import slick.lifted.ForeignKeyQuery

case class DbCafe(id: UUID, chainID: String, address: String, openTimesFk: UUID, closeTimesFK: UUID)

class DbCafeTable(tag: Tag) extends Table[DbCafe](tag, "cafes") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def chainID: Rep[String] = column[String]("chain")

  def streetAddress: Rep[String] = column[String]("address")

  def openTimesFK: Rep[UUID] = column[UUID]("open_times")

  def closeTimesFK: Rep[UUID] = column[UUID]("close_times")

  def * = (id, chainID, streetAddress, openTimesFK, closeTimesFK) <> (DbCafe.tupled, DbCafe.unapply)

  def chain: ForeignKeyQuery[ChainTable, Chain] =
    foreignKey("id", chainID, TableQuery[ChainTable])(
      (chainT: ChainTable) => chainT.id,
      // We want to delete an eatery once the whole chain has been deleted
      onDelete = ForeignKeyAction.Cascade
    )

  private val weekTimes = TableQuery[DBWeekTimesTable]

  def openTimes: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] = foreignKey("open_times", openTimesFK, weekTimes)(
    (weekTT: DBWeekTimesTable) => weekTT.id,
    // We want to delete the times when an eatery gets deleted
    onDelete = ForeignKeyAction.Cascade
  )

  def closeTimes: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] = foreignKey("close_times", closeTimesFK, weekTimes)(
    (weekTT: DBWeekTimesTable) => weekTT.id,
    // We want to delete the times when an eatery gets deleted
    onDelete = ForeignKeyAction.Cascade
  )

}
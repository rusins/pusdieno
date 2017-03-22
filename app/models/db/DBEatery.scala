package models.db

import java.util.UUID

import slick.driver.PostgresDriver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

case class DBEatery(id: UUID, chainID: String, address: String, openTimesFk: UUID, closeTimesFK: UUID)

class DBEateryTable(tag: Tag) extends Table[DBEatery](tag, "eateries") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def chainID: Rep[String] = column[String]("chain")

  def address: Rep[String] = column[String]("address")

  def openTimesFK: Rep[UUID] = column[UUID]("open_times")

  def closeTimesFK: Rep[UUID] = column[UUID]("close_times")

  def * : ProvenShape[DBEatery] = (id, chainID, address, openTimesFK, closeTimesFK) <> (DBEatery.tupled, DBEatery.unapply)

  def chain: ForeignKeyQuery[ChainTable, Chain] = foreignKey("chain", chainID, TableQuery[ChainTable])(
    (chainT: ChainTable) => chainT.id
  )

  private val weekTimes = TableQuery[DBWeekTimesTable]

  def openTimes: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] = foreignKey("open_times", openTimesFK, weekTimes)(
    (weekTT: DBWeekTimesTable) => weekTT.id,
    // We want to delete the times when an eatery gets deleted
    onDelete = ForeignKeyAction.Cascade,
    onUpdate = ForeignKeyAction.Cascade
  )

  def closeTimes: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] = foreignKey("close_times", closeTimesFK, weekTimes)(
    (weekTT: DBWeekTimesTable) => weekTT.id,
    // We want to delete the times when an eatery gets deleted
    onDelete = ForeignKeyAction.Cascade,
    onUpdate = ForeignKeyAction.Cascade
  )

}
package models.db

import java.sql.Time
import java.util.UUID
import javax.inject.{Inject, Singleton}

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape, ShapedValue}

import scala.concurrent.Future

case class DBEatery(id: UUID, chainID: String, address: String, openTimesFk: UUID, closeTimesFK: UUID)

class DBEateryTable(tag: Tag) extends Table[DBEatery](tag, "eateries") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def chainID: Rep[String] = column[String]("chain")

  def address: Rep[String] = column[String]("address")

  def openTimesFK: Rep[UUID] = column[UUID]("open_times_fk")

  def closeTimesFK: Rep[UUID] = column[UUID]("open_times_fk")

  def * : ProvenShape[DBEatery] = (id, chainID, address, openTimesFK, closeTimesFK) <> (DBEatery.tupled, DBEatery.unapply)

  def chain: ForeignKeyQuery[ChainTable, Chain] = foreignKey("chain_fk", chainID, TableQuery[ChainTable])(
    (chainT: ChainTable) => chainT.id
  )

  private val weekTimes = TableQuery[DBWeekTimesTable]

  def openTimes: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] = foreignKey("open_times_fk", openTimesFK, weekTimes)(
    (weekTT: DBWeekTimesTable) => weekTT.id,
    // We want to delete the times when an eatery gets deleted
    onDelete = ForeignKeyAction.Cascade
  )

  def closeTimes: ForeignKeyQuery[DBWeekTimesTable, DBWeekTimes] = foreignKey("close_times_fk", closeTimesFK, weekTimes)(
    (weekTT: DBWeekTimesTable) => weekTT.id,
    // We want to delete the times when an eatery gets deleted
    onDelete = ForeignKeyAction.Cascade
  )

}
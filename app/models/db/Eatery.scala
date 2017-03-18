package models.db

import java.sql.Time
import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.OpenTimes
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.{ForeignKeyQuery, ShapedValue}

import scala.concurrent.Future

case class Eatery(
                   id: UUID = UUID.randomUUID, chainID: String, streetAddress: String, openTimes: Option[OpenTimes])

class EateryTable(tag: Tag) extends Table[Eatery](tag, "eateries") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def chainID: Rep[String] = column[String]("chain")

  def streetAddress: Rep[String] = column[String]("address")

  def mondayOpen: Rep[Option[Time]] = column[Option[Time]]("monday_open")

  def mondayClose: Rep[Option[Time]] = column[Option[Time]]("monday_close")

  def tuesdayOpen: Rep[Option[Time]] = column[Option[Time]]("tuesday_open")

  def tuesdayClose: Rep[Option[Time]] = column[Option[Time]]("tuesday_close")

  def wednesdayOpen: Rep[Option[Time]] = column[Option[Time]]("wednesday_open")

  def wednesdayClose: Rep[Option[Time]] = column[Option[Time]]("wednesday_close")

  def thursdayOpen: Rep[Option[Time]] = column[Option[Time]]("thursday_open")

  def thursdayClose: Rep[Option[Time]] = column[Option[Time]]("thursday_close")

  def fridayOpen: Rep[Option[Time]] = column[Option[Time]]("friday_open")

  def fridayClose: Rep[Option[Time]] = column[Option[Time]]("friday_close")

  def saturdayOpen: Rep[Option[Time]] = column[Option[Time]]("saturday_open")

  def saturdayClose: Rep[Option[Time]] = column[Option[Time]]("saturday_close")

  def sundayOpen: Rep[Option[Time]] = column[Option[Time]]("sunday_open")

  def sundayClose: Rep[Option[Time]] = column[Option[Time]]("sunday_close")

  private type OpenTimesTupleType = (Option[Time], Option[Time], Option[Time], Option[Time], Option[Time], Option[Time],
    Option[Time], Option[Time], Option[Time], Option[Time], Option[Time], Option[Time], Option[Time], Option[Time])
  private type EateryTupleType = (UUID, String, String, OpenTimesTupleType)

  private val eateryShapedValue = (id, chainID, streetAddress, (
    mondayOpen, mondayClose,
    tuesdayOpen, tuesdayClose,
    wednesdayOpen, wednesdayClose,
    thursdayOpen, thursdayClose,
    fridayOpen, fridayClose,
    saturdayOpen, saturdayClose,
    sundayOpen, sundayClose
  )
  ).shaped[EateryTupleType]

  private def toModel: EateryTupleType => Eatery = {
    case (id, chainID, address, (a, b, c, d, e, f, g, h, i, j, k, l, m, n)) =>
      Eatery(id, chainID, address, for {
        mo <- a
        mc <- b
        tuo <- c
        tuc <- d
        wo <- e
        wc <- f
        tho <- g
        thc <- h
        fo <- i
        fc <- j
        sao <- k
        sac <- l
        suo <- m
        suc <- n
      } yield OpenTimes((mo, mc), (tuo, tuc), (wo, wc), (tho, thc), (fo, fc), (sao, sac), (suo, suc)))
  }

  private def toTuple: Eatery => Option[EateryTupleType] =
    eatery => Some(eatery.id, eatery.chainID, eatery.streetAddress, eatery.openTimes match {
      case Some(OpenTimes((a, b), (c, d), (e, f), (g, h), (i, j), (k, l), (m, n))) =>
        (Some(a), Some(b), Some(c), Some(d), Some(e), Some(f), Some(g), Some(h), Some(i), Some(j), Some(k), Some(l), Some(m), Some(n))
      case None => (None, None, None, None, None, None, None, None, None, None, None, None, None, None)
    }
    )

  def * = eateryShapedValue <> (toModel, toTuple)

  def chain: ForeignKeyQuery[ChainTable, Chain] =
    foreignKey("chain_fk", chainID, TableQuery[ChainTable])(
      (chainT: ChainTable) => chainT.id,
      // We want to delete an eatery once the whole chain has been deleted
      onDelete = ForeignKeyAction.Cascade
    )
}
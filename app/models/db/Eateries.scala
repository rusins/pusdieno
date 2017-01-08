package models.db

import java.sql.Time
import java.util.UUID

import models.OpenTimes
import slick.driver.PostgresDriver.api._

case class Eatery(id: UUID = UUID.randomUUID, chain: String, streetAddress: String, openTimes: OpenTimes)

class EateryTable(tag: Tag) extends Table(tag, "eateries") {

  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def chain: Rep[String] = column[String]("chain")

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

  private type OpenTimesTupleType = Option[(Time, Time, Time, Time, Time, Time, Time, Time, Time, Time, Time, Time, Time, Time)]
  private type EateryTupleType = (UUID, String, String, OpenTimesTupleType)

  // TODO: Learn cats and convert tuple of options to an option of a tuple

  private val EateryShapedValue: EateryTupleType = (id, chain, streetAddress, (mondayOpen, mondayClose, tuesdayOpen, tuesdayClose,
    wednesdayOpen, wednesdayClose, thursdayOpen, thursdayClose, fridayOpen, fridayClose,
    thursdayOpen, thursdayClose, fridayOpen, fridayClose, saturdayOpen, saturdayClose,
    sundayOpen, sundayClose)).shaped[EateryTupleType]

  private def toModel: EateryTupleType => Eatery = {
    case (id, chain, address, (a, b, c, d, e, f, g)) => Eatery(id, chain, address, OpenTimes(a, b, c, d, e, f, g))
  }

  private def toTuple: Eatery => Option[EateryTupleType] = eatery => Some {
    def o = eatery.openTimes

    (eatery.id, eatery.chain, eatery.streetAddress, ((o.monday._1, o.monday._2), (o.tuesday._1, o.tuesday._2),
      (o.wednesday._1, o.wednesday._2), (o.thursday._1, o.thursday._2), (o.friday._1, o.friday._2),
      (o.saturday._1, o.saturday._2), (o.sunday._1, o.sunday._2)))
  }

  def * = <> (toModel, toTuple)
}
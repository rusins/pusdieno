package models

import slick.ast.BaseTypedType
import slick.driver.PostgresDriver.api._
import slick.jdbc.JdbcType

object EateryType extends Enumeration {
  type EateryType = Value
  val Eatery = Value("eatery")
  val Cafe = Value("cafe")

  implicit val EateryTypeMapper: JdbcType[EateryType] = MappedColumnType.base[EateryType, String](
    enum => enum.toString,
    string => EateryType.withName(string)
  )
}

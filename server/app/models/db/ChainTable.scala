package models.db

import javax.inject.{Inject, Singleton}

import models.Chain
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

import scala.concurrent.Future

class ChainTable(tag: Tag) extends Table[models.Chain](tag, "chains") {

  def id: Rep[String] = column[String]("id", O.PrimaryKey)

  def website: Rep[Option[String]] = column[Option[String]]("website")

  def menu: Rep[Option[String]] = column[Option[String]]("menu")

  def * : ProvenShape[models.Chain] = (id, website, menu) <> (Chain.tupled, Chain.unapply)
}
package models.db

import javax.inject.{Inject, Singleton}

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.ProvenShape

import scala.concurrent.Future

case class Chain(id: String, websiteURL: Option[String] = None, menuURL: Option[String] = None)

class ChainTable(tag: Tag) extends Table[Chain](tag, "chains") {

  def id: Rep[String] = column[String]("id", O.PrimaryKey)

  def website: Rep[Option[String]] = column[Option[String]]("website")

  def menu: Rep[Option[String]] = column[Option[String]]("menu")

  def * : ProvenShape[Chain] = (id, website, menu) <> (Chain.tupled, Chain.unapply)
}
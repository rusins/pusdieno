package models.db

import javax.inject.{Inject, Singleton}

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

case class Chain(id: String, websiteURL: Option[String] = None, menuURL: Option[String] = None)

class ChainTable(tag: Tag) extends Table[Chain](tag, "chains") {

  def id: Rep[String] = column[String]("id", O.PrimaryKey)

  def website: Rep[Option[String]] = column[Option[String]]("website")

  def menu: Rep[Option[String]] = column[Option[String]]("menu")

  def * = (id, website, menu) <> (Chain.tupled, Chain.unapply)
}

@Singleton
class Chains @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  private val db = dbConfigProvider.get[JdbcProfile].db

  private val chains = TableQuery[ChainTable]

  def retrieveChain(id: String): Future[Option[Chain]] = db.run(chains.filter(_.id === id).result.headOption)
}

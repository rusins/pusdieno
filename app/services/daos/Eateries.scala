package services.daos

import javax.inject.{Inject, Singleton}

import models.db.{Eatery, EateryTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

@Singleton
class Eateries @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  private val eateries = TableQuery[EateryTable]


  private val db = dbConfigProvider.get[JdbcProfile].db

  def retrieveAll(): Future[Seq[Eatery]] = db.run(eateries.result)
}
package services.daos

import javax.inject.{Inject, Singleton}

import models.db.{Cafe, CafeTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

@Singleton
class Cafes @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  private val cafes = TableQuery[CafeTable]


  private val db = dbConfigProvider.get[JdbcProfile].db

  def retrieveAll(): Future[Seq[Cafe]] = db.run(cafes.result)
}
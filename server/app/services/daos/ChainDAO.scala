package services.daos

import java.util.UUID
import javax.inject.Inject

import models.Chain
import models.db.ChainTable
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.JdbcProfile
import services.ChainService

import scala.concurrent.Future

class ChainDAO @Inject() (dbConfigProvider: DatabaseConfigProvider) extends ChainService{

  private val chains = TableQuery[ChainTable]

  private val db = dbConfigProvider.get[JdbcProfile].db

  override def add(chain: Chain): Future[Unit] = db.run(chains += chain).map(_ => Unit)

  override def delete(chainID: UUID): Future[Unit] = db.run(chains.filter(_.id === chainID).delete).map(_ => Unit)
}

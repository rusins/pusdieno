package services.daos

import javax.inject.{Inject, Singleton}

import models.Establishment
import models.db._
import models.db.establishments._
import play.api.db.slick.DatabaseConfigProvider
import services.EstablishmentService
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EstablishmentDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)
                                (implicit ex: ExecutionContext) extends EstablishmentService with TimeTable {

  protected[daos] val establishments = TableQuery[DbEstablishmentTable]
  protected[daos] val eateries = TableQuery[DbEateryTable]
  protected[daos] val cafes = TableQuery[DbCafeTable]
  protected[daos] val bars = TableQuery[DbBarTable]

  private val db = dbConfigProvider.get[JdbcProfile].db

  def retrieveAll(): Future[Seq[Establishment]] = db.run(establishments.result).flatMap { seq =>
    Future.sequence(seq.map { dbEstablishmentTable =>
      for {
        eatery <- dbEstablishmentTable.eateryInfo match {
          case None => Future.successful(None)
          case Some(uuid) => db.run((eateries.filter(_.id === uuid) join weekTimes on
            (_.openTimes === _.id) join weekTimes on (_._1.closeTimes === _.id) result).head) map {
            case ((eatery, openTimes), closeTimes) => Some(eatery.toModel(openTimes.toModel, closeTimes.toModel))
          }
        }
        cafe <- dbEstablishmentTable.cafeInfo match {
          case None => Future.successful(None)
          case Some(uuid) => db.run((cafes.filter(_.id === uuid) join weekTimes on
            (_.openTimes === _.id) join weekTimes on (_._1.closeTimes === _.id) result).head) map {
            case ((cafe, openTimes), closeTimes) => Some(cafe.toModel(openTimes.toModel, closeTimes.toModel))
          }
        }
        bar <- dbEstablishmentTable.barInfo match {
          case None => Future.successful(None)
          case Some(uuid) => db.run((bars.filter(_.id === uuid) join weekTimes on
            (_.openTimes === _.id) join weekTimes on (_._1.closeTimes === _.id) result).head) map {
            case ((bar, openTimes), closeTimes) => Some(bar.toModel(openTimes.toModel, closeTimes.toModel))
          }
        }
      } yield dbEstablishmentTable.toModel(eatery, cafe, bar)
    })
  }

  def add(establishment: Establishment): Future[Unit] = (establishment.eatery match {
    case None => Future.successful()
    case Some(eateryInfo) => db.run(DBIO.seq(
      weekTimes.insertOrUpdate(DBWeekTimes.fromModel(eateryInfo.openHours._1)),
      weekTimes.insertOrUpdate(DBWeekTimes.fromModel(eateryInfo.openHours._2)),
      eateries.insertOrUpdate(DbEatery.fromModel(establishment.id, eateryInfo))
    ).transactionally)
  }).flatMap(_ => establishment.cafe match {
    case None => Future.successful()
    case Some(cafeInfo) => db.run(DBIO.seq(
      weekTimes.insertOrUpdate(DBWeekTimes.fromModel(cafeInfo.openHours._1)),
      weekTimes.insertOrUpdate(DBWeekTimes.fromModel(cafeInfo.openHours._2)),
      cafes.insertOrUpdate(DbCafe.fromModel(establishment.id, cafeInfo))
    ).transactionally)
  }).flatMap(_ => establishment.bar match {
    case None =>Future.successful()
    case Some(barInfo) => db.run(DBIO.seq(
      weekTimes.insertOrUpdate(DBWeekTimes.fromModel(barInfo.openHours._1)),
      weekTimes.insertOrUpdate(DBWeekTimes.fromModel(barInfo.openHours._2)),
      bars.insertOrUpdate(DbBar.fromModel(establishment.id, barInfo))
    ))
  })
}
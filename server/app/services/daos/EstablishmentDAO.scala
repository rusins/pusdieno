package services.daos

import javax.inject.{Inject, Singleton}

import models.{Establishment, Restaurant}
import models.db._
import models.db.establishments.{DbBarTable, DbCafeTable, DbRestaurantTable}
import play.api.db.slick.DatabaseConfigProvider
import services.EstablishmentService
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EstablishmentDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)
                                (implicit ex: ExecutionContext) extends EstablishmentService with TimeTable {

  protected[daos] val establishments = TableQuery[DbEstablishmentTable]
  protected[daos] val restaurants = TableQuery[DbRestaurantTable]
  protected[daos] val cafes = TableQuery[DbCafeTable]
  protected[daos] val bars = TableQuery[DbBarTable]

  private val db = dbConfigProvider.get[JdbcProfile].db


/*
  override def retrieveAll(): Future[Seq[Restaurant]] = db.run(
    (for {
      e <- restaurants
      opens <- e.openTimes
      closes <- e.closeTimes
    } yield (e, opens, closes)).result
  ).map(_.map((Restaurant.fromDbRestaurant _).tupled))

  override def add(eatery: Restaurant): Future[Unit] = db.run(eatery.toDbRestaurant match {
    case (dbEatery, opens, closes) =>
      DBIO.seq(
        times += opens,
        times += closes,
        restaurants += dbEatery
      )
  })
  */
  override def retrieveAll() = db.run(
    establishments.joinLeft(restaurants).on(_.restaurantFk === _.id).joinLeft(cafes).on(_._1.cafeFk === _.id)
      .joinLeft(bars).on(_._1._1.barFk === _.id).result
  ).map(seq =>
    seq.map{ case (((DbEstablishment, DbRestaurantO), dbCafeO), dbBarO) => Esta}
  )

  override def retrieveAllRestaurants() = ???

  override def retrieveAllCafes() = ???

  override def retrieveAllBars() = ???

  override def add(establishment: Establishment) = ???
}
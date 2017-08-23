package services.daos

import javax.inject.{Inject, Singleton}

import models.Restaurant
import models.db._
import play.api.db.slick.DatabaseConfigProvider
import services.RestaurantService
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RestaurantDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)
                             (implicit ex: ExecutionContext) extends RestaurantService {

  private val db = dbConfigProvider.get[JdbcProfile].db

  private val restaurants = TableQuery[DbRestaurantTable]
  private val times = TableQuery[DBWeekTimesTable]

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
}
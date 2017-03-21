package services

import javax.inject.{Inject, Singleton}

import models.Eatery
import models.db._
import play.api.db.slick.DatabaseConfigProvider
import services.daos.Eateries
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@Singleton
class DatabasePopulator @Inject()(dbConfigProvider: DatabaseConfigProvider, eateries: Eateries) {

  private val db = dbConfigProvider.get[JdbcProfile].db
  private val chains = TableQuery[ChainTable]

  private val closed = (WeekTimes.empty, WeekTimes.empty)

  private val subway = Eatery(chainID = "subway", address = "Raiņa Bulvāris 7", openHours = closed)
  private val pankukas = Eatery(chainID = "pankukas", address = "9/11 memorial site, NY, USA", openHours = closed)
  private val kfc = Eatery(chainID = "kfc", address = "Ķekava", openHours = closed)
  private val pelmeni = Eatery(chainID = "pelmeni", address = "Vecrīgā, Kalķu 7, Rīga", openHours = closed)
  private val mcdonalds = Eatery(chainID = "mcdonalds", address = "Raiņa Bulvāris 8", openHours = closed)
  private val himalaji = Eatery(chainID = "himalaji", address = "Blaumaņa iela", openHours = closed)

  /*
  private val public = User(UUID.fromString("00000000-0000-0000-0000-000000000000"), "Public", Some(25576439), Some("pusdieno@krikis.org"), WeekPlan.empty)
  private val dalai = User(id = UUID.fromString("00000000-0000-0000-0000-000000000001"), mobile = Some(42042069), name = "Dalai Lama", eatsAt = WeekPlan.empty)
  private val vaira = User(id = UUID.fromString("00000000-0000-0000-0000-000000000002"), name = "Vaira Vīķe Freiberga", eatsAt = WeekPlan.empty)
  private val tyrion = User(id = UUID.fromString("00000000-0000-0000-0000-000000000003"), name = "Tyrion Lannister", eatsAt = WeekPlan.empty)
  private val martins = User(id = UUID.fromString("00000000-0000-0000-0000-000000000004"), name = "Mārtiņš Rītiņš", eatsAt = WeekPlan.empty)
  private val ziedonis = User(id = UUID.fromString("00000000-0000-0000-0000-000000000005"), name = "Imants Ziedonis", eatsAt = WeekPlan.empty)
  private val twisty = User(id = UUID.fromString("00000000-0000-0000-0000-000000000006"), name = "Twisty the clown", eatsAt = WeekPlan.empty)
  private val steve = User(id = UUID.fromString("00000000-0000-0000-0000-000000000007"), name = "Steve Buscemi", eatsAt = WeekPlan.empty)
  private val margaret = User(id = UUID.fromString("00000000-0000-0000-0000-000000000008"), name = "Margaret Thatcher", eatsAt = WeekPlan.empty)
*/

  private val initialFuture: Future[Unit] = db run DBIO.seq(
    chains.delete,
    chains += Chain(id = "subway"),
    chains += Chain(id = "pankukas"),
    chains += Chain(id = "kfc"),
    chains += Chain(id = "pelmeni"),
    chains += Chain(id = "mcdonalds"),
    chains += Chain(id = "himalaji")
  )

  Await.result(initialFuture, Duration.Inf)
  eateries.add(subway)
  eateries.add(pankukas)
  eateries.add(kfc)
  eateries.add(pelmeni)
  eateries.add(mcdonalds)
  eateries.add(himalaji)
}

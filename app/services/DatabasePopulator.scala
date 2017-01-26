package services

import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.WeekPlan
import models.db._
import play.api.db.slick.DatabaseConfigProvider
import play.api.routing.Router
import play.core.routing.Route
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration

@Singleton
class DatabasePopulator @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  private val db = dbConfigProvider.get[JdbcProfile].db
  private val chains = TableQuery[ChainTable]
  private val eateries = TableQuery[EateryTable]
  private val users = TableQuery[UserTable]
  private val eateryChoices = TableQuery[EateryChoiceTable]
  private val contacts = TableQuery[ContactTable]

  private val subway = Eatery(chainID = "subway", streetAddress = "Raiņa Bulvāris 7", openTimes = None)
  private val pankukas = Eatery(chainID = "pankukas", streetAddress = "9/11 memorial site, NY, USA", openTimes = None)
  private val kfc = Eatery(chainID = "kfc", streetAddress = "Ķekava", openTimes = None)
  private val pelmeni = Eatery(chainID = "pelmeni", streetAddress = "Vecrīgā, Kalķu 7, Rīga", openTimes = None)
  private val mcdonalds = Eatery(id = UUID.fromString("00000000-0000-0000-0000-000000000000"), chainID = "mcdonalds", streetAddress = "Raiņa Bulvāris 8", openTimes = None)
  private val himalaji = Eatery(chainID = "himalaji", streetAddress = "Blaumaņa iela", openTimes = None)

  private val public = User(UUID.fromString("00000000-0000-0000-0000-000000000000"), "Public", Some(25576439), Some("pusdieno@krikis.org"), WeekPlan.empty)
  private val dalai = User(id = UUID.fromString("00000000-0000-0000-0000-000000000001"), mobile = Some(42042069), name = "Dalai Lama", eatsAt = WeekPlan.empty)
  private val vaira = User(id = UUID.fromString("00000000-0000-0000-0000-000000000002"), name = "Vaira Vīķe Freiberga", eatsAt = WeekPlan.empty)
  private val tyrion = User(id = UUID.fromString("00000000-0000-0000-0000-000000000003"), name = "Tyrion Lannister", eatsAt = WeekPlan.empty)
  private val martins = User(id = UUID.fromString("00000000-0000-0000-0000-000000000004"), name = "Mārtiņš Rītiņš", eatsAt = WeekPlan.empty)
  private val ziedonis = User(id = UUID.fromString("00000000-0000-0000-0000-000000000005"), name = "Imants Ziedonis", eatsAt = WeekPlan.empty)
  private val twisty = User(id = UUID.fromString("00000000-0000-0000-0000-000000000006"), name = "Twisty the clown", eatsAt = WeekPlan.empty)
  private val steve = User(id = UUID.fromString("00000000-0000-0000-0000-000000000007"), name = "Steve Buscemi", eatsAt = WeekPlan.empty)
  private val margaret = User(id = UUID.fromString("00000000-0000-0000-0000-000000000008"), name = "Margaret Thatcher", eatsAt = WeekPlan.empty)


  private val initialFuture: Future[Unit] = (db run DBIO.seq(
    chains.delete,
    chains += Chain(id = "subway"),
    chains += Chain(id = "pankukas"),
    chains += Chain(id = "kfc"),
    chains += Chain(id = "pelmeni"),
    chains += Chain(id = "mcdonalds"),
    chains += Chain(id = "himalaji"),
    eateries += subway,
    eateries += pankukas,
    eateries += kfc,
    eateries += pelmeni,
    eateries += mcdonalds,
    eateries += himalaji,
    users.delete,
    users += public,
    users += dalai,
    users += vaira,
    users += tyrion,
    users += martins,
    users += ziedonis,
    users += twisty,
    users += steve,
    users += margaret
  )).flatMap { _ =>
    db run DBIO.seq(
      eateryChoices.delete,
      eateryChoices += Choice(user = dalai.id, eatery = himalaji.id),
      eateryChoices += Choice(user = dalai.id, eatery = mcdonalds.id),
      eateryChoices += Choice(user = steve.id, eatery = mcdonalds.id),
      eateryChoices += Choice(user = twisty.id, eatery = mcdonalds.id),
      eateryChoices += Choice(user = public.id, eatery = mcdonalds.id),
      eateryChoices += Choice(user = tyrion.id, eatery = mcdonalds.id),
      eateryChoices += Choice(user = ziedonis.id, eatery = mcdonalds.id),
      contacts.delete,
      contacts += Contact(ownerID = public.id, contactID = Some(dalai.id)),
      contacts += Contact(ownerID = dalai.id, contactID = Some(public.id)),
      contacts += Contact(ownerID = public.id, contactID = Some(vaira.id)),
      contacts += Contact(ownerID = public.id, contactID = Some(twisty.id)),
      contacts += Contact(ownerID = twisty.id, contactID = Some(public.id)),
      contacts += Contact(ownerID = public.id, contactID = Some(tyrion.id)),
      contacts += Contact(ownerID = tyrion.id, contactID = Some(public.id)),
      contacts += Contact(ownerID = public.id, contactID = Some(ziedonis.id)),
      contacts += Contact(ownerID = ziedonis.id, contactID = Some(public.id)),
      contacts += Contact(ownerID = public.id, contactID = Some(margaret.id)),
      contacts += Contact(ownerID = margaret.id, contactID = Some(public.id))
    )
  }

  Await.result(initialFuture, Duration.Inf)
}

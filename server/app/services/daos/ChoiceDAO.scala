package services.daos

import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.{Choice, User}
import models.db._
import models.helpers.Lusts
import play.api.db.slick.DatabaseConfigProvider
import services.{ChoiceService, UserService}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class ChoiceDAO @Inject()(dbConfigProvider: DatabaseConfigProvider, establishmentDAO: EstablishmentDAO, ex: ExecutionContext) extends ChoiceService {

  private val choices = TableQuery[ChoiceTable]

  private val db = dbConfigProvider.get[JdbcProfile].db

  protected[daos] def wantsFoodQuery(userID: Rep[UUID]): Rep[Boolean] =
    choices.filter(_.user === userID).flatMap(_.establishmentQuery).flatMap(_.eateryInfoQuery).exists

  protected[daos] def wantsCoffeeQuery(userID: Rep[UUID]): Rep[Boolean] =
    choices.filter(_.user === userID).flatMap(_.establishmentQuery).flatMap(_.cafeInfoQuery).exists

  protected[daos] def wantsAlcoholQuery(userID: Rep[UUID]): Rep[Boolean] =
    choices.filter(_.user === userID).flatMap(_.establishmentQuery).flatMap(_.barInfoQuery).exists

  override def makeChoice(userID: UUID, eateryID: UUID) = ???

  override def deleteChoice(userID: UUID, eateryID: UUID) = ???

  override def clearRestaurantChoices(userID: UUID) = ???

  override def clearCafeChoices(userID: UUID) = ???

  override def wantsFood(userID: UUID): Future[Boolean] = db.run(wantsCoffeeQuery(userID).result)

  override def wantsCoffee(userID: UUID): Future[Boolean] = db.run(wantsCoffeeQuery(userID).result)

  override def wantsAlcohol(userID: UUID): Future[Boolean] = db.run(wantsAlcoholQuery(userID).result)
}
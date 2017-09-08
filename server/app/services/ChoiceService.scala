package services

import java.util.UUID

import models.helpers.Lusts

import scala.concurrent.Future

trait ChoiceService {

  def makeChoice(userID: UUID, eateryID: UUID): Future[Unit]

  def deleteChoice(userID: UUID, eateryID: UUID): Future[Unit]

  def clearRestaurantChoices(userID: UUID): Future[Unit]

  def clearCafeChoices(userID: UUID): Future[Unit]

  def wantsFood(userID: UUID): Future[Boolean]

  def wantsCoffee(userID: UUID): Future[Boolean]

  def wantsAlcohol(userID: UUID): Future[Boolean]

  def getLusts(userID: UUID): Future[Lusts] = for {
    food <- wantsFood(userID)
    coffee <- wantsCoffee(userID)
    alcohol <- wantsAlcohol(userID)
  } yield Lusts(food, coffee, alcohol)

}

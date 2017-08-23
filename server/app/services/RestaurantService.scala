package services

import models.Restaurant

import scala.concurrent.Future

trait RestaurantService {

  def retrieveAll(): Future[Seq[Restaurant]]

  def add(eatery: Restaurant): Future[Unit]
}

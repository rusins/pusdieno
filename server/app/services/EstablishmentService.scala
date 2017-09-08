package services

import models.{Bar, Cafe, Establishment, Restaurant}

import scala.concurrent.Future

trait EstablishmentService {

  def retrieveAll(): Future[Seq[Establishment]]

  def retrieveAllRestaurants(): Future[Seq[Restaurant]]

  def retrieveAllCafes(): Future[Seq[Cafe]]

  def retrieveAllBars(): Future[Seq[Bar]]

  def add(establishment: Establishment): Future[Unit]
}

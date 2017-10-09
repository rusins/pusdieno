package services

import models.Establishment

import scala.concurrent.Future

trait EstablishmentService {

  def retrieveAll(): Future[Seq[Establishment]]

  def retrieveAllRestaurants(): Future[Seq[Establishment]] = retrieveAll().map(_.filterNot(_.eatery.isEmpty))

  def retrieveAllCafes(): Future[Seq[Establishment]] = retrieveAll().map(_.filterNot(_.cafe.isEmpty))

  def retrieveAllBars(): Future[Seq[Establishment]] = retrieveAll().map(_.filterNot(_.bar.isEmpty))

  def add(establishment: Establishment): Future[Unit]
}

package services

import models.Cafe

import scala.concurrent.Future

trait CafeService {

  def retrieveAll(): Future[Seq[Cafe]]

  def add(eatery: Cafe): Future[Unit]
}

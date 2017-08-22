package services

import models.Eatery

import scala.concurrent.Future

trait EateryService {

  def retrieveAll(): Future[Seq[Eatery]]

  def add(eatery: Eatery): Future[Unit]
}

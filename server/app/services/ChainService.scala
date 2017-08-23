package services

import models.Chain

import scala.concurrent.Future

trait ChainService {

  def clearAll(): Future[Unit]

  def add(chain: Chain): Future[Unit]

}

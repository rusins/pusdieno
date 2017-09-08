package services

import java.util.UUID

import models.Chain

import scala.concurrent.Future

trait ChainService {

  def add(chain: Chain): Future[Unit]

  def delete(chainID: UUID): Future[Unit]

}

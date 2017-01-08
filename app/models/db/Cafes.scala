package models.db

import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.OpenTimes
import play.api.db.slick.DatabaseConfigProvider

case class Cafe(id: UUID = UUID.randomUUID(), chain: String, streetAddress: String, openTimes: OpenTimes)


@Singleton
class Cafes @Inject() (dbConfigProvider: DatabaseConfigProvider){

}

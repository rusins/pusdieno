package modules

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import models.Cafe
import net.codingwell.scalaguice.ScalaModule
import services.{CafeService, RestaurantService, UserService}
import services.daos._

class DbAccessModule extends AbstractModule with ScalaModule{

  override def configure(): Unit = {
    bind[DelegableAuthInfoDAO[OAuth2Info]].to[OAuth2InfoDAO]
    bind[UserService].to[UserDAO]
    bind[RestaurantService].to[RestaurantDAO]
    bind[CafeService].to[Cafe]
    bind[ContactService].to[ContactDAO] // WARNING!!! ContactDAO depends on UserDAO, meaning you can't separate their respective implementations

  }

}

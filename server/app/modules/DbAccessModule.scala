package modules

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import models.Cafe
import net.codingwell.scalaguice.ScalaModule
import services.{CafeService, EateryService, UserService}
import services.daos.{EateryDAO, OAuth2InfoDAO, UserDAO}

class DbAccessModule extends AbstractModule with ScalaModule{

  override def configure(): Unit = {
    bind[DelegableAuthInfoDAO[OAuth2Info]].to[OAuth2InfoDAO]
    bind[UserService].to[UserDAO]
    bind[EateryService].to[EateryDAO]
    bind[CafeService].to[Cafe]
  }

}

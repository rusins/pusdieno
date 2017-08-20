package modules

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import net.codingwell.scalaguice.ScalaModule
import services.UserService
import services.daos.{OAuth2InfoDAO, UserDAO}

class DbAccessModule extends AbstractModule with ScalaModule{

  override def configure(): Unit = {
    bind[DelegableAuthInfoDAO[OAuth2Info]].to[OAuth2InfoDAO]
    bind[UserService].to[UserDAO]
  }

}

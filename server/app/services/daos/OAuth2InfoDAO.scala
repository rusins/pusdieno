package services.daos

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import models.db._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

/**
  * The DAO to store the OAuth2 information.
  */
class OAuth2InfoDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, ex: ExecutionContext)
  extends DelegableAuthInfoDAO[OAuth2Info] {

  protected[daos] val db = dbConfigProvider.get[JdbcProfile].db
  protected[daos] val logins = TableQuery[DBLoginInfoTable]
  protected[daos] val oAuth2Infos = TableQuery[DBOAuth2InfoTable]

  def toOAuth2Info(i: DBOAuth2Info) = OAuth2Info(i.accessToken, i.tokenType, i.expiresIn, i.refreshToken)

  private def loginQuery(loginInfo: LoginInfo): Query[DBLoginInfoTable, DBLoginInfo, Seq] =
    logins.filter(l => l.providerID === loginInfo.providerID && l.providerKey === loginInfo.providerKey)

  // Use subquery workaround instead of join to get authinfo because slick only supports selecting
  // from a single table for update/delete queries (https://github.com/slick/slick/issues/684).
  private def oAuth2InfoSubQuery(loginInfo: LoginInfo) = oAuth2Infos.filter(_.loginInfoID in loginQuery(loginInfo).map(_.id))

  // Not sure why I need / have this too ¯\_(ツ)_/¯
  private def oAuth2InfoQuery(loginInfo: LoginInfo) = for {
    (dbLoginInfo, dbOAuth2InfoO) <- logins.joinLeft(oAuth2Infos).on(_.id === _.loginInfoID)
  } yield dbOAuth2InfoO

  private def addAction(loginInfo: LoginInfo, authInfo: OAuth2Info) =
    loginQuery(loginInfo).result.head.flatMap { dbLoginInfo =>
    oAuth2Infos += DBOAuth2Info(UUID.randomUUID(), dbLoginInfo.id, authInfo.accessToken, authInfo.tokenType,
      authInfo.expiresIn, authInfo.refreshToken)
  }.transactionally


  private def updateAction(loginInfo: LoginInfo, authInfo: OAuth2Info) =
    oAuth2InfoSubQuery(loginInfo).map(
      dbAuth2Info => (dbAuth2Info.accessToken, dbAuth2Info.tokenType, dbAuth2Info.expiresIn, dbAuth2Info.refreshToken)).
      update((authInfo.accessToken, authInfo.tokenType, authInfo.expiresIn, authInfo.refreshToken))


  override def find(loginInfo: LoginInfo): Future[Option[OAuth2Info]] =
    db.run(oAuth2InfoSubQuery(loginInfo).result.headOption).map(_.map(toOAuth2Info))

  /**
    * ONLY SAVES
    **/
  override def add(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] =
    db.run(addAction(loginInfo, authInfo)).map(_ => authInfo)

  override def update(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] =
    db.run(updateAction(loginInfo, authInfo)).map(_ => authInfo)

  /**
    * SAVES IF DOESN'T EXIST, UPDATE OTHERWISE
    */
  override def save(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = {

    val query = for {
      result <- loginQuery(loginInfo).joinLeft(oAuth2Infos).on(_.id === _.loginInfoID)
    } yield result

    val action = query.result.head.flatMap {
      case (_, Some(_)) => updateAction(loginInfo, authInfo)
      case (_, None) => addAction(loginInfo, authInfo)
    }.transactionally

    db.run(action.named("Saving user in OAuth2InfoDAO")).map(_ => authInfo)
  }

  override def remove(loginInfo: LoginInfo): Future[Unit] = db.run(oAuth2InfoSubQuery(loginInfo).delete).map(_ => Unit)
}
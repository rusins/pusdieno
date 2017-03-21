package services.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import javax.inject.Inject

import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import models.db._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.Effect.{Read, Transactional, Write}
import slick.driver
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

/**
  * The DAO to store the OAuth2 information.
  */
class OAuth2InfoDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends DelegableAuthInfoDAO[OAuth2Info] {

  private val db = dbConfigProvider.get[JdbcProfile].db
  private val logins = TableQuery[DBLoginInfoTable]
  private val oAuth2Infos = TableQuery[DBOAuth2InfoTable]

  def toOAuth2Info(i: DBOAuth2Info) = OAuth2Info(i.accessToken, i.tokenType, i.expiresIn, i.refreshToken)

  private def loginQuery(loginInfo: LoginInfo): Query[DBLoginInfoTable, DBLoginInfo, Seq] =
    logins.filter(l => l.providerID === loginInfo.providerID && l.providerKey === loginInfo.providerKey)

  // Use subquery workaround instead of join to get authinfo because slick only supports selecting
  // from a single table for update/delete queries (https://github.com/slick/slick/issues/684).
  private def oAuth2InfoSubQuery(loginInfo: LoginInfo) = oAuth2Infos.filter(_.id in loginQuery(loginInfo))

  // Not sure why I need / have this too ¯\_(ツ)_/¯
  private def oAuth2InfoQuery(loginInfo: LoginInfo): DBIOAction[Option[DBOAuth2Info], NoStream, Read] = for {
    dbLoginO: Option[DBLoginInfo] <- loginQuery(loginInfo).result
    dbLogin: DBLoginInfo <- dbLoginO
    oAuth2InfoO: Option[DBOAuth2Info] <- oAuth2Infos.filter(_.loginInfoFK === dbLogin.id).result.headOption
  } yield oAuth2InfoO

  private def addAction(loginInfo: LoginInfo, authInfo: OAuth2Info) = for {
    dbLoginO: Option[DBLoginInfo] <- loginQuery(loginInfo).result.headOption
    dbLogin: DBLoginInfo <- dbLoginO
    res <- oAuth2Infos += DBOAuth2Info(UUID.randomUUID(), dbLogin.id, authInfo.accessToken, authInfo.tokenType,
      authInfo.expiresIn, authInfo.refreshToken)
  } yield res.result.transactionally


  private def updateAction(loginInfo: LoginInfo, authInfo: OAuth2Info) =
    oAuth2InfoSubQuery(loginInfo).map(
      dbAuth2Info => (dbAuth2Info.accessToken, dbAuth2Info.tokenType, dbAuth2Info.expiresIn, dbAuth2Info.refreshToken)).
      update((authInfo.accessToken, authInfo.tokenType, authInfo.expiresIn, authInfo.refreshToken))


  override def find(loginInfo: LoginInfo): Future[Option[OAuth2Info]] =
    db.run(oAuth2InfoQuery(loginInfo)).map(_.map(toOAuth2Info))

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
      result <- loginQuery(loginInfo).joinLeft(oAuth2Infos).on(_.id === _.loginInfoFK)
    } yield result

    val action = query.result.head.flatMap {
      case (_, Some(_)) => updateAction(loginInfo, authInfo)
      case (_, None) => addAction(loginInfo, authInfo)
    }.transactionally

    db.run(action).map(_ => authInfo)
  }

  override def remove(loginInfo: LoginInfo): Future[Unit] = db.run(oAuth2InfoSubQuery(loginInfo).delete).map(_ => Unit)
}
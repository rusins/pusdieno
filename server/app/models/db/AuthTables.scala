package models.db

import java.util.UUID

import slick.driver.PostgresDriver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

case class DBLoginInfo(id: UUID = UUID.randomUUID(),
                       providerID: String,
                       providerKey: String,
                       userID: UUID)

class DBLoginInfoTable(tag: Tag) extends Table[DBLoginInfo](tag, "login_info") {
  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def providerID: Rep[String] = column[String]("provider_id")

  def providerKey: Rep[String] = column[String]("provider_key")

  def userID: Rep[UUID] = column[UUID]("user_id")

  def * : ProvenShape[DBLoginInfo] = (id, providerID, providerKey, userID) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
}

case class DBOAuth2Info(id: UUID,
                        loginInfoFK: UUID,
                        accessToken: String,
                        tokenType: Option[String],
                        expiresIn: Option[Int],
                        refreshToken: Option[String])

class DBOAuth2InfoTable(tag: Tag) extends Table[DBOAuth2Info](tag, "oauth2info") {
  def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)

  def loginInfoID: Rep[UUID] = column[UUID]("login_info")

  def accessToken: Rep[String] = column[String]("access_token")

  def tokenType: Rep[Option[String]] = column[Option[String]]("token_type")

  def expiresIn: Rep[Option[Int]] = column[Option[Int]]("expires_in")

  def refreshToken: Rep[Option[String]] = column[Option[String]]("refresh_token")

  def * : ProvenShape[DBOAuth2Info] = (id, loginInfoID, accessToken, tokenType, expiresIn, refreshToken) <>
    (DBOAuth2Info.tupled, DBOAuth2Info.unapply)

  def loginInfo: ForeignKeyQuery[DBLoginInfoTable, DBLoginInfo] =
    foreignKey("login_info", loginInfoID, TableQuery[DBLoginInfoTable])(
      (loginIT: DBLoginInfoTable) => loginIT.id,
      // We want to delete the auth info if the login info gets deleted or changed
      onDelete = ForeignKeyAction.Cascade,
      onUpdate = ForeignKeyAction.Cascade
    )
}
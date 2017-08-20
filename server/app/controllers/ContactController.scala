package controllers

import java.util.UUID
import javax.inject.Inject

import utils.CookieEnv
import com.mohiva.play.silhouette.api.{Authorization, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.User
import models.db.Contact
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc._
import services.daos.ContactsDAO
import views.{ContactView, ErrorView}

import scala.concurrent.{ExecutionContext, Future}

case class ContactData(name: String, phone: Option[Int], email: Option[String])

class ContactController @Inject()(silhouette: Silhouette[CookieEnv],
                                  contacts: ContactsDAO)
                                 (implicit ex: ExecutionContext)
  extends InjectedController with I18nSupport {

  def contactForm()(implicit messagesProvider: MessagesProvider) = Form(
    mapping(
      "name" -> nonEmptyText,
      "phone" -> optional(number(min = 1000000, max = 99999999)),
      "email" -> optional(email)
    )(ContactData.apply)(ContactData.unapply) verifying(Messages("error.contact-form.missing-info"),
      (contactData: ContactData) => contactData.phone.nonEmpty || contactData.email.nonEmpty
    )
  )

  case class canEdit(contactID: UUID) extends Authorization[User, CookieAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: CookieAuthenticator)(
      implicit request: Request[B]): Future[Boolean] =
      contacts.belongsTo(contactID).map {
        case Some(id) => id == identity.id
        case None => true
      }
  }

  def create: Action[AnyContent] = silhouette.SecuredAction { implicit request =>
    val contactData = ContactData("", None, None)
    Ok(ContactView.editContact(request.identity, contactForm.fill(contactData), UUID.randomUUID()))
  }

  def edit(contactID: UUID): Action[AnyContent] = silhouette.SecuredAction(canEdit(contactID)).async { implicit request =>
    contacts.get(contactID).map {
      case None => ErrorView.unauthorized("Invalid UUID!")
      case Some(contact) => {
        val contactData = ContactData(contact.name, contact.phone, contact.email)
        Ok(ContactView.editContact(request.identity, contactForm.fill(contactData), contactID))
      }
    }
  }

  def save(contactID: UUID): Action[AnyContent] = silhouette.SecuredAction(canEdit(contactID)).async { implicit request =>
    contactForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(ContactView.editContact(request.identity, formWithErrors, contactID)))
      },
      contactData => {
        val contact = Contact(id = contactID, name = contactData.name, ownerID = request.identity.id, contactID = None,
          phone = contactData.phone, email = contactData.email)
        contacts.save(contact).map { _ =>
          Redirect(routes.ContactController.index().url)
        }
      }
    )
  }

  def delete(contactID: UUID): Action[AnyContent] = silhouette.SecuredAction(canEdit(contactID)).async {
    implicit request =>
      contacts.delete(contactID).map(_ => Redirect(routes.ContactController.index().url))
  }

  def index: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    contacts.contactsWithOptionalDBUserInfo(request.identity.id).map(seq =>
      Ok(ContactView.index(request.identity, seq))
    )
  }
}

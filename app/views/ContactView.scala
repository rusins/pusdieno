package views

import java.util.UUID

import controllers.{ContactData, routes}
import models.User
import models.db.{Contact, DBUser}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.RequestHeader
import play.twirl.api.Html

import scalatags.Text.all._

object ContactView {

  def index(user: User, seq: Seq[(Contact, Option[DBUser])])(implicit request: RequestHeader, messages: Messages): Html = {
    val headers = Seq(
      script(src := "/assets/javascripts/list.min.js"),
      script(src := "/assets/javascripts/list.fuzzysearch.min.js")
    )

    val body = Seq(
      div(cls := "container", paddingTop := 10)(
        div(cls := "contact-list")(
          div(cls := "panel  panel-default panel-body", paddingTop := 0)(
            ol(cls := "nav nav-pills")(
              li(marginTop := 15)(
                a(href := routes.FriendsController.index().url)(messages("friends"))
              ),
              li(marginTop := 15, cls := "active")(
                a(href := routes.ContactController.index().url)(messages("contacts"))
              ),
              li(marginTop := 15)(
                a(href := routes.ContactController.create().url)(messages("contacts.create"))
              ),
              li(marginTop := 15, float.right)(
                input(cls := "fuzzy-search form-control", `type` := "text", placeholder := messages("search"))
              )
            )
          ),
          table(cls := "list table table-stripped table-hover", style := "list-style-type: none;", paddingLeft := 0)(
            thead(
              tr(
                th(), // avatar image
                th(messages("contacts.name")),
                th(messages("contacts.email")),
                th(messages("contacts.phone")),
                th() // edit and delete buttons
              )
            ),
            seq.map { case (contact, friend) =>
              tr(cls := friend.map(_ => "success").getOrElse(""))(
                td(friend.map(f => img(cls := "img-circle", src := f.avatarURL.getOrElse(""))).getOrElse(UnitFrag(Unit))),
                td(contact.name, br, friend.map(_.name)),
                td(contact.email),
                td(a(href := contact.phone.map("tel:" + _).getOrElse(""))(contact.phone)),
                td(span(float.right)(
                  a(cls := "btn btn-info", href := routes.ContactController.edit(contact.id).url)(messages("contacts.edit")),
                  form(action := routes.ContactController.delete(contact.id).url, method := "post", display.inline)(
                    button(cls := "btn btn-danger", `type` := "submit", marginLeft := 5)(messages("contacts.delete"))
                  )
                ))
              )
            }
          )
        )
      ),
      script(raw(
        """
          |var options = {
          |  valueNames: [ 'name', 'phone', 'email' ],
          |  plugins: [ ListFuzzySearch() ]
          |};
          |
          |var contactList = new List('contact-list', options);
        """.stripMargin))
    )

    MainTemplate(messages("contacts"), "friends", headers, body, Some(user))
  }

  def editContact(user: User, contactForm: Form[ContactData], contactID: UUID)(implicit request: RequestHeader, messages: Messages): Html = {
    val headers = UnitFrag(Unit)

    val body: Frag = div(cls := "container", paddingTop := 10)(
      div(cls := "contact-list")(
        div(cls := "panel  panel-default panel-body", paddingTop := 0)(
          ol(cls := "nav nav-pills")(
            li(marginTop := 15)(
              a(href := routes.FriendsController.index().url)(messages("friends"))
            ),
            li(marginTop := 15)(
              a(href := routes.ContactController.index().url)(messages("contacts"))
            ),
            li(marginTop := 15, cls := "active")(
              a(href := routes.ContactController.create().url)(messages("contacts.create"))
            )
          )
        ),
        div(cls := "well")(
          form(cls := "form-horizontal", method := "post", action := routes.ContactController.save(contactID).url)(
            //raw(views.html.helper.CSRF.formField.body),
            fieldset(
              div(cls := "form-group row" + contactForm.error("name").fold("")(_ => " has-error"))(
                label(`for` := "inputName", cls := "col-md-2 control-label")(messages("contacts.name"), sup(color.red)("*")),
                div(cls := "col-md-10")(
                  input(name := "name", cls := "form-control", id := "inputName", `type` := "text",
                    value := contactForm.data.getOrElse("name", "")),
                  contactForm.error("name") match {
                    case None => UnitFrag(Unit)
                    case Some(error) => div(p(cls := "text-primary")(messages(error.message)))
                  },
                  small(cls := "form-text text-muted")(messages("form.required"))
                )
              ),
              div(cls := "form-group row" + contactForm.error("email").fold("")(_ => " has-error"))(
                label(`for` := "inputEmail", cls := "col-md-2 control-label")(messages("contacts.email")),
                div(cls := "col-md-10")(input(`type` := "email", name := "email", cls := "form-control", id := "inputEmail",
                  `type` := "text", value := contactForm.data.getOrElse("email", "")),
                  contactForm.error("email") match {
                    case None => UnitFrag(Unit)
                    case Some(error) => div(p(cls := "text-primary")(messages(error.message)))
                  },
                  small(cls := "form-text text-muted")(messages("form.optional"))
                )
              ),
              div(cls := "form-group row" + contactForm.error("phone").fold("")(_ => " has-error"))(
                label(`for` := "inputPhone", cls := "col-md-2 control-label")(messages("contacts.phone")),
                div(cls := "col-md-10")(input(name := "phone", cls := "form-control", id := "inputPhone",
                  `type` := "text", value := contactForm.data.getOrElse("phone", "")),
                  contactForm.error("phone") match {
                    case None => UnitFrag(Unit)
                    case Some(error) => div(p(cls := "text-primary")(messages(error.message)))
                  },
                  small(cls := "form-text text-muted")(messages("form.optional"))
                )
              ),
              div(cls := "form-group row")(
                div(cls := "col-md-10 col-md-offset-2")(
                  a(href := routes.ContactController.index().url, cls := "btn btn-default")(messages("form.cancel")),
                  button(`type` := "submit", cls := "btn btn-primary", marginLeft := 5)(messages("form.submit"))
                )
              ),
              contactForm.globalError match {
                case None => UnitFrag(Unit)
                case Some(error) => p(cls := "text-primary")(error.message)
              }
            )
          )
        )
      )
    )

    MainTemplate.apply(messages("contacts.create"), "friends", headers, body, Some(user))
  }
}

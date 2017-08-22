package views

import controllers.routes
import models.User
import play.api.i18n.{Messages, MessagesProvider}
import play.twirl.api.Html
import utils.Languages
import views.styles.CommonStyleSheet

import scala.language.implicitConversions
import scalacss.ProdDefaults._
import scalacss.ScalatagsCss._
import scalatags.Text._
import scalatags.Text.all._

object MainTemplate {

  class IfBoolean(val value: Boolean) extends AnyVal {
    def ?[T](a: => T, b: => T): T = if (value) a else b
  }

  implicit def BoolToIfBool(value: Boolean): IfBoolean = new IfBoolean(value)


  def apply(pageTitle: String, section: String, headContent: Frag, bodyContent: Frag, userO: Option[User])(
    implicit messagesProvider: MessagesProvider): Html = {

    val lang = messagesProvider.messages.lang

    Html(all.html(
      head(
        meta(charset := "utf-8"),
        meta(name := "viewport", content := "width=device-width, initial-scale=1"),
        tags2.title(pageTitle + "– Pusdieno"),
        link(rel := "stylesheet", media := "screen", href := "/assets/stylesheets/bootstrap.min.css"),
        link(rel := "shortcut icon", `type` := "image/png", href := "/assets/images/favicon-16.png"),
        script(src := "/assets/javascripts/jquery-3.1.1.min.js"),
        script(src := "/assets/javascripts/bootstrap.min.js"),
        link(rel := "stylesheet", media := "screen", href := "/assets/stylesheets/navbar-main.css"),
        CommonStyleSheet.render[scalatags.Text.TypedTag[String]],
        headContent
      ),
      body(paddingTop := 60)(
        tags2.nav(cls := "navbar navbar-inverse navbar-fixed-top", marginBottom := 0)(div(cls := "container")(
          div(cls := "navbar-header")(
            button(`type` := "button", cls := "navbar-toggle collapsed", data.toggle := "collapse",
              data.target := "#navbar", aria.expanded := "false", aria.controls := "navbar")(
              span(cls := "sr-only")(Messages("toggle.navigation")),
              span(cls := "icon-bar"),
              span(cls := "icon-bar"),
              span(cls := "icon-bar")
            ),
            a(cls := "navbar-brand", href := "/")("Pusdieno!")
          ),
          div(id := "navbar", cls := "navbar-collapse collapse")(
            ul(cls := "nav navbar-nav")(
              li(cls := (section == "overview").?("active", ""))(a(href := "/overview")(Messages("overview"))),
              li(cls := (section == "eateries").?("active", ""))(a(href := "/eateries")(Messages("eateries"))),
              li(cls := (section == "friends").?("active", ""))(a(href := "/friends")(Messages("friends")))
            ),
            SeqFrag(if (userO.nonEmpty) Seq() else
              Seq(form(cls := "navbar-form navbar-left", action := routes.AuthController.signIn().url)(
                button(`type` := "submit", cls := "btn btn-success")(Messages("sign-in"))
              ))
            ),
            ul(cls := "nav navbar-nav navbar-right")(
              li(cls := "dropdown")(
                userO match {
                  case None => SeqFrag(Seq(
                    a(paddingTop := 19, height := 60, href := "#", cls := "dropdown-toggle",
                      data.toggle := "dropdown", aria.expanded := false)(
                      img(width := 30, src := "/assets/images/flags/" + lang.code + ".png"),
                      " " + Messages("language"),
                      span(cls := "caret")
                    ),
                    ul(cls := "dropdown-menu", backgroundColor := "#eb6864")(
                      form(action := routes.LanguageController.changeLanguage().url, method := "POST", cls := "form-inline")(
                        //raw(views.html.helper.CSRF.formField.body),
                        SeqFrag(for ((code, language) <- (Languages.supported - lang.code).toSeq) yield li(
                          button(`type` := "submit", name := "languageCode", value := code, backgroundColor := "#eb6864",
                            paddingTop := 6, paddingRight := 20, paddingLeft := 20, border := 0, color := "#FFFFFF")(
                            img(width := 2.em, src := s"/assets/images/flags/$code.png", alt := code),
                            " " + language
                          )
                        ))
                      )
                    )
                  ))
                  case Some(user) => SeqFrag(Seq(
                    a(paddingTop := 19, height := 60, href := "#", cls := "dropdown-toggle",
                      data.toggle := "dropdown", aria.expanded := false)(
                      img(width := 30, src := user.avatarURL.getOrElse(routes.Assets.
                        versioned("images/icons/ic_account_circle_black_36px.svg").url),
                        cls := "img-circle"),
                      " " + user.name,
                      span(cls := "caret")
                    ),
                    ul(cls := "dropdown-menu", backgroundColor := "#eb6864")(
                      li(a(href := routes.ContactController.index().url)(h5(color.white)(Messages("contacts")))),
                      li(a(href := routes.SettingsController.index().url)(h5(color.white)(Messages("settings")))),
                      li(a(href := routes.AuthController.signOut().url)(h5(color.white)(Messages("sign-out"))))
                    )
                  ))
                }
              )
            )
          )
        )
        ),
        bodyContent
      )
    ).render)
  }
}

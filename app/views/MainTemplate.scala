package views

import models.Languages
import play.api.i18n.{Lang, Messages}
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import views.html.helper.CSRF

import scala.concurrent.{ExecutionContext, Future}
import scalatags.Text._
import scalatags.Text.all._

object MainTemplate {

  class IfBoolean(value: Boolean) {
    def ?[T](a: => T, b: => T): T = if (value) a else b
  }

  implicit def BoolToIfBool(value: Boolean): IfBoolean = new IfBoolean(value)


  def apply(pageTitle: String, section: String, headContent: Frag, bodyContent: Frag, showSignInButton: Boolean = false)(
    implicit request: RequestHeader, messages: Messages, lang: Lang, ec: ExecutionContext): Future[Html] = Future(
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
      headContent
    ),
    body(paddingTop := 60)(
      tags2.nav(cls := "navbar navbar-inverse navbar-fixed-top", marginBottom := 0)(div(cls := "container")(
        div(cls := "navbar-header")(
          button(`type` := "button", cls := "navbar-toggle collapsed", data.toggle := "collapse",
            data.target := "#navbar", aria.expanded := "false", aria.controls := "navbar")(
            span(cls := "sr-only")(messages("toggle.navigation")),
            span(cls := "icon-bar"),
            span(cls := "icon-bar"),
            span(cls := "icon-bar")
          ),
          a(cls := "navbar-brand", href := "/")("Pusdieno!")
        ),
        div(id := "navbar", cls := "navbar-collapse collapse")(
          ul(cls := "nav navbar-nav")(
            li(cls := (section == "overview").?("active", ""))(a(href := "/overview")(messages("overview"))),
            li(cls := (section == "eateries").?("active", ""))(a(href := "/eateries")(messages("eateries"))),
            li(cls := (section == "friends").?("active", ""))(a(href := "/friends")(messages("friends"))),
            li(cls := (section == "randomizer").?("active", ""))(a(href := "randomizer")(messages("randomizer")))
          ),
          SeqFrag(if (!showSignInButton) Seq() else
            Seq(form(cls := "navbar-form navbar-left", action := "https://forms.google.com/kaukas")(
              button(`type` := "submit", cls := "btn btn-success")(messages("signin"))
            ))
          ),
          ul(cls := "nav navbar-nav navbar-right")(
            li(cls := (section == "contact").?("active", ""))(a(href := "contact")(messages("contact"))),
            li(cls := (section == "about").?("active", ""))(a(href := "about")(messages("about"))),
            li(cls := (section == "help").?("active", ""))(a(href := "help")(messages("help"))),
            li(cls := "dropdown")(
              a(paddingTop := 19, height := 60, href := "#", cls := "dropdown-toggle",
                data.toggle := "dropdown", aria.expanded := false)(
                img(width := 30, src := "/assets/images/flags/" + lang.code + ".png"),
                " " + messages("language"),
                span(cls := "caret")
              ),
              ul(cls := "dropdown-menu", backgroundColor := "#eb6864")(
                form(action := "/changeLanguage", method := "POST", cls := "form-inline")(
                  raw(CSRF.formField.body),
                  SeqFrag(for ((code, language) <- (Languages.supported - lang.code).toSeq) yield li(
                    button(`type` := "submit", name := "languageCode", value := code,
                      paddingTop := 6, paddingRight := 20, paddingLeft := 20, border := 0, color := "#FFFFFF")(
                      img(width := 2.em, src := s"/assets/images/flags/$code.png", alt := code),
                      " " + language
                    )
                  ))
                )
              )
            )
          )
        )
      )
      ),
      bodyContent
    )
  ).render)
  )
}

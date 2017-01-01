package views

import controllers.routes
import models.User

import scalatags.Text.all._
import models.User._
import play.api.i18n._
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import views.html.main

object Friends {

  def apply()(implicit messages: Messages, lang: Lang, request: RequestHeader) =
    main(Html("<style type=\"text/css\">\n" +
      ".hover-me:not(:hover) .show-me\n" +
      "{opacity: 0;}\n" +
      "</style>\n" +
      "<script src=\"/assets/javascripts/popup.js\"></script>"))(messages("friends") + " – Pusdieno")("friends")(Html({

      def display(user: User) = {
        tr(`class` := "hover-me")(
          td(
            button(data.trigger := "focus", data.toggle := "popover", data.placement := "top", style := "border:none;",
              data.content := messages("friends.favAction"))(
              if (user.fav)
                img(src := "/assets/images/ic_star_yellow_36px.svg", width := 36, height := 36)
              else
                img(`class` := "show-me", src := "/assets/images/ic_star_border_black_36px.svg", width := 36, height := 36)
            ),
            img(`class` := "img-circle",
              src := "/assets/images/" + user.image.getOrElse("ic_account_circle_black_36px.svg"),
              width := 50, height := 50, style := {
                if (user.location.length > 1) "opacity:0.4;" else ""
              })
          ),
          td(
            img(style := {
              if (!user.hungry) "opacity:0;" else ""
            }, src := "/assets/images/ic_restaurant_black_36px.svg",
              width := 36, height := 36),
            img(style := {
              if (!user.cafe) "opacity:0;" else ""
            }, src := "/assets/images/ic_local_cafe_black_36px.svg",
              width := 36, height := 36)
          ),
          td(user.name, br, user.phone),
          td(hr(width := 100, float.left)),
          user.schedule.map(time => td(messages("friends.time"), br, time)).getOrElse(td())
        )
      }

      div(`class` := "content", paddingTop := 10)(
        ul(`class` := "nav nav-tabs")(
          li(`class` := "active")(a(href := "#favorites", data.toggle := "tab", aria.expanded := "true")(messages("friends.favorite"))),
          li(a(href := "#hungry", data.toggle := "tab", aria.expanded := "false")(messages("friends.hungry"))),
          li(a(href := "#cafe", data.toggle := "tab", aria.expanded := "false")(messages("friends.cafe"))),
          li(`class` := "disabled")(a(href := "#", data.toggle := "tab", aria.expanded := "false")(messages("friends.custom") + "#1")),
          li(`class` := "disabled")(a(href := "#", data.toggle := "tab", aria.expanded := "false")(messages("friends.custom") + "#2")),
          li(`class` := "disabled")(a(href := "#", data.toggle := "tab", aria.expanded := "false")(messages("friends.custom") + "#3")),
          li(`class` := "dropdown")(
            a(`class` := "dropdown-toggle", data.toggle := "dropdown", href := "#", aria.expanded := "false")(
              messages("friends.other"),
              span(`class` := "caret")
            ),
            ul(`class` := "dropdown-menu")(
              li(`class` := "disabled")(a(href := "#", data.toggle := "tab")(messages("friends.custom") + "#4")),
              li(`class` := "disabled")(a(href := "#", data.toggle := "tab")(messages("friends.custom") + "#5")),
              li(`class` := "disabled")(a(href := "#", data.toggle := "tab")(messages("friends.custom") + "#6")),
              li(`class` := "divider"),
              li(`class` := "disabled")(a(href := "#all", data.toggle := "tab")(messages("friends.all")))
            )
          )
        ),
        div(id := "myTabContent", `class` := "tab-content")(
          div(`class` := "tab-pane active in", id := "favorites")(
            table(`class` := "table table-striped table-hover")(
              users.filter(_.fav).map(display)
            )
          ),
          div(`class` := "tab-pane", id := "hungry")(
            table(`class` := "table table-striped table-hover")(
              users.filter(_.hungry).map(display)
            )
          ),
          div(`class` := "tab-pane", id := "cafe")(
            table(`class` := "table table-striped table-hover")(
              users.filter(_.cafe).map(display)
            )

          ),
          div(`class` := "tab-pane", id := "all")(
            table(`class` := "table table-striped table-hover")(
              users.map(display)
            )
          )
        )
      )
    }.toString))(messages, lang, request)
}
package views

import javax.inject.Inject

import controllers.routes
import models.{User, Users, WeekPlan}

import scalatags.Text.all._
import models.User._
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n._
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import views.html.main

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

class Friends @Inject()(users: Users) {

  def index()(implicit messages: Messages, lang: Lang, request: RequestHeader, ec: ExecutionContext): Future[_root_.play.twirl.api.Html] = Future(
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
              if (true)
                img(src := "/assets/images/ic_star_yellow_36px.svg", width := 36, height := 36)
              else
                img(`class` := "show-me", src := "/assets/images/ic_star_border_black_36px.svg", width := 36, height := 36)
            ),
            img(`class` := "img-circle",
              src := "/assets/images/" + user.id,
              width := 50, height := 50, style := {
                if (false) "opacity:0.4;" else ""
              })
          ),
          td(
            img(style := {
              if (false) "opacity:0;" else ""
            }, src := "/assets/images/ic_restaurant_black_36px.svg",
              width := 36, height := 36),
            img(style := {
              if (false) "opacity:0;" else ""
            }, src := "/assets/images/ic_local_cafe_black_36px.svg",
              width := 36, height := 36)
          ),
          td(user.name, br, user.phoneNumber),
          td(hr(width := 100, float.left))
        )
      }

      /*
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
              */
      div(id := "myTabContent", `class` := "tab-content")(
        div(`class` := "tab-pane active in", id := "favorites")(
          table(`class` := "table table-striped table-hover") {
            //Await.result(Users().add(User(name="Raitis", phoneNumber = None, eatsAt = WeekPlan(None, None, None, None, None, None, None))), Duration.Inf)
            Await.result(users.getAll(), 5 seconds).map(display)
          }
        ))
      /*,
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

            */

    }.toString))(messages, lang, request))
}
package views

import javax.inject.Inject

import controllers.routes
import models.WeekPlan

import scalatags.Text.all._
import models.db._
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.{Lang, Messages}
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import slick.driver.PostgresDriver.api._
import views.styles.FriendsStyleSheet

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scalacss.ScalatagsCss._
import scalacss.DevDefaults._
import scalatags.Text.TypedTag

class FriendsView @Inject()(contacts: Contacts, dbConfigProvider: DatabaseConfigProvider) {

  // TODO: Separate tabs into individual web pages in order to support more users with faster load times

  private val db = dbConfigProvider.get[JdbcProfile].db

  private val eateryChoices = TableQuery[EateryChoiceTable]

  private val cafeChoices = TableQuery[CafeChoiceTable]

  def index(user: User)(implicit messages: Messages, lang: Lang, request: RequestHeader, ec: ExecutionContext): Future[Html] = {
    val theHead = SeqFrag(Seq(
      script(src := "/assets/javascripts/popup.js"),
      FriendsStyleSheet.render[scalatags.Text.TypedTag[String]]
    ))

    def display(friend: User, fav: Boolean, eat: Boolean, coffee: Boolean): TypedTag[String] = {
      tr(cls := "hover-me")(
        td(
          button(data.trigger := "focus", data.toggle := "popover", data.placement := "top", style := "border:none;",
            data.content := messages("friends.favAction"))(
            if (fav)
              img(src := "/assets/images/icons/ic_star_yellow_36px.svg", width := 36, height := 36)
            else
              img(cls := "show-me", src := "/assets/images/icons/ic_star_border_black_36px.svg", width := 36, height := 36)
          ),
          img(cls := "img-circle",
            src := "/assets/images/" + friend.id, width := 50, height := 50, style := {
              if (false) "opacity:0.4;" else ""
            }, onerror := "javascript:this.src='assets/images/icons/ic_account_circle_black_36px.svg'")
        ),
        td(
          img(style := {
            if (!eat) "opacity:0;" else ""
          }, src := "/assets/images/icons/ic_restaurant_black_36px.svg",
            width := 36, height := 36),
          img(style := {
            if (!coffee) "opacity:0;" else ""
          }, src := "/assets/images/icons/ic_local_cafe_black_36px.svg",
            width := 36, height := 36)
        ),
        td(friend.name, br, friend.mobile),
        td(hr(width := 100, float.left))
      )
    }

    val theBody = div(cls := "content", paddingTop := 10)(
      ul(cls := "nav nav-tabs")(
        li(cls := "active")(a(href := "#favorites", data.toggle := "tab", aria.expanded := "true")(messages("friends.favorites"))),
        li(a(href := "#hungry", data.toggle := "tab", aria.expanded := "false")(messages("friends.hungry"))),
        li(a(href := "#cafe", data.toggle := "tab", aria.expanded := "false")(messages("friends.cafe"))),
        li(cls := "disabled")(a(href := "#", data.toggle := "tab", aria.expanded := "false")(messages("friends.custom") + "#1")),
        li(cls := "disabled")(a(href := "#", data.toggle := "tab", aria.expanded := "false")(messages("friends.custom") + "#2")),
        li(cls := "disabled")(a(href := "#", data.toggle := "tab", aria.expanded := "false")(messages("friends.custom") + "#3")),
        li(cls := "dropdown")(
          a(cls := "dropdown-toggle", data.toggle := "dropdown", href := "#", aria.expanded := "false")(
            messages("friends.other"),
            scalatags.Text.all.span(cls := "caret")
          ),
          ul(cls := "dropdown-menu")(
            li(cls := "disabled")(a(href := "#", data.toggle := "tab")(messages("friends.custom") + "#4")),
            li(cls := "disabled")(a(href := "#", data.toggle := "tab")(messages("friends.custom") + "#5")),
            li(cls := "disabled")(a(href := "#", data.toggle := "tab")(messages("friends.custom") + "#6")),
            li(cls := "divider"),
            li(a(href := "#all", data.toggle := "tab")(messages("friends.all")))
          )
        )
      ),
      div(id := "myTabContent", cls := "tab-content") {

        val friends: Seq[(User, Boolean, Boolean, Boolean)] = Await.result(db.run(
          (for {
            (c: ContactTable, f: UserTable) <- contacts.friendsOfUserAction(user.id)
          } yield (f, c.favorite, eateryChoices.filter(_.user === f.id).exists, cafeChoices.filter(_.user === f.id).exists)).result
        ), 5 seconds)

        SeqFrag(Seq(
          div(cls := "tab-pane active in", id := "favorites")(
            table(cls := "table table-striped table-hover")(
              friends.filter { case (f, fav, e, c) => fav }.map { case (f, fav, e, c) => display(f, fav, e, c) }
            )
          ),
          div(cls := "tab-pane", id := "hungry")(
            table(cls := "table table-striped table-hover")(
              friends.filter { case (f, fav, e, c) => e }.map { case (f, fav, e, c) => display(f, fav, e, c) }
            )
          ),
          div(cls := "tab-pane", id := "cafe")(
            table(cls := "table table-striped table-hover")(
              friends.filter({ case (f, fav, e, c) => c }).map { case (f, fav, e, c) => display(f, fav, e, c) }
            )
          ),
          div(cls := "tab-pane", id := "all")(
            table(cls := "table table-striped table-hover")(
              friends.map { case (f, fav, e, c) => display(f, fav, e, c) }
            )
          )
        ))
      }
    )

    MainTemplate(messages("friends"), "friends", theHead, theBody)
  }
}
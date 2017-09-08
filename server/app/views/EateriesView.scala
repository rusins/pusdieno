package views

import javax.inject.Inject

import controllers.routes
import models.{Cafe, Restaurant, User}
import play.api.i18n.{Messages, MessagesProvider}
import play.twirl.api.{Html, HtmlFormat}
import services.daos._
import services.{ChoiceService, ContactService, RestaurantService}
import views.styles.EateriesStyleSheet

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scalacss.DevDefaults._
import scalacss.ScalatagsCss._
import scalatags.Text
import scalatags.Text.TypedTag
import scalatags.Text.all._

class EateriesView @Inject()(choices: ChoiceService, eateries: RestaurantService, cafes: CafeDAO, contacts: ContactService) {

  def index(section: String, userO: Option[User])
           (implicit messagesProvider: MessagesProvider, ex: ExecutionContext): Future[Html] = {
    val headers = Seq(
      script(src := "/assets/javascripts/jquery.form.js"),
      script(src := "/assets/javascripts/eateries.js"),
      script(src := "/assets/javascripts/list.min.js"),
      script(src := "/assets/javascripts/list.fuzzysearch.min.js"),
      script(src := "/assets/javascripts/popup.js"),
      EateriesStyleSheet.render[scalatags.Text.TypedTag[String]])

    def body: Frag = div(cls := "container", paddingTop := 10)(
      div(id := "eatery-list")(
        div(cls := "panel  panel-default panel-body", paddingTop := 0)(
          ol(cls := "nav nav-pills" /*,style := "display: table; margin-left: auto; margin-right: auto;"*/)(
            li(marginTop := 15, cls := {
              if (section == "eateries") "active" else ""
            })(
              a(href := routes.EateriesController.eaterySelection().url)(Messages("eateries"))
            ),
            li(marginTop := 15, cls := {
              if (section == "cafes") "active" else ""
            })(
              a(href := routes.EateriesController.cafeSelection().url)(Messages("cafes"))
            ),
            li(marginTop := 15, float.right)(
              input(cls := "fuzzy-search form-control", `type` := "text", placeholder := Messages("search"))
            )
          )
        ),
        ol(cls := "list", style := "list-style-type: none;", paddingLeft := 0)(
          if (section == "eateries") {
            val friendChoices = userO match {
              case None => Map[String, Seq[User]]()
              case Some(user) => Await.result(choices.friendEateryChoiceMap(user.id), 5 seconds)
            }

            val userChoices: Set[String] = userO match {
              case None => Set()
              case Some(user) => Await.result(choices.getChoices(user.id), 5 seconds).toSet
            }

            Await.result(eateries.retrieveAll(), 5 seconds).groupBy(_.chainID).toSeq.
              sortBy(chain => Messages("eateries." + chain._1)).map(chain =>
              displayEatery(chain, friendChoices.getOrElse(chain._1, Seq()), if (userChoices(chain._1)) {
                if (userChoices.size == 1) "yes" else "maybe"} else "no"))
          }
          else
            Await.result(cafes.retrieveAll(), 5 seconds).groupBy(_.chainID).toSeq.
              sortBy(chain => Messages("cafes." + chain._1)).map(displayCafe)
        )
      ),
      script(raw(
        """
          |var options = {
          |  valueNames: [ 'name', 'address' ],
          |  plugins: [ ListFuzzySearch() ]
          |};
          |
          |var eateryList = new List('eatery-list', options);
        """.stripMargin))
    )

    Future(MainTemplate(Messages("eateries"), "eateries", headers, body, userO))
  }

  implicit def StringToHtml(s: String): Html = Html(s)

  implicit def HtmlToString(h: HtmlFormat.Appendable): String = h.body

  implicit def HtmlToText(h: HtmlFormat.Appendable): Text.Modifier = h.body

  implicit def TypeTagToString(t: TypedTag[String]): String = t.render

  // TODO: click on eatery to show information

  def displayEatery(chain: (String, Seq[Restaurant]), friends: Seq[User], going: String)
                   (implicit messagesProvider: MessagesProvider): Frag = {

    val (chainID, eateries) = chain

    div(cls := "jumbotron eatery flip" + {if (going == "yes") " going" else ""}, id := chainID, onclick :=
      """
        |$("#" + this.id + " .hidden-panel").slideToggle("fast");
      """.stripMargin,
      style :=
        s"background-image: linear-gradient(to right, #333333, transparent, transparent), url(/assets/images/eateries/$chainID.jpg);" +
          "box-shadow: 0 0 10px gray; background-size: cover; background-position: center;")(
      div(cls := "row")(
        div(cls := "col-sm-12 col-md-6 vcenter row")(
          h2(cls := "name col-xs-12 col-sm-6 col-md-12 col-lg-6", style := "margin-top: 0px; color: white;" +
            "text-shadow: 0 0 5px black; float:left;")(
            Messages("eateries." + chainID)
          ),
          div(cls := "col-xs-12 col-sm-6 col-md-12 col-lg-6")(
            friends.map(friend =>
              img(cls := "img-circle phone-popover",
                src := friend.avatarURL.getOrElse("/assets/images/icons/ic_account_circle_black_36px.svg"),
                width := 50, height := 50,
                data.toggle := "tooltip", data.placement := "top", title := friend.name,
                data("phone-number") := friend.phone.map(_.toString).getOrElse(""),
                data("phone") := friend.phone.map(_.toString).getOrElse(Messages("error.phone")),
                onclick := "event.stopPropagation();",
                style := "margin-left: 2px; margin-right: 2px; margin-bottom: 2px; margin-top: 2px;")
            )
          )
        ),
        div(cls := "col-sm-12 col-md-6 vcenter")(
          form(action := routes.EateriesController.eat().url, method := "post", cls := "eatery-form",
            marginBottom := 0, onclick := "event.stopPropagation();")(
            input(`type` := "hidden", name := "eatery", value := chainID), {
              val (green, blue, red) = going match {
                case "yes" => ("active", "inactive", "inactive")
                case "maybe" => ("inactive", "active", "inactive")
                case "no" => ("inactive", "inactive", "active")
                case _ => ("inactive", "inactive", "inactive")
              }
              div(id := chainID, cls := "btn-group btn-group-justified")(
                div(cls := "form-group btn-group")(
                  button(`type` := "submit", cls := "btn btn-success yes " + green, name := "status", value := "yes")(
                    Messages("eateries.going")
                  )
                ),
                div(cls := "form-group btn-group")(
                  button(`type` := "submit", cls := "btn btn-info maybe " + blue, name := "status", value := "maybe")(
                    Messages("eateries.undecided")
                  )
                ),
                div(cls := "form-group btn-group")(
                  button(`type` := "submit", cls := "btn btn-primary no " + red, name := "status", value := "no")(
                    Messages("eateries.not-going")
                  )
                )
              )
            }
          )
        )
      )
      ,
      div(cls := "hidden-panel")(
        h3(color.white)(chain._2.headOption.map(Messages("eateries.address") + ": " + _.address))
      )
    )
  }

  def displayCafe(chain: (String, Seq[Cafe])): Frag = {
    div()
  }

}

// TODO: clickable phone number
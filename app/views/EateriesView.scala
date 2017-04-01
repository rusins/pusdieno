package views

import javax.inject.Inject

import controllers.routes
import models.{Cafe, Eatery, User}
import play.api.i18n.{Lang, Messages}
import play.api.mvc.RequestHeader
import play.twirl.api.{Html, HtmlFormat}
import services.daos.{Cafes, Choices, Contacts, Eateries}
import views.html.b3._
import views.html.b3.inline.fieldConstructor
import views.styles.{CommonStyleSheet, EateriesStyleSheet}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scalacss.DevDefaults._
import scalacss.ScalatagsCss._
import scalatags.Text
import scalatags.Text.TypedTag
import scalatags.Text.all._

class EateriesView @Inject()(choices: Choices, eateries: Eateries, cafes: Cafes, contacts: Contacts) {

  def index(section: String, userO: Option[User])(implicit messages: Messages, lang: Lang,
                                                  request: RequestHeader, ec: ExecutionContext): Future[Html] = {
    val headers = Seq(
      script(src := "/assets/javascripts/jquery.form.js"),
      script(src := "/assets/javascripts/eateries.js"),
      script(src := "/assets/javascripts/list.min.js"),
      script(src := "/assets/javascripts/list.fuzzysearch.min.js"),
      script(src := "/assets/javascripts/popup.js"),
      EateriesStyleSheet.render[scalatags.Text.TypedTag[String]],
      CommonStyleSheet.render[scalatags.Text.TypedTag[String]])

    val body = div(cls := "container", style := "padding-top: 10px;")(
      div(id := "eatery-list")(
        div(cls := "panel  panel-default panel-body", style := "padding-top: 0px;")(
          ol(cls := "nav nav-pills" /*,style := "display: table; margin-left: auto; margin-right: auto;"*/)(
            li(style := "margin-top: 15px;", cls := {
              if (section == "eateries") "active" else ""
            })(
              a(href := "/eateries")(messages("eateries"))
            ),
            li(style := "margin-top: 15px;", cls := {
              if (section == "cafes") "active" else ""
            })(
              a(href := "/cafes")(
                messages("cafes")
              )
            ),
            li(style := "margin-top: 15px; float: right;")(
              input(cls := "fuzzy-search form-control", `type` := "text", placeholder := messages("search"))
            )
          )
        ),
        ol(cls := "list", style := "list-style-type: none; padding-left: 0px;")(
          if (section == "eateries") {
            val friendChoices = userO match {
              case Some(user) => Await.result(choices.friendEateryChoiceMap(user), 5 seconds)
              case None => Map[String, Seq[User]]()
            }

            Await.result(eateries.retrieveAll(), 5 seconds).groupBy(_.chainID).toSeq.
              sortBy(chain => messages("eateries." + chain._1)).map(chain =>
              displayEatery(chain, friendChoices.getOrElse(chain._1, Seq())))
          }
          else
            Await.result(cafes.retrieveAll(), 5 seconds).groupBy(_.chainID).toSeq.
              sortBy(chain => messages("cafes." + chain._1)).map(displayCafe)
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

    MainTemplate(messages("eateries"), "eateries", headers, body, userO)
  }

  implicit def StringToHtml(s: String): Html = Html(s)

  implicit def HtmlToString(h: HtmlFormat.Appendable): String = h.body

  implicit def HtmlToText(h: HtmlFormat.Appendable): Text.Modifier = h.body

  implicit def TypeTagToString(t: TypedTag[String]): String = t.render

  // TODO: click on eatery to show information

  def displayEatery(chain: (String, Seq[Eatery]), friends: Seq[User])(implicit messages: Messages, request: RequestHeader): Frag = {
    val (chainID, eateries) = chain
    div(cls := "jumbotron eatery flip", id := chainID, onclick :=
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
            messages("eateries." + chainID)
          ),
          div(cls := "col-xs-12 col-sm-6 col-md-12 col-lg-6")(
            friends.map(user =>
              img(cls := "img-circle phone-popover", src := "/assets/images/" + user.id, width := 50, height := 50,
                onerror := "javascript:this.src='assets/images/icons/ic_account_circle_black_36px.svg'",
                data.toggle := "tooltip", data.placement := "top", title := user.name,
                data("phone-number") := user.mobile.map(_.toString).getOrElse(""),
                data("phone") := user.mobile.map(_.toString).getOrElse(messages("error.phone")),
                onclick := "event.stopPropagation();",
                style := "margin-left: 2px; margin-right: 2px; margin-bottom: 2px; margin-top: 2px;")
            )
          )
        ),
        div(cls := "col-sm-12 col-md-6 vcenter")(raw(
          formCSRF(routes.EateriesController.eat(), 'class -> "eatery-form", 'style -> "margin-bottom: 0px;",
            'onclick -> "event.stopPropagation();")(
            views.html.b3.hidden("eatery", chainID) + Html(
              div(id := chainID, cls := "btn-group btn-group-justified")(raw(
                submit('_class -> "btn-group", 'name -> "status", 'value -> "yes", 'class -> "btn btn-success yes inactive")(
                  messages("eateries.going")
                ) +
                  submit('_class -> "btn-group", 'name -> "status", 'value -> "maybe", 'class -> "btn btn-info maybe inactive")(
                    messages("eateries.undecided")
                  ) +
                  submit('_class -> "btn-group no", 'name -> "status", 'value -> "no", 'class -> "btn btn-primary no active")(
                    messages("eateries.notGoing")
                  )
              ))
            )
          )
        ))
      ),
      div(cls := "hidden-panel")(
        h1("Wassup, dudes?")
      )
    )
  }

  def displayCafe(chain: (String, Seq[Cafe])): Frag = {
    div()
  }

}

// TODO: clickable phone number
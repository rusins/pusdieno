package views

import scalacss.ScalatagsCss._
import javax.inject.Inject

import controllers.routes

import scalatags.Text.all._
import models.db.{Eateries, Eatery}
import org.w3c.dom.html.HTMLStyleElement
import play.api.i18n.{Lang, Messages}
import play.api.mvc.RequestHeader
import play.twirl.api.{Html, HtmlFormat}
import views.html.main
import views.html.b3.inline.fieldConstructor
import views.html.b3._
import views.styles.eateries.EateriesStyleSheet

import scala.concurrent.{Await, ExecutionContext, Future}
import scalatags.Text
import scalatags.Text.TypedTag
import scala.concurrent.duration._
import scalacss.ScalatagsCss._
import scalacss.DevDefaults._

class EateriesView @Inject()(eateries: Eateries) {

  implicit def StringToHtml(s: String): Html = Html(s)

  implicit def HtmlToString(h: HtmlFormat.Appendable): String = h.body

  implicit def HtmlToText(h: HtmlFormat.Appendable): Text.Modifier = h.body

  implicit def TypeTagToString(t: TypedTag[String]): String = t.render

  def display(eatery: Eatery)(implicit messages: Messages, request: RequestHeader): TypedTag[String] =
    div(`class` := "jumbotron eatery", style :=
      s"background-image: linear-gradient(to right, #333333, transparent, transparent), url(/assets/images/eateries/${eatery.chainID}.jpg);" +
      //"background-image: url(https://mdn.mozillademos.org/files/11305/firefox.png), linear-gradient(to right, rgba(30, 75, 115, 1), rgba(255, 255, 255, 0));" +
      "box-shadow: 0 0 10px gray; background-size: cover; background-position: center;")(
      div(`class` := "row", style := "padding-top: 10.5px;")(
        div(`class` := "col-md-6")(
          div(
            h2(`class` := "col-xs-12 col-sm-6 col-md-12 col-lg-6", style := "margin-top: 0px; color: white;" +
              "text-shadow: 0 0 5px black; float:left;")(
              messages("eateries." + eatery.chainID)
            ),
            div(`class` := "col-xs-12 col-sm-6 col-md-12 col-lg-6")(
              "People going there"
            )
          )
        ),
        div(`class` := "col-md-6")(raw(
          formCSRF(routes.EateriesController.eat(), 'class -> "eatery-form")(
            views.html.b3.hidden("eatery", eatery.chainID) + Html(
              div(id := eatery.chainID, `class` := "btn-group btn-group-justified")(raw(
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
      )
    )

  def eaterySelection()(implicit messages: Messages, lang: Lang,
                        request: RequestHeader, ec: ExecutionContext): Future[Html] = Future(
    main(
      Html(
        SeqFrag(Seq(
          script(src := "http://malsup.github.com/jquery.form.js"),
          script(src := "/assets/javascripts/eateries.js"),
          EateriesStyleSheet.render[scalatags.Text.TypedTag[String]]
        )).render
      )
    )(messages(messages("eateries")))("eateries")(Html(
      div(`class` := "container", style := "padding-top: 10px;")(
        div(`class` := "row")(
          ul(`class` := "nav nav-pills")(
            li(`class` := "active col-xs-6")(
              messages("eateries")
            ),
            li(`class` := "col-xs-6")(
              a(href := "/cafes")
            )
          )
        ),
        Await.result(eateries.retrieveAll(), 5 seconds).map(display)
      ).render
    ))(messages, lang, request)

  )

  def cafeSelection()(implicit messages: Messages, lang: Lang, request: RequestHeader, ec: ExecutionContext) = Future(
    main(
      Html(
        SeqFrag(Seq(
          script(src := "http://malsup.github.com/jquery.form.js"),
          script(src := "/assets/javascripts/eateries.js"),
          EateriesStyleSheet.render[scalatags.Text.TypedTag[String]]
        )).render
      )
    )(messages("cafes"))("eateries")(Html(
      ""
    ))

  )

  def test()(implicit ec: ExecutionContext) = Future(
    Html(div(raw("<h1>Ayy, lmao!</h1>")))
  )
}
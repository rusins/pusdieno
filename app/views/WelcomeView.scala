package views

import play.api.i18n.{Lang, Messages}
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import scalatags.Text.all._

import scala.concurrent.{ExecutionContext, Future}

object WelcomeView {

  def index()(implicit messages: Messages, lang: Lang,
              request: RequestHeader, ec: ExecutionContext): Future[Html] = {
    val theHead: Frag = UnitFrag(Unit)
    val theBody: Frag = SeqFrag(Seq(
      div(width := 100.pct, height := 100.pct, paddingTop := 100,
        backgroundImage := "url(\"/assets/images/cover_lowsize.jpg\")", backgroundSize := "cover")(
        div(cls := "container")(
          div(cls := "jumbotron", color := "#FFFFFF", backgroundColor := "rgba(0, 0, 0, 0.7)")(
            h1(color := "#FFFFFF")("Pusdieno!"),
            p(messages("welcome.text") + messages("welcome.extra")),
            p(a(cls := "btn btn-success btn-lg", href := "https://forms.google.com/kaukas")(messages("welcome.form")))
          )
        )
      )
    ))
    MainTemplate("Pusdieno", "welcome", theHead, theBody)
  }

}

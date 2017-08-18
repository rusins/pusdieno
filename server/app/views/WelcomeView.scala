package views

import controllers.routes
import play.api.i18n.{Lang, Messages, MessagesProvider}
import play.api.mvc.RequestHeader
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scalatags.Text.all._

object WelcomeView {

  def index()(implicit messagesProvider: MessagesProvider): Html = {
    val headers: Frag = meta(name := "google-site-verification", content := "l_AKKzyNYlbZcqLqbjlcZp40qf41mQkKxRJeUz9ph48")

    val body: Frag = SeqFrag(Seq(
      div(width := 100.pct, height := 100.pct, paddingTop := 100,
        backgroundImage := "url(\"/assets/images/cover_lowsize.jpg\")", backgroundSize := "cover")(
        div(cls := "container")(
          div(cls := "jumbotron", color := "#FFFFFF", backgroundColor := "rgba(0, 0, 0, 0.7)")(
            h1(color := "#FFFFFF")("Pusdieno!"),
            p(Messages("welcome.text")),
            p(a(cls := "btn btn-success btn-lg", href := routes.AuthController.signIn().url)(Messages("sign-in")))
          )
        )
      )
    ))

    MainTemplate("Pusdieno", "welcome", headers, body, None)
  }

}

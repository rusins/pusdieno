package views

import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import controllers.routes
import play.api.i18n.{Lang, Messages, MessagesProvider}
import play.api.mvc.RequestHeader
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scalacss.DevDefaults._
import scalacss.ScalatagsCss._
import scalatags.Text
import scalatags.Text.TypedTag
import scalatags.Text.all._

object SignInView {

  def apply(socialProviderRegistry: SocialProviderRegistry, error: Option[String])
           (implicit messagesProvider: MessagesProvider): Html = {

    val headers: Seq[TypedTag[String]] = Seq()

    val errorSpot: Frag = error match {
      case Some(errorMessage) => div(`class` := "alert alert-danger center-block", maxWidth := 480)(
        errorMessage
      )
      case None => UnitFrag(Unit)
    }

    val body: Frag = div(`class` := "container", paddingTop := 100)(
      div(`class` := "panel panel-default center-block", maxWidth := 480)(
        div(`class` := "panel-heading")(Messages("sign-in")),
        div(`class` := "panel-body")(
          a(href := routes.AuthController.authenticate("google").url)(
            img(src := "/assets/images/google_sign_in.png", `class` := "img-responsive center-block")
          ),
          div(`class` := "text-center")(Messages("or")),
          a(href := routes.AuthController.authenticate("facebook").url)(
            img(src := "/assets/images/facebook_sign_in.png", `class` := "img-responsive center-block")
          )
        )
      ),
      errorSpot
    )

    MainTemplate(Messages("sign-in"), "sign-in", headers, body, None)
  }
}
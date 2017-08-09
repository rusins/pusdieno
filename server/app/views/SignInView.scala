package views

import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import controllers.routes
import play.api.i18n.{Lang, Messages}
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import views.styles.SignInStyleSheet

import scala.concurrent.{ExecutionContext, Future}
import scalacss.DevDefaults._
import scalacss.ScalatagsCss._
import scalatags.Text
import scalatags.Text.all._

object SignInView {

  def apply(socialProviderRegistry: SocialProviderRegistry)
           (implicit request: RequestHeader, messages: Messages, lang: Lang): Html = {

    val headers = Seq(
      SignInStyleSheet.render[scalatags.Text.TypedTag[String]]
    )

    val errorSpot: Frag = request.flash.get("error") match {
      case Some(errorMessage) => div(`class` := "alert alert-danger center-block", maxWidth := 480)(
        errorMessage
      )
      case None => UnitFrag(Unit)
    }

    val body: Frag = div(`class` := "container", paddingTop := 100)(
      div(`class` := "panel panel-default center-block", maxWidth := 480)(
        div(`class` := "panel-heading")(messages("sign-in")),
        div(`class` := "panel-body")(
          a(href := routes.AuthController.authenticate("google").url)(
            img(src := "/assets/images/google_sign_in.png", `class` := "img-responsive center-block")
          ),
          div(`class` := "text-center")(messages("or")),
          a(href := routes.AuthController.authenticate("facebook").url)(
            img(src := "/assets/images/facebook_sign_in.png", `class` := "img-responsive center-block")
          )
        )
      ),
      errorSpot
    )

    MainTemplate(messages("sign-in"), "sign-in", headers, body, None)
  }
}
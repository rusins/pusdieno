package views

import play.api.i18n.{Lang, Messages}
import play.api.mvc.RequestHeader
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scalatags.Text.all._

object ErrorView {

  def apply(errorTitle: String, errorMessage: String)(implicit request: RequestHeader,
                                                      messages: Messages,
                                                      lang: Lang,
                                                      ec: ExecutionContext): Future[Html] = {

    val headers = UnitFrag(Unit)

    val body = div(`class` := "content", paddingTop := 100)(
      div(`class` := "alert alert-danger")(
        strong(errorTitle),
        br,
        textarea(errorMessage)
      )
    )

    MainTemplate.apply("Error", "error", headers, body)
  }

  def unimplemented(optionalMessage: Option[String] = None)(implicit request: RequestHeader,
                                                            messages: Messages,
                                                            lang: Lang,
                                                            ec: ExecutionContext): Future[Html] = {
    val headers = UnitFrag(Unit)

    val body = div(`class` := "content", paddingTop := 100)(
      strong(paddingTop := 100)(messages("error.unimplemented")),
      optionalMessage match {
        case Some(message) => message
        case None => UnitFrag(Unit)
      }
    )

    MainTemplate.apply("Page not found", "error", headers, body)
  }
}

package views

import play.api.i18n.{Lang, Messages}
import play.api.mvc.RequestHeader
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scalatags.Text.all._

object ErrorView {

  def apply(errorTitle: String, errorMessage: String, image: String)(implicit request: RequestHeader,
                                                      messages: Messages,
                                                      lang: Lang,
                                                      ec: ExecutionContext): Future[Html] = {

    val headers = UnitFrag(Unit)

    val body = div(`class` := "content", paddingTop := 100)(
      div(`class` := "row")(
        div(`class` := "col-md-6")(
          div(`class` := "alert alert-danger center-block")(
            strong(errorTitle),
            br,
            textarea(errorMessage)
          )
        ),
        div(`class` := "col-md-6")(
          img(src := s"/assets/images/$image.png", `class` := "img-responsive center-block")
        )
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
      div(`class` := "row")(
        div(`class` := "col-md-6")(
          strong(paddingTop := 100)(messages("error.unimplemented")),
          optionalMessage match {
            case Some(message) => message
            case None => UnitFrag(Unit)
          }
        ),
        div(`class` := "col-md-6")(
          img(src := "/assets/images/server_error.png", `class` := "img-responsive center-block")
        )
      )
    )

    MainTemplate.apply("Page not found", "error", headers, body)
  }
}

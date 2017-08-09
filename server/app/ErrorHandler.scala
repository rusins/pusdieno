import javax.inject.{Inject, Provider, Singleton}

import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import play.api.routing.Router
import play.api.{Configuration, Environment, OptionalSourceMapper, UsefulException}
import views.ErrorView

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

@Singleton
class ErrorHandler @Inject()(env: Environment,
                             config: Configuration,
                             sourceMapper: OptionalSourceMapper,
                             router: Provider[Router],
                             val messagesApi: MessagesApi
                            ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) with I18nSupport {

  override def onProdServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
    implicit val requestHeader = request
    Future.successful(InternalServerError(ErrorView(Messages("error.server"), exception.getMessage, "server_error")))
  }

  override def onForbidden(request: RequestHeader, message: String): Future[Result] = {
    implicit val requestHeader = request
    Future.successful(Forbidden(ErrorView(Messages("error.forbidden_access"), message, "unauthorized_error")))
  }
}

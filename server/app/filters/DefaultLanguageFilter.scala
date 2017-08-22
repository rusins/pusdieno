package filters

import akka.stream.Materializer
import javax.inject._

import play.api.i18n.{Lang, MessagesApi}
import play.api.mvc._
import utils.LoggingSupport

import scala.concurrent.{ExecutionContext, Future}

/**
  * Checks if the language cookie is set, if not, sets it to Latvian
  * @param ex
  */
@Singleton
class DefaultLanguageFilter @Inject()(messagesApi: MessagesApi)(implicit ex: ExecutionContext) extends EssentialFilter
  with LoggingSupport {

  override def apply(next: EssentialAction) = EssentialAction { request =>
    println("WTF")
    next(request).map { result =>
      if (request.cookies.get(messagesApi.langCookieName).isEmpty) {
        logger.info("Setting request without language cookie to Latvian")
        messagesApi.setLang(result, Lang("lv"))
      }
      else {
        logger.info("Request already had language cookie")
        result
      }
    }
  }
}

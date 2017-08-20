package controllers

import javax.inject.Inject

import models.Languages
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.{Action, InjectedController}
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import utils.LoggingSupport

class LanguageController @Inject()() extends InjectedController with I18nSupport with LoggingSupport {

  def changeLanguage() = Action { implicit request =>
    logger.debug("User requested language change")
    // Get URL of page the language was changed from, default to root
    val referer = request.headers.get(REFERER).getOrElse("/")
    // Define a single field form
    val form = Form("languageCode" -> nonEmptyText)
    // Populate the form with the data from the request
    form.bindFromRequest.fold(
      erroneousForm => {
        logger.error("Invalid language form sent")
        Redirect(referer)
      },
      languageCode => if (Languages.supported.contains(languageCode))
        Redirect(referer).withLang(Lang(languageCode))
      else {
        logger.warn("User requested language change to unsupported language: " + limit(languageCode, 20))
        Redirect(referer).withLang(Lang("lv"))
      }

    )
  }

}

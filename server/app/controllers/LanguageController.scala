package controllers

import javax.inject.Inject

import models.Languages
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import play.api.i18n.{I18nSupport, Lang, MessagesApi}

class LanguageController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def changeLanguage() = Action { implicit request =>
    println("Changing language?")
    // Get URL of page the language was changed from, default to root
    val referer = request.headers.get(REFERER).getOrElse("/")
    // Define a single field form
    val form = Form("languageCode" -> nonEmptyText)
    // Populate the form with the data from the request
    form.bindFromRequest.fold(
      erroneousForm => {println("Invalid language form sent"); Redirect(referer)},
      languageCode => Redirect(referer).withLang(if (Languages.supported.contains(languageCode))
        Lang(languageCode)
      else
        Lang("lv")
      )
    )
  }

}

package controllers

import javax.inject.Inject

import play.api.data._
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import play.api.i18n.{I18nSupport, Lang, MessagesApi}

class LanguageController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def changeLanguage() = Action { implicit request =>
    println("Changing language")
    // Get URL of page the language was changed from, default to root
    val referer = request.headers.get(REFERER).getOrElse("/")
    // Define a single field form
    val form = Form("languageCode" -> nonEmptyText)
    // Populate the form with the data from the request
    form.bindFromRequest.fold(
      erroneousForm => {
        println("Language form had errors :(")
        Redirect(referer)
      },
      languageCode => Redirect(referer).withLang(languageCode match {
        case "lv" => println("Changing language to latvian"); Lang("lv")
        case "en" => println("Changing language to english"); Lang("en")
        case "ru" => println("Changing language to russian"); Lang("ru")
        case _ => println("Unknown language code, changing to default"); Lang(Lang.defaultLang.code)
      })
    )
  }

}

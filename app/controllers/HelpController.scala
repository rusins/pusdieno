package controllers

import play.api.mvc._

class HelpController extends Controller {

  def index = Action{ implicit request =>
    Ok("temp")
  }
}

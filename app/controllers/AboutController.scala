package controllers

import play.api.mvc._

class AboutController extends Controller {

  def index = Action{ implicit request =>
    Ok("temp")
  }
}

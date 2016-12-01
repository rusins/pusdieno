package controllers

import play.api.mvc._

class ContactController extends Controller {

  def index = Action{ implicit request =>
    Ok("temp")
  }
}

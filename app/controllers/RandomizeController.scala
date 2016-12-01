package controllers

import play.api.mvc._

class RandomizeController extends Controller {

  def index = Action{ implicit request =>
    Ok("temp")
  }
}

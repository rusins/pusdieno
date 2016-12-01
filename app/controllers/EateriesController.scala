package controllers

import play.api.mvc._

class EateriesController extends Controller {

  def index = Action{ implicit request =>
    Ok("temp")
  }
}

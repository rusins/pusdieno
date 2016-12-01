package controllers

import play.api.mvc._

class OverviewController extends Controller {

  def index = Action{ implicit request =>
    Ok("temp")
  }
}

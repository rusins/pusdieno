package controllers

import play.api.mvc._

class FriendsController extends Controller {

  def index = Action{ implicit request =>
    Ok(views.html.friends())
  }
}

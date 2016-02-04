package port.adapter.ui.controllers

import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok(port.adapter.ui.views.html.index("Your new application is ready."))
  }

}

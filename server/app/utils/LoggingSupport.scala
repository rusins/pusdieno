package utils

import com.mohiva.play.silhouette.api.Logger

trait LoggingSupport extends Logger {

  def limit(text: String, maxLength: Int): String = {
    if (text.length <= maxLength)
      text
    else
      text.substring(0, maxLength - 3) + "..."
  }

}

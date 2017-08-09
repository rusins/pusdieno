package views.styles

import scalacss.Defaults._

object FriendsStyleSheet extends StyleSheet.Standalone {

  import dsl._

  ".hover-me:not(:hover)" - (
    &(".show-me") -
      opacity(0.4)
    )
}
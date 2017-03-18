package views.styles

import scalacss.Defaults._
import scalacss.internal.Transform

object CommonStyleSheet extends StyleSheet.Standalone {

  import dsl._

  ".vcenter" - (
    display.inlineBlock,
    verticalAlign.middle,
    float.none
  )

   ".center-block" - (
    display.block,
    marginLeft.auto,
    marginRight.auto
   )

}
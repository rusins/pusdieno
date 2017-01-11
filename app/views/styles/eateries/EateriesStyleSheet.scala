package views.styles.eateries

import scalacss.Defaults._

object EateriesStyleSheet extends StyleSheet.Standalone {

  import dsl._

  ".yes.inactive, .maybe.inactive, .no.inactive" - (
    backgroundColor(c"#999999"),
    borderColor(c"#999999")
  )

  ".yes.inactive" - (
    &.hover - (
      backgroundColor(c"#1A873A"),
      borderColor(c"#1A873A")
    )
    )

  ".maybe.inactive" - (
    &.hover - (
      backgroundColor(c"#264C73"),
      borderColor(c"#264C73")
    )
    )

  ".no.inactive: hover" - (
    &.hover - (
      backgroundColor(c"#E53C37"),
      borderColor(c"#E53C37")
    )
    )

  ".jumbotron.eatery" - (
    paddingTop(5.px),
    paddingBottom(5.px)
  )


  "jumbotron eatery" - (
    &.after -
      backgroundColor(rgba(0, 0, 0, 0.5)),

    &("p") - (
      fontSize(12.px),
      color(gray)
    ),

    &("b") -
      color(white)

  )
}
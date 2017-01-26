package views.styles

import scalacss.Defaults._

object EateriesStyleSheet extends StyleSheet.Standalone {

  import dsl._

  /*
    "body" -(
      backgroundColor(c"#669966")
    )
  */

  ".yes.inactive, .maybe.inactive, .no.inactive" - (
    backgroundColor(c"#999999"),
    borderColor(c"#999999")
  )

  ".yes.inactive" - (
    //color(black),
    backgroundColor(c"#909e94"),
    borderColor(c"#909e94"),
    &.hover - (
      backgroundColor(c"#1A873A"),
      borderColor(c"#1A873A")
    )
  )

  ".maybe.inactive" - (
    //color(black),
    backgroundColor(c"#868c92"),
    borderColor(c"#868c92"),
    &.hover - (
      backgroundColor(c"#264C73"),
      borderColor(c"#264C73")
    )
  )

  ".no.inactive" - (
    //color(black),
    backgroundColor(c"#b5a9a9"),
    borderColor(c"#b5a9a9"),
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

  ".vcenter" - (
    display.inlineBlock,
    verticalAlign.middle,
    float.none
  )

  ".panel" - (
    display.none,
    backgroundColor.transparent,
    boxShadow := none
  )
}
name := """pusdieno"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

scalacOptions ++= Seq(
  "-feature",
  "-language:postfixOps",
  "-unchecked",
  "-deprecation",
  "-Yno-adapted-args",
  "-Ywarn-dead-code"
)

routesImport += "play.api.mvc.PathBindable.bindableUUID"

resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"

libraryDependencies ++= Seq(
  filters,
  ws,
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test,
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
  "com.lihaoyi" %% "scalatags" % "0.6.5",
  "com.github.japgolly.scalacss" %% "core" % "0.5.3",
  "com.github.japgolly.scalacss" %% "ext-scalatags" % "0.5.3",
  "com.mohiva" %% "play-silhouette" % "4.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
  "com.mohiva" %% "play-silhouette-testkit" % "4.0.0" % Test,
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "com.iheart" %% "ficus" % "1.4.1" // typesafe importing values from config files
)

// fork in run := true // only forks run command in SBT

// These 2 lines "should" disable including API documentation in the production build´
sources in(Compile, doc) := Seq.empty
publishArtifact in(Compile, packageDoc) := false

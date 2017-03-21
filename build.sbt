name := """pusdieno"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-unchecked",
  "-deprecation",
  "-Yno-adapted-args",
  "-Ywarn-dead-code"
)

resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"

libraryDependencies ++= Seq(
  filters,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3",
  "com.lihaoyi" %% "scalatags" % "0.6.1",
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
  "com.typesafe.play" %% "play-slick" % "2.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.2",
  "com.github.japgolly.scalacss" %% "core" % "0.5.1",
  "com.github.japgolly.scalacss" %% "ext-scalatags" % "0.5.1",
  "com.mohiva" %% "play-silhouette" % "4.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
  "com.mohiva" %% "play-silhouette-testkit" % "4.0.0" % "test",
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "com.iheart" %% "ficus" % "1.4.0"
)

fork in run := true

sources in(Compile, doc) := Seq.empty // These 2 lines disable including API documentation in the production build´
publishArtifact in(Compile, packageDoc) := false

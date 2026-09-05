ThisBuild / organization := "dev.yumuuu"
ThisBuild / scalaVersion := "3.8.4"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Wunused:all",
    "-Wvalue-discard",
    "-Wnonunit-statement"
  )
)

lazy val domain = project
  .in(file("modules/domain"))
  .settings(commonSettings)
  .settings(name := "play-clean-scala-domain")

lazy val application = project
  .in(file("modules/application"))
  .dependsOn(domain)
  .settings(commonSettings)
  .settings(name := "play-clean-scala-application")

lazy val infrastructure = project
  .in(file("modules/infrastructure"))
  .dependsOn(application, domain)
  .settings(commonSettings)
  .settings(name := "play-clean-scala-infrastructure")

lazy val presentation = project
  .in(file("modules/presentation"))
  .dependsOn(application, domain)
  .settings(commonSettings)
  .settings(name := "play-clean-scala-presentation")

lazy val bootstrap = project
  .in(file("modules/bootstrap"))
  .dependsOn(domain, application, infrastructure, presentation)
  .settings(commonSettings)
  .settings(name := "play-clean-scala-bootstrap")

lazy val root = project
  .in(file("."))
  .aggregate(domain, application, infrastructure, presentation, bootstrap)
  .settings(commonSettings)
  .settings(
    name := "play-clean-scala",
    publish / skip := true
  )

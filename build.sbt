ThisBuild / organization := "dev.yumuuu"
ThisBuild / scalaVersion := "3.8.4"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

val catsVersion = "2.13.0"
val catsEffectVersion = "3.7.1"
val circeVersion = "0.14.16"
val doobieVersion = "1.0.0-RC13"
val munitVersion = "1.3.6"
val pekkoVersion = "1.1.5"
val pekkoHttpVersion = "1.4.0"
val pureConfigVersion = "0.17.10"
val weldVersion = "6.0.4.Final"
val jakartaInjectVersion = "2.0.1"

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
  .settings(
    name := "play-clean-scala-domain",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "org.scalameta" %% "munit" % munitVersion % Test
    )
  )

lazy val usecase = project
  .in(file("modules/usecase"))
  .settings(commonSettings)
  .settings(
    name := "play-clean-scala-usecase",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % catsEffectVersion
    )
  )

lazy val application = project
  .in(file("modules/application"))
  .dependsOn(domain, usecase)
  .settings(commonSettings)
  .settings(
    name := "play-clean-scala-application",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "jakarta.inject" % "jakarta.inject-api" % jakartaInjectVersion,
      "org.scalameta" %% "munit" % munitVersion % Test
    )
  )

lazy val infrastructure = project
  .in(file("modules/infrastructure"))
  .dependsOn(domain)
  .settings(commonSettings)
  .settings(
    name := "play-clean-scala-infrastructure",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "org.typelevel" %% "doobie-core" % doobieVersion,
      "org.typelevel" %% "doobie-postgres" % doobieVersion,
      "jakarta.inject" % "jakarta.inject-api" % jakartaInjectVersion
    )
  )

lazy val presentation = project
  .in(file("modules/presentation"))
  .dependsOn(application, domain, usecase)
  .settings(commonSettings)
  .settings(
    name := "play-clean-scala-presentation",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "jakarta.inject" % "jakarta.inject-api" % jakartaInjectVersion,
      "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-stream" % pekkoVersion
    )
  )

lazy val bootstrap = project
  .in(file("modules/bootstrap"))
  .dependsOn(domain, usecase, application, infrastructure, presentation)
  .settings(commonSettings)
  .settings(
    name := "play-clean-scala-bootstrap",
    Compile / run / mainClass := Some("dev.yumuuu.playclean.bootstrap.Main"),
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.5.18",
      "com.github.pureconfig" %% "pureconfig-core" % pureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-generic-scala3" % pureConfigVersion,
      "jakarta.annotation" % "jakarta.annotation-api" % "3.0.0",
      "jakarta.enterprise" % "jakarta.enterprise.cdi-api" % "4.1.0",
      "org.typelevel" %% "doobie-hikari" % doobieVersion,
      "org.jboss.weld.se" % "weld-se-core" % weldVersion
    )
  )

lazy val root = project
  .in(file("."))
  .aggregate(domain, usecase, application, infrastructure, presentation, bootstrap)
  .settings(commonSettings)
  .settings(
    name := "play-clean-scala",
    publish / skip := true
  )

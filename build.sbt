lazy val commonSettings = Seq(
  organization := "com.github.fdietze",
  version      := "master-SNAPSHOT",

  scalaVersion := "2.12.8",
  crossScalaVersions := Seq("2.11.12", "2.12.8"),

  resolvers ++= (
    ("jitpack" at "https://jitpack.io") ::
    Nil
  ),

  scalacOptions ++=
    "-encoding" :: "UTF-8" ::
    "-unchecked" ::
    "-deprecation" ::
    "-explaintypes" ::
    "-feature" ::
    "-language:_" ::
    "-Xfuture" ::
    "-Xlint" ::
    "-Ypartial-unification" ::
    "-Yno-adapted-args" ::
    "-Ywarn-infer-any" ::
    "-Ywarn-value-discard" ::
    "-Ywarn-nullary-override" ::
    "-Ywarn-nullary-unit" ::
    Nil,

  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) =>
        "-Ywarn-extra-implicit" ::
        Nil
      case _ =>
        Nil
    }
  },

  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6"),

  initialCommands in console := """
  import colorado._
  """,
)

enablePlugins(ScalaJSPlugin)

lazy val root = (project in file("."))
  .aggregate(coloradoJS, coloradoJVM)
  .settings(commonSettings)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val colorado = crossProject.crossType(CrossType.Pure)
  .settings(commonSettings)
  .settings(
    name         := "colorado",

    libraryDependencies ++=
      Deps.scalaTest.value % Test ::
      Nil
  )
  .jsSettings(
    scalacOptions += "-P:scalajs:sjsDefinedByDefault",
    scalacOptions ++= git.gitHeadCommit.value.map { headCommit =>
      val local = baseDirectory.value.toURI
      val remote = s"https://raw.githubusercontent.com/fdietze/colorado/${headCommit}/"
      s"-P:scalajs:mapSourceURI:$local->$remote"
    }
  )

lazy val coloradoJS = colorado.js
lazy val coloradoJVM = colorado.jvm

lazy val commonSettings = Seq(
  organization := "com.github.fdietze",
  version := "master-SNAPSHOT",

  scalaVersion := crossScalaVersions.value.last,
  crossScalaVersions := Seq("2.11.12", "2.12.12", "2.13.3"),

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
    "-Xlint" ::
    Nil,

  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) =>
        "-Ywarn-extra-implicit" ::
        "-Ypartial-unification" ::
        "-Yno-adapted-args" ::
        "-Ywarn-infer-any" ::
        "-Ywarn-value-discard" ::
        "-Ywarn-nullary-override" ::
        "-Ywarn-nullary-unit" ::
        Nil
      case _ =>
        Nil
    }
  },

  initialCommands in console := """
  import colorado._
  """,
)

enablePlugins(ScalaJSPlugin)

lazy val colorado = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure)
  .settings(commonSettings)
  .settings(
    name := "colorado",

    libraryDependencies ++=
      Deps.scalaTest.value % Test ::
      Nil
  )
  .jsSettings(
    scalacOptions ++= git.gitHeadCommit.value.map { headCommit =>
      val local = baseDirectory.value.toURI
      val remote = s"https://raw.githubusercontent.com/fdietze/colorado/${headCommit}/"
      s"-P:scalajs:mapSourceURI:$local->$remote"
    }
  )

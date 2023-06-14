lazy val commonSettings = Seq(
  organization       := "com.github.fdietze",
  version            := "master-SNAPSHOT",
  crossScalaVersions := Seq("2.11.12", "2.12.18", "2.13.11", "3.3.0"),
  scalaVersion       := crossScalaVersions.value.last,
  scalacOptions --= Seq("-Xfatal-warnings"), // overwrite sbt-tpolecat setting

  resolvers ++= Seq(
    "jitpack" at "https://jitpack.io",
  ),
  console / initialCommands := """
  import colorado._
  """,
)

enablePlugins(ScalaJSPlugin)

lazy val colorado = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(commonSettings)
  .settings(
    name := "colorado",
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % "3.2.16" % Test,
    ),
  )

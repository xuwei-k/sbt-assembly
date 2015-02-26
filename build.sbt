lazy val commonSettings: Seq[Setting[_]] = Seq(
  version in ThisBuild := "0.13.0",
  organization in ThisBuild := "com.eed3si9n"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    sbtPlugin := true,
    name := "sbt-assembly",
    description := "sbt plugin to create a single fat jar",
    licenses := Seq("MIT License" -> url("https://github.com/sbt/sbt-assembly/blob/master/LICENSE")),
    scalacOptions := Seq("-deprecation", "-unchecked", "-Dscalac.patmat.analysisBudget=1024"),
    libraryDependencies += "org.scalactic" %% "scalactic" % "2.2.1",
    publishArtifact in (Compile, packageBin) := true,
    publishArtifact in (Test, packageBin) := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := true,
    publishMavenStyle := false,
    publishTo := {
      if (version.value contains "-SNAPSHOT") Some(Resolver.sbtPluginRepo("snapshots"))
      else Some(Resolver.sbtPluginRepo("releases"))
    },
    credentials += Credentials(Path.userHome / ".ivy2" / ".sbtcredentials")
  )

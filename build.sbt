lazy val root = (project in file(".")).
  settings(
    sbtPlugin := true,
    name := "sbt-assembly",
    organization := "com.eed3si9n",
    version := "0.12.0-SNAPSHOT",
    description := "sbt plugin to create a single fat jar",
    licenses := Seq("MIT License" -> url("https://github.com/sbt/sbt-assembly/blob/master/LICENSE")),
    scalacOptions := Seq("-deprecation", "-unchecked"),
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

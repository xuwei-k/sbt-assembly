sbtPlugin := true

name := "sbt-assembly"

organization := "com.eed3si9n"

version := "0.11.2"

description := "sbt plugin to create a single fat jar"

licenses := Seq("MIT License" -> url("https://github.com/sbt/sbt-assembly/blob/master/LICENSE"))

scalacOptions := Seq("-deprecation", "-unchecked")

publishArtifact in (Compile, packageBin) := true

publishArtifact in (Test, packageBin) := false

publishArtifact in (Compile, packageDoc) := false

publishArtifact in (Compile, packageSrc) := true

publishMavenStyle := false

publishTo := {
  if (version.value contains "-SNAPSHOT") Some(Resolver.sbtPluginRepo("snapshots"))
  else Some(Resolver.sbtPluginRepo("releases"))
}

credentials += Credentials(Path.userHome / ".ivy2" / ".sbtcredentials")

lsSettings

LsKeys.tags in LsKeys.lsync := Seq("sbt", "jar")

(externalResolvers in LsKeys.lsync) := Seq(
  "sbt-plugin-releases" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases")

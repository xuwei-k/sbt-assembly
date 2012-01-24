sbtPlugin := true

name := "sbt-assembly"

organization := "com.eed3si9n"

version := "0.7.3-SNAPSHOT"

description := "sbt plugin to create a single fat jar"

licenses := Seq("MIT License" -> url("https://github.com/sbt/sbt-assembly/blob/master/LICENSE"))

scalacOptions := Seq("-deprecation", "-unchecked")

publishArtifact in (Compile, packageBin) := true

publishArtifact in (Test, packageBin) := false

publishArtifact in (Compile, packageDoc) := false

publishArtifact in (Compile, packageSrc) := false

seq(lsSettings :_*)

LsKeys.tags in LsKeys.lsync := Seq("sbt", "jar")

licenses in LsKeys.lsync <<= licenses

seq(ScriptedPlugin.scriptedSettings: _*)

publishMavenStyle := false

publishTo := Some(Resolver.url("sbt-plugin-releases",
  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns))

// publishTo <<= version { (v: String) =>
//   if(v endsWith "-SNAPSHOT") Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/snapshots/")
//   else Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")
// }

credentials += Credentials(Path.userHome / ".ivy2" / ".sbtcredentials")

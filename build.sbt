sbtPlugin := true

name := "sbt-assembly"

organization := "com.eed3si9n"

// version <<= (sbtVersion, version in Posterous) { (sv, nv) => "sbt" + sv + "_" + nv }

version := "0.7.2"

description := "sbt plugin to create a single fat jar"

licenses := Seq("MIT License" -> url("https://github.com/sbt/sbt-assembly/blob/master/LICENSE"))

scalacOptions := Seq("-deprecation", "-unchecked")

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  if(v endsWith "-SNAPSHOT") Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/snapshots/")
  else Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishArtifact in (Compile, packageBin) := true

publishArtifact in (Test, packageBin) := false

publishArtifact in (Compile, packageDoc) := false

publishArtifact in (Compile, packageSrc) := false

seq(lsSettings :_*)

LsKeys.tags in LsKeys.lsync := Seq("sbt", "jar")

licenses in LsKeys.lsync <<= licenses

seq(ScriptedPlugin.scriptedSettings: _*)

sbtPlugin := true

name := "sbt-assembly"

organization := "com.eed3si9n"

version := "0.2"

scalacOptions := Seq("-deprecation", "-unchecked")

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  if(v endsWith "-SNAPSHOT") Some(ScalaToolsSnapshots)
  else Some(ScalaToolsReleases)
}

publishArtifact in (Compile, packageDoc) := false

publishArtifact in (Compile, packageSrc) := false

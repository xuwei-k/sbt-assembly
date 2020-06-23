ThisBuild / version := "0.15.1-SNAPSHOT"
ThisBuild / organization := "com.eed3si9n"

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-assembly",
    description := "sbt plugin to create a single fat jar",
    licenses := Seq("MIT License" -> url("https://github.com/sbt/sbt-assembly/blob/master/LICENSE")),
    scalacOptions := Seq("-deprecation", "-unchecked", "-Dscalac.patmat.analysisBudget=1024", "-Xfuture"),
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.2.0",
      "com.eed3si9n.jarjarabrams" %% "jarjar-abrams-core" % "0.1.0",
      "org.scalatest" %% "scalatest" % "3.1.2" % Test,
    ),
    crossSbtVersions := Seq("0.13.18", "1.2.8"), // https://github.com/sbt/sbt/issues/5049
    publishArtifact in (Compile, packageBin) := true,
    publishArtifact in (Test, packageBin) := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := true
  )

lazy val commonSettings: Seq[Setting[_]] = Seq(
  version in ThisBuild := "0.14.7-SNAPSHOT",
  organization in ThisBuild := "com.eed3si9n"
)

lazy val root = (project in file(".")).
  enablePlugins(SbtPlugin).
  settings(commonSettings: _*).
  settings(
    sbtPlugin := true,
    name := "sbt-assembly",
    description := "sbt plugin to create a single fat jar",
    licenses := Seq("MIT License" -> url("https://github.com/sbt/sbt-assembly/blob/master/LICENSE")),
    scalacOptions := Seq("-deprecation", "-unchecked", "-Dscalac.patmat.analysisBudget=1024", "-Xfuture"),
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.0.1",
      "org.pantsbuild" % "jarjar" % "1.6.5"
    ),
    TaskKey[Unit]("runScriptedTest") := Def.taskDyn {
      val sbtBinVersion = (sbtBinaryVersion in pluginCrossBuild).value
      val base = sbtTestDirectory.value

      def isCompatible(directory: File): Boolean = {
        val buildProps = new java.util.Properties()
        IO.load(buildProps, directory / "project" / "build.properties")
        Option(buildProps.getProperty("sbt.version"))
          .map { version =>
            val requiredBinVersion = CrossVersion.binarySbtVersion(version)
            val compatible = requiredBinVersion == sbtBinVersion
            if (!compatible) {
              val testName = directory.relativeTo(base).getOrElse(directory)
              streams.value.log.warn(s"Skipping $testName since it requires sbt $requiredBinVersion")
            }
            compatible
          }
          .getOrElse(true)
      }

      val testDirectoryFinder = base * AllPassFilter * AllPassFilter filter { _.isDirectory }
      val tests = for {
        test <- testDirectoryFinder.get
        if isCompatible(test)
        path <- Path.relativeTo(base)(test)
      } yield path.replace('\\', '/')

      if (tests.nonEmpty)
        Def.task(scripted.toTask(tests.mkString(" ", " ", "")).value)
      else
        Def.task(streams.value.log.warn("No tests can be run for this sbt version"))
    }.value,
    publishArtifact in (Compile, packageBin) := true,
    publishArtifact in (Test, packageBin) := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := true
  )

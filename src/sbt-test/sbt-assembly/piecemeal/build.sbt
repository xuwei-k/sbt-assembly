lazy val root = (project in file(".")).
  settings(
    name := "foo",
    version := "0.1",
    scalaVersion := "2.10.2",
    assembleArtifact in assemblyPackageScala := false,
    assembleArtifact in assemblyPackageDependency := false,
    TaskKey[Unit]("check") := {
      val process = sys.process.Process("java", Seq("-cp", 
        (crossTarget.value / "scala-library-2.10.2-assembly.jar").toString + ":" +
        (crossTarget.value / "foo-assembly-0.1.jar").toString,
        "Main"))
      val out = (process!!)
      if (out.trim != "hello") sys.error("unexpected output: " + out)
      ()
    }
  )

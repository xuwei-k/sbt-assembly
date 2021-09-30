lazy val root = (project in file(".")).
  settings(
    version := "0.1",
    scalaVersion := "2.12.15",
    assembly / mainClass := Some("foo.Hello"),
    assembly / assemblyJarName := "foo.jar",
    TaskKey[Unit]("check") := {
      val process = sys.process.Process("java", Seq("-jar", (crossTarget.value / "foo.jar").toString))
      val out = (process!!)
      if (out.trim != "hello") sys.error("unexpected output: " + out)
      ()
    }
  )

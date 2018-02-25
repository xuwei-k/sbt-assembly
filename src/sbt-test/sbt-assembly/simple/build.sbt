lazy val root = (project in file(".")).
  settings(
    version := "0.1",
    scalaVersion := "2.10.7",
    assemblyOption in assembly ~= { _.copy(cacheOutput = true) },
    assemblyOption in assembly ~= { _.copy(cacheUnzip = true) },
    assemblyJarName in assembly := "foo.jar",
    TaskKey[Unit]("check") := {
      val process = sys.process.Process("java", Seq("-jar", (crossTarget.value / "foo.jar").toString))
      val out = (process!!)
      if (out.trim != "hello") sys.error("unexpected output: " + out)
      ()
    }
  )


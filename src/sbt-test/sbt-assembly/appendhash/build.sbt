lazy val root = (project in file(".")).
  settings(
    name := "foo",
    version := "0.1",
    scalaVersion := "2.10.2",
    libraryDependencies ++= Seq(
      "net.databinder.dispatch" %% "dispatch-core" % "0.11.0"
    ),
    assemblyOption in assembly ~= { _.copy(includeScala = false, includeDependency = false) },
    assemblyOption in assembly ~= { _.copy(appendContentHash = true) },
    assemblyOption in assemblyPackageDependency ~= { _.copy(appendContentHash = true) },
    TaskKey[Unit]("check") := {
      val process = sys.process.Process("java", Seq("-cp", 
        (crossTarget.value / "foo-assembly-0.1-7a4ebf373b385ed1badbab93d52cffdfc4587c04.jar").toString +
        java.io.File.pathSeparator +
        (crossTarget.value / "foo-assembly-0.1-deps-1aa2cc229f2e93446713bf8d1c6efc1e6ddab0fe.jar").toString,
        "Main"))
      val out = (process!!)
      if (out.trim != "hello") sys.error("unexpected output: " + out)
      ()
    }
  )

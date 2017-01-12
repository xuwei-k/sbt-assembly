lazy val root = (project in file(".")).
  settings(
    version := "0.1",
    scalaVersion := "2.9.1"
  ).
  settings(inConfig(Test)(baseAssemblySettings): _*).
  settings(
    assemblyJarName in (Test, assembly) := "foo.jar",
    TaskKey[Unit]("check") := {
      val process = sbt.Process("java", Seq("-jar", (crossTarget.value / "foo.jar").toString))
      val out = (process!!)
      if (out.trim != "hellospec") sys.error("unexpected output: " + out)
      ()
    }
  )

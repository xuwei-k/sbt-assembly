lazy val root = (project in file(".")).
  settings(
    version := "0.1",
    scalaVersion := "2.9.1",
    libraryDependencies += "org.scalatest" % "scalatest_2.9.0" % "1.6.1" % "test",
    libraryDependencies += "com.weiglewilczek.slf4s" %% "slf4s" % "1.0.7",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "0.9.29" % "runtime",
    unmanagedJars in Compile ++= {
       (baseDirectory.value / "lib" / "compile" ** "*.jar").classpath
    },
    unmanagedJars in Runtime ++= {
       (baseDirectory.value / "lib" / "runtime" ** "*.jar").classpath
    },
    unmanagedJars in Test ++= {
       (baseDirectory.value / "lib" / "test" ** "*.jar").classpath
    },
    assemblyExcludedJars in assembly := {
      (fullClasspath in assembly).value filter {_.data.getName == "compile-0.1.0.jar"}
    },
    assemblyJarName in assembly := "foo.jar",
    TaskKey[Unit]("check") := {
      val process = sbt.Process("java", Seq("-jar", (crossTarget.value / "foo.jar").toString))
      val out = (process!!)
      if (out.trim != "hello") sys.error("unexpected output: " + out)
      ()
    }
  )

lazy val root = (project in file(".")).
  settings(
    version := "0.1",
    scalaVersion := "2.11.8",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "0.9.29" % "runtime",
    unmanagedJars in Compile <++= baseDirectory map { base =>
       (base / "lib" / "compile" ** "*.jar").classpath
    },
    unmanagedJars in Runtime <++= baseDirectory map { base =>
       (base / "lib" / "runtime" ** "*.jar").classpath
    },
    unmanagedJars in Test <++= baseDirectory map { base =>
       (base / "lib" / "test" ** "*.jar").classpath
    },
    assemblyExcludedJars in assembly <<= (fullClasspath in assembly) map { cp => 
      cp filter {_.data.getName == "compile-0.1.0.jar"}
    },
    assemblyJarName in assembly := "foo.jar",
    TaskKey[Unit]("check") <<= (crossTarget) map { (crossTarget) =>
      val process = sbt.Process("java", Seq("-jar", (crossTarget / "foo.jar").toString))
      val out = (process!!)
      if (out.trim != "hello") sys.error("unexpected output: " + out)
      ()
    }
  )

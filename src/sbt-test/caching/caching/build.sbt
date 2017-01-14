lazy val root = (project in file(".")).
  settings(
    version := "0.1",
    scalaVersion := "2.11.8",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "0.9.29" % "runtime",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(cacheOutput = true),
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(cacheUnzip = true),
    assemblyJarName in assembly := "foo.jar",
    TaskKey[Seq[File]]("genresource") := {
      val dirs = (unmanagedResourceDirectories in Compile).value
      val file = dirs.head / "foo.txt"
      IO.write(file, "bye")
      Seq(file)
    },
    TaskKey[Seq[File]]("genresource2") := {
      val dirs = (unmanagedResourceDirectories in Compile).value
      val file = dirs.head / "bar.txt"
      IO.write(file, "bye")
      Seq(file)
    },
    TaskKey[Unit]("check") := {
      val process = sbt.Process("java", Seq("-jar", (crossTarget.value / "foo.jar").toString))
      val out = (process!!)
      if (out.trim != "hello") sys.error("unexpected output: " + out)
      ()
    },
    TaskKey[Unit]("checkfoo") := {
      val process = sbt.Process("java", Seq("-jar", (crossTarget.value / "foo.jar").toString))
      val out = (process!!)
      if (out.trim != "foo.txt") sys.error("unexpected output: " + out)
      ()
    },
    TaskKey[Unit]("checkhash") := {
      import java.security.MessageDigest
      val jarHash = crossTarget.value / "jarHash.txt"
      val hash = MessageDigest.getInstance("SHA-1").digest(IO.readBytes(crossTarget.value / "foo.jar")).map( b => "%02x".format(b) ).mkString
      if ( jarHash.exists )
      {
        val prevHash = IO.read(jarHash)
        streams.value.log.info( "Checking hash: " + hash + ", " + prevHash )
        assert( hash == prevHash )
      }
      IO.write( jarHash, hash )
      ()
    }
  )

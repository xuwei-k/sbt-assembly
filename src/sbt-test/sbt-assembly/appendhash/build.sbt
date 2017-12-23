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
    InputKey[Unit]("checkFile") := {
      val args = sbt.complete.Parsers.spaceDelimited("<arg>").parsed
      val expectFileNameRegex = args.head.r
      assert((crossTarget.value ** "*.jar").get.exists{ jar =>
        expectFileNameRegex.findFirstIn(jar.getName).isDefined
      })
    },
    TaskKey[Unit]("checkPrevious") := {
      import sbinary.DefaultProtocol._
      import sbtassembly.PluginCompat._
      import CacheImplicits._
      assert(Some(assembly.value) == assembly.previous)
    }
  )

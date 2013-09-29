import AssemblyKeys._

name := "foo"

version := "0.1"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0"
)

assemblySettings

assemblyOption in assembly ~= { _.copy(includeScala = false, includeDependency = false) }

assemblyOption in assembly ~= { _.copy(appendContentHash = true) }

assemblyOption in packageDependency ~= { _.copy(appendContentHash = true) }

TaskKey[Unit]("check") <<= (crossTarget) map { (crossTarget) =>
  val process = sbt.Process("java", Seq("-cp", 
    (crossTarget / "foo-assembly-0.1-36cb6ed20856f73f6fd0a9182a01ed1be3698cda.jar").toString +
    java.io.File.pathSeparator +
    (crossTarget / "foo-assembly-0.1-deps-41634647ccb50e55269aa81227bea572a0edfc9e.jar").toString,
    "Main"))
  val out = (process!!)
  if (out.trim != "hello") error("unexpected output: " + out)
  ()
}

import AssemblyKeys._

version := "0.1"

scalaVersion := "2.10.2"

assemblySettings

assemblyCacheOutput in assembly := true 

assemblyCacheUnzip in assembly := true

jarName in assembly := "foo.jar"

TaskKey[Unit]("check") <<= (crossTarget) map { (crossTarget) =>
  val process = sbt.Process("java", Seq("-jar", (crossTarget / "foo.jar").toString))
  val out = (process!!)
  if (out.trim != "hello") error("unexpected output: " + out)
  ()
}

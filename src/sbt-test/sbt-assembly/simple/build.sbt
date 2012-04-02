import AssemblyKeys._

version := "0.1"

assemblySettings

jarName in assembly := "foo.jar"

TaskKey[Unit]("check") <<= (target) map { (target) =>
  val process = sbt.Process("java", Seq("-jar", (target / "foo.jar").toString))
  val out = (process!!)
  if (out.trim != "hello") error("unexpected output: " + out)
  ()
}

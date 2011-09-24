import AssemblyKeys._

version := "0.1"

seq(inConfig(Test)(baseAssemblySettings): _*)

jarName in (Test, assembly) := "foo.jar"

TaskKey[Unit]("check") <<= (target) map { (target) =>
  val process = sbt.Process("java", Seq("-jar", (target / "foo.jar").toString))
  val out = (process!!)
  if (out.trim != "hellospec") error("unexpected output: " + out)
  ()
}

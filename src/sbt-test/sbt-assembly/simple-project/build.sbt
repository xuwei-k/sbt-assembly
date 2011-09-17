version := "0.1"

seq(Assembly.settings: _*)

Assembly.jarName := "foo.jar"

TaskKey[Unit]("check") <<= (target) map { (target) =>
  val process = sbt.Process("java", Seq("-jar", (target / "foo.jar").toString))
  val out = (process!!)
  if (out.trim != "hello") error("not hello")
  ()
}

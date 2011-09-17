import sbt._
import Keys._

object ScriptedTestBuild extends Build {
  import sbtassembly.Plugin._
  
  lazy val assertAssembly = TaskKey[Unit]("assert-assembly")
  
  private def assertAssemblyTask = (target) map { (target) =>
    val process = Process("java", Seq("-jar", (target / "foo.jar").toString))
    val out = (process!!)
    if (out.trim != "hello") error("not hello")
    
    ()
  }
  
  lazy val root = Project("main", file("."), settings = Defaults.defaultSettings ++
    Seq(assertAssembly <<= assertAssemblyTask,
        version := "0.1") ++
    Assembly.settings ++
    Seq(Assembly.jarName in Assembly := "foo.jar"))   
}

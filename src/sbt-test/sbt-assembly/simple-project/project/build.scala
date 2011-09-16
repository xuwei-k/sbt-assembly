import sbt._
import Keys._

object ScriptedTestBuild extends Build {
  import sbtassembly.Plugin._
  
  lazy val assertAssembly = TaskKey[Unit]("assert-assembly")
  
  private def assertAssemblyTask = (target) map { (target) =>
    if (!(target / "foo.jar").exists) error("bad")
    
    ()
  }
  
  lazy val root = Project("main", file("."), settings = Defaults.defaultSettings ++
    Seq(assertAssembly <<= assertAssemblyTask,
        version := "0.1") ++
    sbtassembly.Plugin.assemblySettings ++
    Seq(Assembly.jarName in Assembly := "foo.jar"))   
}

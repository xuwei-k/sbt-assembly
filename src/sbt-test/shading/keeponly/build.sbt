lazy val testkeep = (project in file(".")).
  settings(
    version := "0.1",
    assemblyJarName in assembly := "foo.jar",
    scalaVersion := "2.11.8",
    assemblyShadeRules in assembly := Seq(
      ShadeRule.keep("keep.**").inProject
    ),
    TaskKey[Unit]("check") := {
      IO.withTemporaryDirectory { dir ⇒
        IO.unzip(crossTarget.value / "foo.jar", dir)
        mustNotExist(dir / "removed" / "ShadeClass.class")
        mustNotExist(dir / "removed" / "ShadePackage.class")
        mustExist(dir / "keep" / "Keeped.class")
      }
    })

def mustNotExist(f: File): Unit = {
  if (f.exists) sys.error("file" + f + " exists!")
}
def mustExist(f: File): Unit = {
  if (!f.exists) sys.error("file" + f + " does not exist!")
}

lazy val testshade = (project in file(".")).
  settings(
    version := "0.1",
    assemblyJarName in assembly := "foo.jar",
    scalaVersion := "2.9.1",
    assemblyShadingRules in assembly := Seq(
      ShadeRule.Remove("remove.*").applyToCompiling,
      ShadeRule.Rename("toshade.ShadeClass" -> "toshade.ShadedClass").applyToCompiling,
      ShadeRule.Rename("toshade.ShadePackage" -> "shaded_package.ShadePackage").applyToCompiling
    ),
    TaskKey[Unit]("check") <<= (crossTarget) map { (crossTarget) ⇒
      IO.withTemporaryDirectory { dir ⇒
        IO.unzip(crossTarget / "foo.jar", dir)
        mustNotExist(dir / "remove" / "Removed.class")
        mustExist(dir / "shaded_package" / "ShadePackage.class")
        mustExist(dir / "toshade" / "ShadedClass.class")
      }
    })

def mustNotExist(f: File): Unit = {
  if (f.exists) sys.error("file" + f + " exists!")
}
def mustExist(f: File): Unit = {
  if (!f.exists) sys.error("file" + f + " does not exist!")
}

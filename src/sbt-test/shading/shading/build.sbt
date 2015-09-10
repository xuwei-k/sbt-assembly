lazy val testshade = (project in file(".")).
  settings(
    version := "0.1",
    assemblyJarName in assembly := "foo.jar",
    scalaVersion := "2.9.1",
    libraryDependencies += "commons-io" % "commons-io" % "2.4",
    assemblyShadeRules in assembly := Seq(
      ShadeRule.remove("remove.**").inProject,
      ShadeRule.rename("toshade.ShadeClass" -> "toshade.ShadedClass").inProject,
      ShadeRule.rename("toshade.ShadePackage" -> "shaded_package.ShadePackage").inProject,
      ShadeRule.rename("org.apache.commons.io.**" -> "shadeio.@1").inLibrary("commons-io" % "commons-io" % "2.4")
    ),
    TaskKey[Unit]("check") <<= (crossTarget) map { (crossTarget) ⇒
      IO.withTemporaryDirectory { dir ⇒
        IO.unzip(crossTarget / "foo.jar", dir)
        mustNotExist(dir / "remove" / "Removed.class")
        mustExist(dir / "shaded_package" / "ShadePackage.class")
        mustExist(dir / "toshade" / "ShadedClass.class")
        mustExist(dir / "shadeio" / "ByteOrderMark.class")
      }
    })

def mustNotExist(f: File): Unit = {
  if (f.exists) sys.error("file" + f + " exists!")
}
def mustExist(f: File): Unit = {
  if (!f.exists) sys.error("file" + f + " does not exist!")
}

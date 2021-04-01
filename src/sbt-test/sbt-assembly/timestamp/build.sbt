def fixedTimestamp: Long = 1234567000L

assembly / packageOptions += Package.FixedTimestamp(Some(fixedTimestamp))

TaskKey[Unit]("check") := {
  val jar = crossTarget.value / (assembly / assemblyJarName).value
  IO.withTemporaryDirectory{ tmp =>
    val files = IO.unzip(jar, tmp)
    val expected = fixedTimestamp - java.util.TimeZone.getDefault().getOffset(fixedTimestamp)
    assert(files.nonEmpty)
    assert(files.forall(_.lastModified == expected))
  }
}

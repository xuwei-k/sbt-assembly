lazy val testmerge = (project in file(".")).
  settings(
    version := "0.1",
    jarName in assembly := "foo.jar",
    mergeStrategy in assembly := {
      val old = (mergeStrategy in assembly).value

      {
        case _ => MergeStrategy.singleOrError
      }
    }
  )

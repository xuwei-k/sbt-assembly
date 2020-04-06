{
  val pluginVersion = Option(System.getProperty("plugin.version")).getOrElse("0.14.11-SNAPSHOT")
  if(pluginVersion == null)
    throw new RuntimeException("""|The system property 'plugin.version' is not defined.
                                 |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
  else addSbtPlugin("com.eed3si9n" % "sbt-assembly" % pluginVersion changing())
}

ScriptedPlugin.scriptedSettings

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + version.value)
}

scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
  a => Seq("scala.ext.dirs").exists(a.contains)
)

scriptedBufferLog := false

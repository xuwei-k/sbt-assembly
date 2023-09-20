enablePlugins(SbtPlugin)

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx2024M", "-Dplugin.version=" + version.value)
}

scriptedBufferLog := false

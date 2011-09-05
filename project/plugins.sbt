resolvers += Resolver.url("Typesafe repository", new java.net.URL("http://typesafe.artifactoryonline.com/typesafe/ivy-releases/"))(Resolver.defaultIvyPatterns)

resolvers += Resolver.url("Typesafe snapshot repository", new java.net.URL("http://typesafe.artifactoryonline.com/typesafe/ivy-snapshots/"))(Resolver.defaultIvyPatterns)

// addSbtPlugin("org.scala-tools.sbt" % "scripted-plugin" % "0.11.0-RC0")

libraryDependencies <++= (sbtVersion) { sv =>
  Seq("org.scala-tools.sbt" %% "scripted-plugin" % sv,
      "org.scala-tools.sbt" %% "scripted-sbt" % sv)
}

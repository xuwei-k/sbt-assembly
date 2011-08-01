sbt-assembly
============

*Deploy fat JARs. Restart processes.*

sbt-assembly is a sbt 0.10 port of an awesome sbt plugin by codahale:

> assembly-sbt is a [simple-build-tool](http://code.google.com/p/simple-build-tool/)
plugin for building a single JAR file of your project which includes all of its
dependencies, allowing to deploy the damn thing as a single file without dicking
around with shell scripts and lib directories or, worse, welding your
configuration to your deployable in the form of a WAR file.

Requirements
------------

* Simple Build Tool
* The burning desire to have a simple deploy procedure.


How To Use
----------

Specify sbt-assembly as a dependency in `project/plugins/build.sbt`:

```scala
libraryDependencies <+= (sbtVersion) { sv => "com.eed3si9n" %% "sbt-assembly" % ("sbt" + sv + "_0.4") }
```

Or, specify sbt-assembly.git as a dependency in `project/plugins/project/build.scala`:

```scala
import sbt._

object Plugins extends Build {
  lazy val root = Project("root", file(".")) dependsOn(
    uri("git://github.com/eed3si9n/sbt-assembly.git#XX") // where XX is branch
  )
}
```

(You may need to check this project's tags to see what the most recent release
is. I'm notoriously crap about updating the version numbers in my READMEs.)

Then, add the following in your `build.sbt`:

```scala
seq(sbtassembly.Plugin.assemblySettings: _*)
```

or, for full configuration:

```scala
lazy val sub = Project("sub", file("sub")) settings(sbtassembly.Plugin.assemblySettings: _*)
```

Now you'll have an awesome new `assembly` task which will compile your project,
run your tests, and then pack your class files and all your dependencies into a
single JAR file: `target/scala_X.X.X/projectname-assembly-X.X.X.jar`.

If you specify a `mainClass in Assembly` in simple-build-tool (or just let it autodetect
one) then you'll end up with a fully executable JAR, ready to rock.

You can type

    > show assembly:[tab]

and list the keys you can rewire.

    assembly               assembly-option        configuration
    conflicting-files      dependency-classpath   excluded-files
    full-classpath         jar-name               main-class
    output-path            package-dependency     package-options
    package-scala          publish-artifact       streams
    test

For example the name of the jar can be set as follows in built.sbt:

```scala
jarName in Assembly := "something.jar"
```

To exclude Scala library,

```scala
publishArtifact in (Assembly, packageScala) := false
```

To exclude your source files,

```scala
publishArtifact in (Assembly, packageBin) := false
```

To exclude some package,

```scala
excludedFiles in Assembly := { (base: Seq[File]) =>
  ((base / "something-to-exclude" ** "*") +++
  ((base / "META-INF" * "*").get collect { case f if f.isFile => f })).get }
```

To make a jar containing only the dependencies, type

    > assembly:package-dependency

License
-------

Copyright (c) 2010-2011 e.e d3si9n, Coda Hale

Published under The MIT License, see LICENSE

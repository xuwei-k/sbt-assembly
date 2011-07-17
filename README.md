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

    libraryDependencies <+= (sbtVersion) { sv => "com.eed3si9n" %% "sbt-assembly" % ("sbt" + sv + "_0.3") }

Or, specify sbt-assembly.git as a dependency in `project/plugins/project/build.scala`:

    import sbt._

    object Plugins extends Build {
      lazy val root = Project("root", file(".")) dependsOn(
        uri("git://github.com/eed3si9n/sbt-assembly.git")
      )
    }

(You may need to check this project's tags to see what the most recent release
is. I'm notoriously crap about updating the version numbers in my READMEs.)

Now you'll have an awesome new `assembly` task which will compile your project,
run your tests, and then pack your class files and all your dependencies into a
single JAR file: `target/scala_X.X.X/projectname-assembly-X.X.X.jar`.

If you specify a `mainClass in Assembly` in simple-build-tool (or just let it autodetect
one) then you'll end up with a fully executable JAR, ready to rock.


License
-------

Copyright (c) 2010-2011 e.e d3si9n, Coda Hale

Published under The MIT License, see LICENSE
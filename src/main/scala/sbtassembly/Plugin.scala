package sbtassembly

import sbt._
import Keys._
import java.io.PrintWriter
import scala.collection.mutable
import scala.io.Source
import Project.Initialize

object Plugin extends sbt.Plugin {
  val Assembly = config("assembly") extend(Runtime)
  val assembly = TaskKey[File]("assembly", "Builds a single-file deployable jar.")
  val packageScala      = TaskKey[File]("package-scala", "Produces the scala artifact.")
  val packageDependency = TaskKey[File]("package-dependency", "Produces the dependency artifact.")
  
  val assemblyOption    = SettingKey[AssemblyOption]("assembly-option")
  val jarName           = SettingKey[String]("jar-name")
  val outputPath        = SettingKey[File]("output-path")
  val excludedFiles     = SettingKey[Seq[File] => Seq[File]]("excluded-files")  
  val conflictingFiles  = SettingKey[Seq[File] => Seq[File]]("conflicting-files")  
  
  private def assemblyTask(out: File, po: Seq[PackageOption], ao: AssemblyOption,
      classpath: Classpath, dependencies: Classpath, cacheDir: File, log: Logger): File =
    IO.withTemporaryDirectory { tempDir =>
      val srcs = assemblyPaths(tempDir, classpath, dependencies, ao, log)
      val config = new Package.Configuration(srcs, out, po)
      Package(config, cacheDir, log)
      out
    }
  
  private def assemblyOptionTask: Initialize[AssemblyOption] =
    (publishArtifact in (Assembly, packageBin), publishArtifact in (Assembly, packageScala),
     publishArtifact in (Assembly, packageDependency), excludedFiles, conflictingFiles) {
      (includeBin, includeScala, includeDeps, exclude, conflicting) =>   
      AssemblyOption(includeBin, includeScala, includeDeps, exclude, conflicting) 
    }

  private def assemblyPackageOptionsTask: Initialize[Task[Seq[PackageOption]]] =
    (packageOptions in Compile, mainClass in Assembly) map { (os, mainClass) =>
      mainClass map { s =>
        os find { o => o.isInstanceOf[Package.MainClass] } map { _ => os
        } getOrElse { Package.MainClass(s) +: os }
      } getOrElse {os}      
    }

  private def assemblyExcludedFiles(base: Seq[File]): Seq[File] =
    (base / "META-INF" * "*").get collect { case f if f.isFile => f }
    
  private def assemblyPaths(tempDir: File, classpath: Classpath, dependencies: Classpath,
      ao: AssemblyOption, log: Logger) = {
    import sbt.classpath.ClasspathUtilities

    val (libs, dirs) = classpath.map(_.data).partition(ClasspathUtilities.isArchive)
    val (depLibs, depDirs) = dependencies.map(_.data).partition(ClasspathUtilities.isArchive)
    val services = mutable.Map[String, mutable.ArrayBuffer[String]]()
    val libsFiltered = libs flatMap {
      case jar if List("scala-library.jar", "scala-compiler.jar") contains jar.asFile.getName =>
        if (ao.includeScala) Some(jar) else None
      case jar if depLibs contains jar.asFile =>
        if (ao.includeDependency) Some(jar) else None
      case jar => Some(jar)
    }
    val dirsFiltered = dirs flatMap {
      case jar if depLibs contains jar.asFile =>
        if (ao.includeDependency) Some(jar) else None
      case jar => Some(jar) 
    }
    
    for(jar <- libsFiltered) {
      val jarName = jar.asFile.getName
      log.info("Including %s".format(jarName))
      IO.unzip(jar, tempDir)
      IO.delete(ao.conflicting(Seq(tempDir)))
      val servicesDir = tempDir / "META-INF" / "services"
      if (servicesDir.asFile.exists) {
       for (service <- (servicesDir ** "*").get) {
         val serviceFile = service.asFile
         if (serviceFile.exists && serviceFile.isFile) {
           val entries = services.getOrElseUpdate(serviceFile.getName, new mutable.ArrayBuffer[String]())
           for (provider <- Source.fromFile(serviceFile).getLines) {
             if (!entries.contains(provider)) {
               entries += provider
             }
           }
         }
       }
     }
    }

    for ((service, providers) <- services) {
      log.info("Merging providers for %s".format(service))
      val serviceFile = (tempDir / "META-INF" / "services" / service).asFile
      val writer = new PrintWriter(serviceFile)
      for (provider <- providers.map { _.trim }.filter { !_.isEmpty }) {
        log.debug("-  %s".format(provider))
        writer.println(provider)
      }
      writer.close()
    }

    val base = tempDir +: dirsFiltered
    val descendants = ((base ** (-DirectoryFilter)) --- ao.exclude(base)).get
    descendants x relativeTo(base)
  }
  
  lazy val assemblySettings: Seq[sbt.Project.Setting[_]] = inConfig(Assembly)(Seq(
    assembly <<= (test, outputPath, packageOptions, assemblyOption, 
        fullClasspath, dependencyClasspath, cacheDirectory, streams) map {
      (test, out, po, ao, cp, deps, cacheDir, s) =>
        assemblyTask(out, po, ao, cp, deps, cacheDir, s.log) },
    
    packageScala <<= (outputPath, packageOptions in Runtime, assemblyOption, 
        fullClasspath, dependencyClasspath, cacheDirectory, streams) map {
      (out, po, ao, cp, deps, cacheDir, s) =>
        assemblyTask(out, po,
          ao.copy(includeBin = false, includeScala = true, includeDependency = false),
          cp, deps, cacheDir, s.log) },
          
    packageDependency <<= (outputPath, packageOptions in Runtime, assemblyOption, 
        fullClasspath, dependencyClasspath, cacheDirectory, streams) map {
      (out, po, ao, cp, deps, cacheDir, s) =>
        assemblyTask(out, po,
          ao.copy(includeBin = false, includeScala = false, includeDependency = true),
          cp, deps, cacheDir, s.log) },
        
    assemblyOption <<= assemblyOptionTask,
    jarName <<= (name, version) { (name, version) => name + "-assembly-" + version + ".jar" },
    outputPath <<= (target, jarName) { (t, s) => t / s },
    test <<= (test in Test).identity,
    mainClass <<= (mainClass in Runtime).identity,
    fullClasspath <<= (fullClasspath in Runtime).identity,
    dependencyClasspath <<= (dependencyClasspath in Runtime).identity,
    packageOptions <<= assemblyPackageOptionsTask,
    excludedFiles := assemblyExcludedFiles _,
    conflictingFiles := assemblyExcludedFiles _
  )) ++
  Seq(
    assembly <<= (assembly in Assembly).identity,
    publishArtifact in (Assembly, packageBin) := true,
    publishArtifact in (Assembly, packageScala) := true,
    publishArtifact in (Assembly, packageDependency) := true
  )
}

case class AssemblyOption(includeBin: Boolean,
  includeScala: Boolean,
  includeDependency: Boolean,
  exclude: Seq[File] => Seq[File],
  conflicting: Seq[File] => Seq[File])

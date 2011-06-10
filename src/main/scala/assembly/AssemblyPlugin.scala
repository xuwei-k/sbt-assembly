package assembly

import sbt._
import Keys._
import java.io.PrintWriter
import scala.collection.mutable
import scala.io.Source
import Project.Initialize

object AssemblyPlugin extends Plugin {  
  val assembly = TaskKey[Unit]("assembly")
  
  val assemblyExclude           = SettingKey[PathFinder => PathFinder]("assembly-exclude")  
  val assemblyOutputPath        = SettingKey[File]("assembly-output-path")
  val assemblyJarName           = SettingKey[String]("assembly-jar-name")
  val assemblyClasspath         = TaskKey[Classpath]("assembly-classpath")
  val assemblyConflictingFiles  = SettingKey[File => List[File]]("assembly-conflicting-files") 
  val assemblyPackageOptions    = TaskKey[Seq[PackageOption]]("assembly-package-options")
        
  private def assemblyTask: Initialize[Task[Unit]] =
    (test in Test, assemblyPackageOptions, cacheDirectory, assemblyOutputPath,
        assemblyClasspath, assemblyExclude, assemblyConflictingFiles, streams) map {
      (test, options, cacheDir, jarPath, cp, exclude, conflicting, s) =>
        IO.withTemporaryDirectory { tempDir =>
          val srcs = assemblyPaths(tempDir, cp, exclude, conflicting, s.log)
          val config = new Package.Configuration(srcs, jarPath, options)
          Package(config, cacheDir, s.log) 
        }
    }
    
  private def assemblyPackageOptionsTask: Initialize[Task[Seq[PackageOption]]] =
    (packageOptions, mainClass in Runtime) map { (os, mainClass) =>
      mainClass map { s =>
        os find { o => o.isInstanceOf[Package.MainClass] } map { _ => os
        } getOrElse { Package.MainClass(s) +: os }
      } getOrElse {os}      
    }

  private def excludePaths(base: PathFinder) =
    (base / "META-INF" ** "*") --- // generally ignore the hell out of META-INF
      (base / "META-INF" / "services" ** "*") --- // include all service providers
      (base / "META-INF" / "maven" ** "*") // include all Maven POMs and such
      
  private def conflictingFiles(path: File) = List((path / "META-INF" / "LICENSE"),
                                                  (path / "META-INF" / "license"),
                                                  (path / "META-INF" / "License"))      
          
  private def assemblyPaths(tempDir: File, classpath: Classpath,
      exclude: PathFinder => PathFinder, conflicting: File => List[File], log: Logger) = {
    import sbt.classpath.ClasspathUtilities

    val (libs, directories) = classpath.map(_.data).partition(ClasspathUtilities.isArchive)
    val services = mutable.Map[String, mutable.ArrayBuffer[String]]()
    for(jar <- libs) {
      val jarName = jar.asFile.getName
      log.info("Including %s".format(jarName))
      IO.unzip(jar, tempDir)
      IO.delete(conflicting(tempDir))
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
      log.debug("Merging providers for %s".format(service))
      val serviceFile = (tempDir / "META-INF" / "services" / service).asFile
      val writer = new PrintWriter(serviceFile)
      for (provider <- providers.map { _.trim }.filter { !_.isEmpty }) {
        log.debug("-  %s".format(provider))
        writer.println(provider)
      }
      writer.close()
    }

    val base = tempDir +: directories
    val descendants = ((base ** "*") --- exclude(base)).get filter {_.isFile}
    descendants x relativeTo(base)
  }
  
  override lazy val settings = Seq(
    assembly <<= assemblyTask,
    assemblyPackageOptions <<= assemblyPackageOptionsTask,
    assemblyExclude := excludePaths _,
    assemblyOutputPath <<= (target, assemblyJarName) { (t, s) => t / s },
    assemblyJarName <<= (name, version) { (name, version) => name + "-assembly-" + version + ".jar" },
    assemblyClasspath <<= (fullClasspath in Runtime) map { x => x },
    assemblyConflictingFiles := conflictingFiles _
  )
}

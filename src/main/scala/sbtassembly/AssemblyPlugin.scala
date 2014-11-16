package sbtassembly

import sbt._
import Keys._
import scala.io.Source

object AssemblyPlugin extends sbt.AutoPlugin {
  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements

  object autoImport extends AssemblyKeys {
    val Assembly = sbtassembly.Assembly
    val MergeStrategy = sbtassembly.MergeStrategy
    val baseAssemblySettings = AssemblyPlugin.baseAssemblySettings
  }
  import autoImport.{ Assembly => _, baseAssemblySettings => _, _ }
   
  val defaultShellScript: Seq[String] = Seq("#!/usr/bin/env sh", """exec java -jar "$0" "$@"""") // "

  override lazy val projectSettings: Seq[Def.Setting[_]] = assemblySettings

  lazy val baseAssemblySettings: Seq[sbt.Def.Setting[_]] = Seq(
    assembly := Assembly.assemblyTask(assembly).value,
    assembledMappings in assembly          := Assembly.assembledMappingsTask(assembly).value,
    packageScala                           := Assembly.assemblyTask(packageScala).value,
    assembledMappings in packageScala      := Assembly.assembledMappingsTask(packageScala).value,
    packageDependency                      := Assembly.assemblyTask(packageDependency).value,
    assembledMappings in packageDependency := Assembly.assembledMappingsTask(packageDependency).value,

    // test
    test in assembly := (test in Test).value,
    test in packageScala := (test in assembly).value,
    test in packageDependency := (test in assembly).value,
    
    // assemblyOption
    assembleArtifact in packageBin := true,
    assembleArtifact in packageScala := true,
    assembleArtifact in packageDependency := true,
    mergeStrategy in assembly := MergeStrategy.defaultMergeStrategy,
    excludedJars in assembly := Nil,
    assemblyOption in assembly := {
      val s = streams.value
      AssemblyOption(
        assemblyDirectory  = s.cacheDirectory / "assembly",
        includeBin         = (assembleArtifact in packageBin).value,
        includeScala       = (assembleArtifact in packageScala).value,
        includeDependency  = (assembleArtifact in packageDependency).value,
        mergeStrategy      = (mergeStrategy in assembly).value,
        excludedJars       = (excludedJars in assembly).value,
        excludedFiles      = Assembly.defaultExcludedFiles,
        cacheOutput        = true,
        cacheUnzip         = true,
        appendContentHash  = false,
        prependShellScript = None)
    },

    assemblyOption in packageScala := {
      val ao = (assemblyOption in assembly).value
      ao.copy(includeBin = false, includeScala = true, includeDependency = false)
    },
    assemblyOption in packageDependency := {
      val ao = (assemblyOption in assembly).value
      ao.copy(includeBin = false, includeScala = true, includeDependency = true)
    },

    // packageOptions
    packageOptions in assembly <<= (packageOptions in (Compile, packageBin), mainClass in assembly) map { (os, mainClass) =>
      mainClass map { s =>
        Package.MainClass(s) +: (os filterNot {_.isInstanceOf[Package.MainClass]})
      } getOrElse {os}
    },
    packageOptions in packageScala := (packageOptions in (Compile, packageBin)).value,
    packageOptions in packageDependency := (packageOptions in (Compile, packageBin)).value,

    // ouputPath
    outputPath in assembly <<= (target in assembly, jarName in assembly) map { (t, s) => t / s },
    outputPath in packageScala <<= (target in assembly, jarName in packageScala) map { (t, s) => t / s },
    outputPath in packageDependency <<= (target in assembly, jarName in packageDependency) map { (t, s) => t / s },
    target in assembly <<= crossTarget,

    jarName in assembly <<= (jarName in assembly) or (defaultJarName in assembly),
    jarName in packageScala <<= (jarName in packageScala) or (defaultJarName in packageScala),
    jarName in packageDependency <<= (jarName in packageDependency) or (defaultJarName in packageDependency),

    defaultJarName in packageScala <<= (scalaVersion) map { (scalaVersion) => "scala-library-" + scalaVersion + "-assembly.jar" },
    defaultJarName in packageDependency <<= (name, version) map { (name, version) => name + "-assembly-" + version + "-deps.jar" },
    defaultJarName in assembly <<= (name, version) map { (name, version) => name + "-assembly-" + version + ".jar" },
    
    mainClass in assembly <<= mainClass or (mainClass in Runtime),
    
    fullClasspath in assembly <<= fullClasspath or (fullClasspath in Runtime),
    
    externalDependencyClasspath in assembly <<= externalDependencyClasspath or (externalDependencyClasspath in Runtime)
  )
  
  lazy val assemblySettings: Seq[sbt.Def.Setting[_]] = baseAssemblySettings
}

case class AssemblyOption(assemblyDirectory: File,
  // include compiled class files from itself or subprojects
  includeBin: Boolean = true,
  includeScala: Boolean = true,
  // include class files from external dependencies
  includeDependency: Boolean = true,
  excludedJars: Classpath = Nil,
  excludedFiles: Seq[File] => Seq[File] = Assembly.defaultExcludedFiles, // use mergeStrategy instead
  mergeStrategy: String => MergeStrategy = MergeStrategy.defaultMergeStrategy,
  cacheOutput: Boolean = true,
  cacheUnzip: Boolean = true,
  appendContentHash: Boolean = false,
  prependShellScript: Option[Seq[String]] = None)

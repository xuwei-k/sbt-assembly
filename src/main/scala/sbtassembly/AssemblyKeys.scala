package sbtassembly

import sbt._
import Keys._

trait AssemblyKeys {
  lazy val assembly          = taskKey[File]("Builds a deployable fat jar.")
  lazy val assembleArtifact  = settingKey[Boolean]("Enables (true) or disables (false) assembling an artifact.")
  lazy val assemblyOption    = taskKey[AssemblyOption]("Configuration for making a deployable fat jar.")
  lazy val assembledMappings = taskKey[Seq[MappingSet]]("Keeps track of jar origins for each source")

  lazy val packageScala      = TaskKey[File]("assembly-package-scala", "Produces the scala artifact.")
  lazy val packageDependency = TaskKey[File]("assembly-package-dependency", "Produces the dependency artifact.")
  lazy val jarName           = TaskKey[String]("assembly-jar-name")
  lazy val defaultJarName    = TaskKey[String]("assembly-default-jar-name")
  lazy val outputPath        = TaskKey[File]("assembly-output-path")
  lazy val excludedJars      = TaskKey[Classpath]("assembly-excluded-jars")
  lazy val mergeStrategy     = SettingKey[String => MergeStrategy]("assembly-merge-strategy", "mapping from archive member path to merge strategy")
}
object AssemblyKeys extends AssemblyKeys

// Keep track of the source package of mappings that come from a jar, so we can
// sha1 the jar instead of the unpacked packages when determining whether to rebuild
case class MappingSet(sourcePackage : Option[File], mappings : Vector[(File, String)]) {
  def dependencyFiles: Vector[File] =
    sourcePackage match {
      case Some(f)  => Vector(f)
      case None     => mappings.map(_._1)
    }
}

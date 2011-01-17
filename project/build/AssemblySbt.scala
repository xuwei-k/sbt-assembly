import sbt._

class AssemblySbt(info: ProjectInfo) extends PluginProject(info)
                                             with IdeaProject
                                             with maven.MavenDependencies {
  lazy val publishTo = Resolver.sftp("Personal Repo",
                                     "codahale.com",
                                     "/home/codahale/repo.codahale.com/")
}

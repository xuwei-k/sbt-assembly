scalaVersion := "2.12.7"

assemblyShadeRules in assembly := Seq(
  ShadeRule.rename("example.A" -> "example.C").inProject
)

name := "ConsistentHashRouting"

version := "0.1"

scalaVersion := "2.12.8"
scalacOptions += "-Ypartial-unification"

lazy val common =  project.settings(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "1.6.0",
    "org.typelevel" %% "cats-effect" % "1.2.0",
    "org.scalactic" %% "scalactic" % "3.0.5",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test"
  )
)
lazy val consul = project.settings(
  libraryDependencies ++= Seq(
    "com.codacy" % "scala-consul_2.11" % "3.0.1"
  )
).dependsOn(common)
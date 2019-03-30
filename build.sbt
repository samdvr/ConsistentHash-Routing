name := "ConsistentHashRouting"

version := "0.1"

scalaVersion := "2.12.8"
scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.6.0",
  "org.typelevel" %% "cats-effect" % "1.2.0",
  "com.codacy" % "scala-consul_2.11" % "3.0.1"
)
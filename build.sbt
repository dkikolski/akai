ThisBuild / scalaVersion := "3.1.0"

ThisBuild / scalacOptions := Seq(
  "-encoding",
  "UTF-8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings"
)

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / assembly / assemblyJarName := "akai.jar"

ThisBuild / libraryDependencies ++= Seq(
  "org.bouncycastle" % "bcprov-jdk15on" % "1.69",
  "org.scalactic"   %% "scalactic"      % "3.2.10",
  "org.scalatest"   %% "scalatest"      % "3.2.10" % "test"
)
import sbt._
import Keys._

object BuildSettings
{
	val buildSettings = Defaults.coreDefaultSettings ++ Seq(
		name := "GCMN Host Pipe",
		organization := "org.ucworks",
		version := "0.1-SNAPSHOT",

		scalaVersion := "2.11.7"
	)
}

object MyBuild extends Build
{
	import BuildSettings._

	lazy val root: Project = Project(
		"gcmn-host-pipe",
		file("."),
		settings = buildSettings)
}

import sbt._
import sbt.Keys._

object Build extends Build {
	val appScalaVersion = "2.11.7"

	lazy val slickGenerate = Project("slickGenerate", base = file(".")).settings(
		scalaVersion := appScalaVersion,
		libraryDependencies ++= List(
			"com.typesafe.slick"  %% "slick"                  % "3.1.0",
			"com.typesafe.slick"  %% "slick-codegen"          % "3.1.0",
			"com.h2database"      %  "h2"                     % "1.4.190"
    ),
    slick <<= slickCodeGenTask
  )

	lazy val slick = taskKey[Seq[File]]("slick code generation from an existing database")

	lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
		val outputDir = "app"
		val url = "jdbc:h2:tcp://localhost/mem:demo"
		val jdbcDriver = "org.h2.Driver"
		val slickDriver = "slick.driver.H2Driver"
		val pkg = "db"
		toError(r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, outputDir, pkg), s.log))
		val fname = outputDir + "Tables.scala"
		Seq(file(fname))
	}
}
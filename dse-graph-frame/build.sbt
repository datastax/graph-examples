

name := "dse-graph-frame"

version := "0.1"

scalaVersion := "2.11.8"

// Please make sure that following DSE version matches your DSE cluster version.
val dseVersion = "5.1.1"

resolvers += "DataStax Repo" at "https://datastax.artifactoryonline.com/datastax/dse/"
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

// Warning Sbt 0.13.13 or greater is required due to a bug with dependency resolution
// uncomment for 5.1.1
//libraryDependencies += "com.datastax.dse" % "dse-spark-dependencies" % dseVersion % "provided"

// this is 5.1.0 workaround for DSP-13074, DseGraphFrame and graph is not included in dse-spark-dependencies
// you can remove all following code with 5.1.1
// set to full path to dse command if it is not in path
val dseScript="/usr/local/bin/dse"

//get dse to classpath
val dseClasspath = {
  import sys.process._
  val whildCardRegex = """(.*?)\/\*""".r
  Seq(dseScript, "spark-classpath").!!.split(":").map(_.trim).flatMap {
    case name @ whildCardRegex(dir) => new File(dir).listFiles().toSeq
    case name => Seq(new File(name))
  }
}

// Use DSE classpath
// becuase DseGraphFrame is not available as public artifact yet
unmanagedJars in Compile :=  Attributed.blankSeq(dseClasspath)


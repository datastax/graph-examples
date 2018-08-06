name := "dse-graph-frame"

version := "0.1"

scalaVersion := "2.11.8"

// Please make sure that following DSE version matches your DSE cluster version.
val dseVersion = "6.0.2"

resolvers += "DataStax Repo" at "https://repo.datastax.com/public-repos/"

mainClass in (Compile, packageBin) := Some("com.datastax.bdp.graphframe.example.StreamingExample")

// Warning Sbt 0.13.13 or greater is required due to a bug with dependency resolution
libraryDependencies += "com.datastax.dse" % "dse-spark-dependencies" % dseVersion % "provided"
libraryDependencies += "graphframes" % "graphframes_2.11" % "0.5.0.2-spark2.2" % "provided"
libraryDependencies += "org.apache.tinkerpop" % "spark-gremlin" % "3.3.2" % "provided" excludeAll(
  ExclusionRule("com.github.jeremyh"))

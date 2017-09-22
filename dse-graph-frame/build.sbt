name := "dse-graph-frame"

version := "0.1"

scalaVersion := "2.11.8"

// Please make sure that following DSE version matches your DSE cluster version.
val dseVersion = "5.1.2"
val sparkVersion = "2.1.1"

mainClass in (Compile, packageBin) := Some("com.datastax.bdp.graphframe.example.StreamingExample")

// Warning Sbt 0.13.13 or greater is required due to a bug with dependency resolution
libraryDependencies ++= Seq(
  "com.datastax.dse" % "dse-spark-dependencies" % dseVersion % "provided",
  "com.datastax.dse" % "dse-graph-frames" % dseVersion % "provided",
  "org.apache.spark" % "spark-core_2.11" % sparkVersion % "provided",
  "org.apache.spark" % "spark-streaming_2.11" % sparkVersion % "provided",
  "org.apache.spark" % "spark-sql_2.11" % sparkVersion % "provided"
)

resolvers += "DataStax Repo" at "https://repo.datastax.com/public-repos/"
resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
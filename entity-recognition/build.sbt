name := "entity-recognition"

version := "0.1"

scalaVersion := "2.11.8"

// Please make sure that following DSE version matches your DSE cluster version.
val dseVersion = "5.1.2"

resolvers += "DataStax Repo" at "https://repo.datastax.com/public-repos/"

mainClass in (Compile, packageBin) := Some("com.datastax.bdp.er.EntityRecognitionExample")

// Warning Sbt 0.13.13 or greater is required due to a bug with dependency resolution
libraryDependencies += "com.datastax.dse" % "dse-spark-dependencies" % dseVersion % "provided"

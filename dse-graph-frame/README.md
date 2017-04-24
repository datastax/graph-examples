# DseGraphFrame example

Documentation for this code is in a blog entry

* friends-graph.groovy small 3 node graph with a schema
it is used in following examples
* Spark-shell-notes.scala a set of manipulations that could be done in "spark shell" with the graph
Commands could be copy-pasted to the shell
* build.sbt and src/ are Spark streaming example.
It generates random messages stream. The stream is handled and stored in graph properties 
 ```bash
 sbt package 
 dse spark-submit target/scala-2.11/dse-graph-frame_2.11-0.1.jar
 ```


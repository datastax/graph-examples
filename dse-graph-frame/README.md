# DseGraphFrame example

This example is described in the blog post [Introducing DSE Graph Frames](http://www.datastax.com/dev/blog/dse-graph-frame).

`friends-graph.groovy` creates a small 3 vertex graph in DSE Graph. It is used in the following examples.

`Spark-shell-notes.scala` is a set of manipulations that can be done in Spark shell. The graph commands can be copied and pasted to the shell.

`build.sbt` and `src/...` are part of a Spark streaming example.

This generates a random messages stream. The stream is handled and stored in the graph.

 ```bash
 sbt package 
 dse spark-submit target/scala-2.11/dse-graph-frame\_2.11-0.1.jar
 ```
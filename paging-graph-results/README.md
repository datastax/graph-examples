# Paging Graph Results - Java Driver
## Summary
This example repo demonstrates how to use the continuous paging functionality available with DataStax graph to page results 
as they are returned from DataStax Graph.  This repo shows how to use this paging functionality with both the synchronous 
and asynchronous methods to stream back a continuous set of results from long running graph queries.

## Prerequisites

* This only works on the 6.8 Release of DataStax Graph, this was built using the 1.8.2.20190711-LABS Experimental release
* Java 8
* DataStax Enterprise Java Driver for the 1.8.2.20190711-LABS Experimental 6.8 DataStax Graph Release 
* Maven
* A DataStax Core Graph created and configured on your 6.8 cluster

## Building and Running

In order to build this we need to run a Maven build using:

```mvn clean package```

This will build and package a fat jar.  Once completed you can run this program from the command line using:

```
java -cp ./target/paging-graph-results-0.1-jar-with-dependencies.jar com.datastax.graphpractice.example.App <insert cluster contact point> <insert graph name>
```

# Entity Recognition Example

The schema contains two vertices label: person with pasport_id and name as id and master.
The goal of algorithms is to add edges from person nodes to one master node if they have
the same passport id, is used, i. e.
 
`(person, passport_id =1, name=Artem) -> (master, passport_id=1, id=xxxx) <-(person, passport_id=1, name=Артем)`

## Running the application example:

`schema.groovy` create empty scheam for simple passport-base ER grpah
run it with gremlin-console

    `dse gremlin-console -e schema.groovy`

`data` directory contains 3 data files. Put them to the shared file system.
    The default location is `dsefs:///tmp`

 ```bash
dse hadoop fs -mkdir dsefs:///tmp
dse hadoop fs -put data/initial.csv data/add_1.csv data/add_2.csv dsefs:///tmp
```

if you test on single node cluster you can pass local filesystem directory with `-d file:///tmp`

Start the example. spark.sql.crossJoin.enabled is needed only for cartesianRecognizer 

 ```bash
 sbt package 
 dse spark-submit --conf spark.sql.crossJoin.enabled=true target/scala-2.11/entity-recognition_2.11-0.1.jar
 ```
 
see results in gremlin-console or studio
 
 ## Running streaming example:

Streaming example generate random passport id and names, so you'll finally will get a really big graph
 ```bash
 dse spark-submit --conf spark.sql.crossJoin.enabled=true --class com.datastax.bdp.er.streaming.StreamingExample target/scala-2.11/entity-recognition_2.11-0.1.jar
```
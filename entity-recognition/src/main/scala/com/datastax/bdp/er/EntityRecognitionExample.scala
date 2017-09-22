package com.datastax.bdp.er

import java.util.UUID

import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import com.datastax.bdp.graph.spark.graphframe._
import com.datastax.driver.dse.DseSession
import com.datastax.driver.dse.graph.SimpleGraphStatement
import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.storage.StorageLevel

object EntityRecognitionExample {
  val graphName = "entity"
  var dataDir = "dsefs:///tmp"

  def usage(): Unit = {
    println("Usage: dse spark-submit --conf spark.sql.crossJoin.enabled=true dse spark-submit --conf spark.sql.crossJoin.enabled=true target/scala-2.11/entity-recognition_2.11-0.1.jar <options>")
    println("    -d <directory>     provide directory url with data example files, default is dsefs:///tmp")
    System.exit(-1)
  }

  def main(args: Array[String]) {

    // parse args
    args.sliding(2, 2).toList.collect {
      case Array("-d", dir: String) => dataDir = dir
      case Array("--data-directory", dir: String) => dataDir = dir
      case Array("-h") => usage()
      case Array("--help") => usage()
    }

    val spark = SparkSession.builder.appName("Load Data Application").getOrCreate()

    val g = spark.dseGraph(graphName)

    // bulk entity recognition with simple eq join entity recognition
    // similar approach will be used to merge two graphs.
    val personCsv = spark.read.csv(s"$dataDir/initial.csv").toDF("passport_id", "name")
    loadWithSimpleJoinRecognizer(g, personCsv)

    // gremlin OLTP base incremental update
    // entity recognition with gremlin query
    // that should be output of the ML data model
    val searchQuery =
    """find = g.V().has("master", "passport_id", passport_id);"""
    val add1PersonCsv = spark.read.csv(s"$dataDir/add_1.csv").toDF("passport_id", "name")
    gremlinRecognizer(add1PersonCsv, searchQuery)

    // n^2 search with universal compare function
    // custom predicate for cartesian joinWith
    val compareUDF = udf((person: Row, entity: Row) => person.getAs[String]("passport_id") == entity.getAs[String]("passport_id") )

    val add2PersonCsv = spark.read.csv(s"$dataDir/add_2.csv").toDF("passport_id", "name")
    cartesianRecognizer(g, add2PersonCsv, compareUDF)

    spark.stop()
    System.exit(0)
  }

  val generateUUID = udf(() => UUID.randomUUID().toString)


  /**
    *  bulk entity recognition with simple join entity recognition
    *
    * @param g
    */
  def loadWithSimpleJoinRecognizer(g: DseGraphFrame, persons: DataFrame) = {
    val personDF = persons.withColumn("~label", lit("person"))

    // that df is persisted  to fix generated ids.
    val entityDF = persons.toDF("passport_id", "name")
      .select("passport_id").distinct()
      .withColumn("~label", lit("master"))
      .withColumn("entity_id", generateUUID()).persist(StorageLevel.MEMORY_AND_DISK)

    // load new persons
    g.updateVertices(personDF)
    g.updateVertices(entityDF)

    //generate edges
    val edgeDF = g.V.hasLabel("master").df.select(col("id") as "dst", col("passport_id"))
      .join(g.V.hasLabel("person").df.select(col("id") as "src", col("passport_id")), "passport_id")
    val e = edgeDF.select(col("src"), col("dst"), lit("is") as "~label")
    g.updateEdges(e)
  }

  /**
    * use gremlin queries to find appropriate entities, best for quries that use indexes and solr/search queries
    * Should be used in case of incremental updates with relatively small number of entries at once.
    * @param newPerson
    */
  def gremlinRecognizer(newPerson: DataFrame, searchQuery: String) = {

    val updateQuery =
      //create new person node
        searchQuery +
        """person = graph.addVertex(T.label, "person", "passport_id", passport_id, "name", name);
        // create master node if needed and add edge
        entity = find.tryNext().orElseGet{ graph.addVertex(T.label, "master" , "entity_id", java.util.UUID.randomUUID().toString(), "passport_id", passport_id) };
        person.addEdge("is", entity);
        """
    val connector = CassandraConnector(newPerson.sparkSession.sparkContext.getConf)
    newPerson.foreachPartition(rows => {
      connector.withSessionDo( {s =>
        val session = s.asInstanceOf[DseSession]
        for (row <- rows) {
          val passportId = row.getString(0)
          val name = row.getString(1)

          //val s = new SimpleGraphStatement(searchQuery).set("passport_id", passportId).setGraphName(graphName)
          //println(session.executeGraph(s).all())

          val stmt = new SimpleGraphStatement(updateQuery)
            .set("passport_id", passportId)
            .set("name", name)
            .setGraphName(graphName)
          session.executeGraph(stmt).all()

        }
      })
    })
  }

  /**
    *  n*m search with universal compare function
    *  cartesian join with any custom predicate.
    *  The algorithm does not use any DSE index, but brute force compare n master nodes with provided m new records.
    *  This approach is best for Machine learning models that has sophisticated internal logic
    *
    * @param persons dataframe with new person nodes
    * @param compareUDF the predicate that takes to parameters: master node row and new person row, in case it returns true new edge will be added
    **/
  def cartesianRecognizer(g: DseGraphFrame, persons: DataFrame, compareUDF: UserDefinedFunction): Unit = {
    val entityDF =  g.V.hasLabel("master")
    val personDF = persons.withColumn("~label", lit("person"))
    g.updateVertices(personDF)

    // find existing connection. get tuple (entityStruct, personStruct)
    val edgeDF: Dataset[(Row, Row)] = personDF.joinWith(entityDF, compareUDF(col("_2"), col("_1")), "left")

    // update edges to existing entities
    g.updateEdges(edgeDF.filter(col("_2").isNotNull)
      .select(g.idColumn(lit("person"), col("_1.passport_id"),  col("_1.name")) as "src", col("_2.id") as "dst", lit("is") as "~label"))

    // person that has no edges yet
    val newPersons = edgeDF.filter(col("_2").isNull).select("_1.*")

    // add new entities for it
    val newEntityDF = newPersons
      .select(col("passport_id"), g.idColumn(lit("person"), col("passport_id"), col("name")) as "src")
      .withColumn("~label", lit("master"))
      .withColumn("entity_id", generateUUID()).persist(StorageLevel.MEMORY_AND_DISK)
    g.updateVertices(newEntityDF.drop("src"))

    // add new edges
    val newEdges = newEntityDF.select(col("src"), g.idColumn(lit("master"), col("entity_id")) as "dst", lit("is") as "~label")
    g.updateEdges(newEdges)

  }

}

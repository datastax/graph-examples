package com.datastax.bdp.er.streaming

import com.datastax.bdp.er.EntityResolutionExample
import com.datastax.bdp.graph.spark.graphframe._
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Streaming example that read messages from RandomReceiver and store them in "messages"  vertex property
  */

object StreamingExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .config("spark.cleaner.ttl", "3600")
      .config("spark.streaming.stopGracefullyOnShutdown","false").getOrCreate()
    import spark.implicits._

    val graph = spark.dseGraph(EntityResolutionExample.graphName)

    val ssc = new StreamingContext(spark.sparkContext, Seconds(1))
    val rec = new RandomReceiver(PassportNumber = 200, NameNumber = 200)
    val dstream = ssc.receiverStream(rec)

    dstream.foreachRDD(rdd => {
      // convert RDD to proper DF
      val updateDF = rdd.toDF("passport_id", "name")
      // update properties
      val compareUDF = udf((person: Row, entity: Row) => person.getAs[String]("passport_id") == entity.getAs[String]("passport_id") )
      EntityResolutionExample.cartesianRecognizer(graph,updateDF, compareUDF)

      /* uncomment to switch to gremlin base code.
      val searchQuery =
        """find = g.V().has("master", "passport_id", passport_id);"""
      EntityResolutionExample.gremlinRecognizer(updateDF, searchQuery)
      */
    })

    // debug print received events
    dstream.print()
    ssc.start()
    ssc.awaitTermination()
    rec.stop()
    System.exit(0)
  }
}

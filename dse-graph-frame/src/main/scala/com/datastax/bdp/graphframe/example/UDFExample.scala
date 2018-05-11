package com.datastax.bdp.graphframe.example

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import com.datastax.bdp.graph.spark.graphframe._

/**
  * Streaming example that read messages from RandomReceiver and store them in "messages"  vertex property
  */

object UDFExample {

  def createJSON (row :Row):String = { "id: " + row.getString(0)}
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .config("spark.cleaner.ttl", "3600")
      .config("spark.streaming.stopGracefullyOnShutdown","true").getOrCreate()
    import spark.implicits._

    val g = spark.dseGraph("test")
    //def createJSON (row :Row):String = { "id: " + row.getString(0)}
    val udfJSON=udf(createJSON _)
    g.V().df.select(udfJSON(struct($"*"))).show
    spark.stop()
  }
}

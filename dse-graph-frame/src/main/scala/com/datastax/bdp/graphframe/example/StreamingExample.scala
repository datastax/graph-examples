package com.datastax.bdp.graphframe.example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import org.apache.spark.streaming.{Seconds, StreamingContext}
import com.datastax.bdp.graph.spark.graphframe._

/**
  * Streaming example that read messages from RandomReceiver and store them in "messages"  vertex property
  */

object StreamingExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .config("spark.cleaner.ttl", "3600")
      .config("spark.streaming.stopGracefullyOnShutdown","true").getOrCreate()
    import spark.implicits._

    val graph = spark.dseGraph("test")

    val ssc = new StreamingContext(spark.sparkContext, Seconds(1))
    val rec = new RandomReceiver(Seq("1", "2", "3"))
    val dstream = ssc.receiverStream(rec)

    dstream.foreachRDD(rdd => {
      // convert RDD to proper DF
      val updateDF = rdd.toDF("_id", "messages").withColumn("~label", lit("person"))
      // update properties
      graph.updateVertices(updateDF)
    })

    // debug print received events
    dstream.print()
    ssc.start()
    ssc.awaitTermination()
    rec.stop()
  }
}

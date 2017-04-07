package com.datastax.bdp.graphframe.example

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver

import scala.util.Random
/**
  * Generate random string events for given set of String id.
  */
class RandomReceiver (val ids:Seq[String]) extends Receiver[(String, String)](StorageLevel.MEMORY_ONLY) {

  var stopped = false

  override def onStart(): Unit = {
    new Thread(new Runnable() {
      override def run() {
        try {
          while (!stopped) {
            Thread.sleep(100)
            val msg: (String, String) = (ids(Random.nextInt(ids.length)), s"Hello #${Random.nextInt(100)}")
            store(msg)
          }
        } catch {
          case e: InterruptedException => println("Interrupted")
        }
      }
    },
      "RandomReceiver").start();
  }

  override def onStop(): Unit = {
    stopped = true
  }

  def stop(): Unit = {
    stopped = true
  }
}
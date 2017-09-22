package com.datastax.bdp.er.streaming

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver

import scala.util.Random
/**
  * Generate random string events for given set of String id.
  */
class RandomReceiver (val PassportNumber:Int = 10000, val NameNumber:Int = 1000) extends Receiver[(String, String)](StorageLevel.MEMORY_ONLY) {

  var stopped = false

  override def onStart(): Unit = {
    new Thread(new Runnable() {
      override def run() {
        try {
          while (!stopped) {
            val msg: (String, String) = (Random.nextInt(PassportNumber).toString, s"Person #${Random.nextInt(NameNumber)}")
            store(msg)
            Thread.sleep(100)
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
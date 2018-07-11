package com.datastax.fraud

import com.datastax.bdp.graph.spark.graphframe._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._


object DataImport {
  def main(args: Array[String]):Unit = {

    val graphName = "fraud"

    val spark = SparkSession
      .builder
      .appName("Data Import Application")
      .enableHiveSupport()
      .getOrCreate()

    val g = spark.dseGraph(graphName)

    // Create Schemas for DataSets
    def customerSchema():StructType = {
      StructType(Array(
        StructField("customerid", StringType, true),
        StructField("firstname", StringType, true),
        StructField("lastname", StringType, true),
        StructField("email", StringType, true),
        StructField("address", StringType, true),
        StructField("city", StringType, true),
        StructField("state", StringType, true),
        StructField("postalcode", StringType, true),
        StructField("countrycode", StringType, true),
        StructField("phone", StringType, true),
        StructField("createdtime", TimestampType, true)
      ))
    }

    def sessionSchema():StructType = {
      StructType(Array(
        StructField("sessionid", StringType, true),
        StructField("deviceid", StringType, true),
        StructField("ipaddress", StringType, true),
        StructField("createdtime", TimestampType, true)
      ))
    }

    def orderSchema():StructType = {
      StructType(Array(
        StructField("orderid", StringType, true),
        StructField("createdtime", TimestampType, true),
        StructField("outcome", StringType, true),
        StructField("creditcardhashed", StringType, true),
        StructField("ipaddress", StringType, true),
        StructField("amount", DoubleType, true),
        StructField("deviceid", StringType, true)
      ))
    }

    def chargebackSchema():StructType = {
      StructType(Array(
        StructField("chargebacknumber", IntegerType, true),
        StructField("amount", DoubleType, true),
        StructField("createdtime", TimestampType, true),
        StructField("creditcardhashed", StringType, true)
      ))
    }

    def addressSchema():StructType = {
      StructType(Array(
        StructField("customerid", StringType, true),
        StructField("address", StringType, true),
        StructField("city", StringType, true),
        StructField("state", StringType, true),
        StructField("postalcode", StringType, true),
        StructField("countrycode", StringType, true)
      ))
    }

    // Read CSV Files (unless we need specific data types, we will infer the schema)
    val customers = spark.sqlContext.read.format("csv").option("header", "true").option("delimiter", "|").schema(customerSchema()).load("dsefs:///data/customers.csv")
    val sessions = spark.sqlContext.read.format("csv").option("header", "true").option("delimiter", "|").schema(sessionSchema()).load("dsefs:///data/sessions.csv")
    val orders = spark.sqlContext.read.format("csv").option("header", "true").option("delimiter", "|").schema(orderSchema()).load("dsefs:///data/orders.csv")
    val chargebacks = spark.sqlContext.read.format("csv").option("header", "true").option("delimiter", "|").schema(chargebackSchema()).load("dsefs:///data/chargebacks.csv")
    val creditcards = spark.sqlContext.read.format("csv").option("header", "true").option("delimiter", "|").option("inferSchema", "true").load("dsefs:///data/creditCards.csv")
    val devices = spark.sqlContext.read.format("csv").option("header", "true").option("delimiter", "|").option("inferSchema", "true").load("dsefs:///data/devices.csv")

    val customerOrders = spark.sqlContext.read.format("csv").option("header", "true").option("delimiter", "|").option("inferSchema", "true").load("dsefs:///data/customerOrders.csv")
    val orderChargebacks = spark.sqlContext.read.format("csv").option("header", "true").option("delimiter", "|").option("inferSchema", "true").load("dsefs:///data/orderChargebacks.csv")
    val customerSessions = spark.sqlContext.read.format("csv").option("header", "true").option("delimiter", "|").option("inferSchema", "true").load("dsefs:///data/customerSessions.csv")
    val customerChargebacks = spark.sqlContext.read.format("csv").option("header", "true").option("delimiter", "|").option("inferSchema", "true").load("dsefs:///data/customerChargebacks.csv")
    val customerAddresses = spark.sqlContext.read.format("csv").option("header", "true").option("delimiter", "|").schema(addressSchema()).load("dsefs:///data/customerAddresses.csv")

    // WRITE OUT VERTICES
    println("\nWriting customer vertices")
    g.updateVertices(customers.withColumn("~label", lit("customer")))

    println("\nWriting session vertices")
    g.updateVertices(sessions.withColumn("~label", lit("session")))

    println("\nWriting order vertices")
    g.updateVertices(orders.withColumn("~label", lit("order")))

    println("\nWriting chargeback vertices")
    g.updateVertices(chargebacks.withColumn("~label", lit("chargeback")))

    println("\nWriting creditCard vertices")
    g.updateVertices(creditcards.withColumn("~label", lit("creditCard")))

    println("\nWriting device vertices")
    g.updateVertices(devices.withColumn("~label", lit("device")))

    println("\nWriting customer addresses")
    val addressVertices = customerAddresses.withColumn("~label", lit("address"))
    // Address has a composite column and requires the idColumn syntax
    g.updateVertices(addressVertices.select(
      g.idColumn(
        col("~label"),
        col("address"),
        col("postalcode")
      ) as "id",
      col("city"),
      col("state"),
      col("countrycode"),
      col("address"),
      col("postalcode"))
    )

    // WRITE OUT EDGES
    println("\nWriting customer places order edges")
    val customerOrderEdges = customerOrders.withColumn("srcLabel", lit("customer")).withColumn("dstLabel", lit("order")).withColumn("edgeLabel", lit("places"))
    g.updateEdges(customerOrderEdges.select(g.idColumn(col("srcLabel"), col("customerid")) as "src", g.idColumn(col("dstLabel"), col("orderid")) as "dst", col("edgeLabel") as "~label"))

    println("\nWriting order uses card edges")
    val orderUsesCardEdges = orders.withColumn("srcLabel", lit("order")).withColumn("dstLabel", lit("creditCard")).withColumn("edgeLabel", lit("usesCard"))
    g.updateEdges(orderUsesCardEdges.select(g.idColumn(col("srcLabel"), col("orderid")) as "src", g.idColumn(col("dstLabel"), col("creditcardhashed")) as "dst", col("edgeLabel") as "~label"))

    println("\nWriting order results in chargebacks edges")
    val orderChargebackEdges = orderChargebacks.withColumn("srcLabel", lit("order")).withColumn("dstLabel", lit("chargeback")).withColumn("edgeLabel", lit("resultsIn"))
    g.updateEdges(orderChargebackEdges.select(g.idColumn(col("srcLabel"), col("orderid")) as "src", g.idColumn(col("dstLabel"), col("chargebacknumber")) as "dst", col("edgeLabel") as "~label"))

    println("\nWriting chargeback from card edges")
    val chargebackFromCardEdges = chargebacks.withColumn("srcLabel", lit("chargeback")).withColumn("dstLabel", lit("creditCard")).withColumn("edgeLabel", lit("fromCard"))
    g.updateEdges(chargebackFromCardEdges.select(g.idColumn(col("srcLabel"), col("chargebacknumber")) as "src", g.idColumn(col("dstLabel"), col("creditcardhashed")) as "dst", col("edgeLabel") as "~label"))

    println("\nWriting customer logs into session edges")
    val customerSessionEdges = customerSessions.withColumn("srcLabel", lit("customer")).withColumn("dstLabel", lit("session")).withColumn("edgeLabel", lit("logsInto"))
    g.updateEdges(customerSessionEdges.select(g.idColumn(col("srcLabel"), col("customerid")) as "src", g.idColumn(col("dstLabel"), col("sessionid")) as "dst", col("edgeLabel") as "~label"))

    println("\nWriting customer charged with chargeback edges")
    val customerChargebackEdges = customerChargebacks.withColumn("srcLabel", lit("customer")).withColumn("dstLabel", lit("chargeback")).withColumn("edgeLabel", lit("chargedWith"))
    g.updateEdges(customerChargebackEdges.select(g.idColumn(col("srcLabel"), col("customerid")) as "src", g.idColumn(col("dstLabel"), col("chargebacknumber")) as "dst", col("edgeLabel") as "~label"))

    println("\nWriting session using device edges")
    val sessionUsingDeviceEdges = sessions.withColumn("srcLabel", lit("session")).withColumn("dstLabel", lit("device")).withColumn("edgeLabel", lit("using"))
    g.updateEdges(sessionUsingDeviceEdges.select(g.idColumn(col("srcLabel"), col("sessionid")) as "src", g.idColumn(col("dstLabel"), col("deviceid")) as "dst", col("edgeLabel") as "~label"))

    println("\nWriting order using device edges")
    val orderUsingDeviceEdges = orders.withColumn("srcLabel", lit("order")).withColumn("dstLabel", lit("device")).withColumn("edgeLabel", lit("using"))
    g.updateEdges(orderUsingDeviceEdges.select(g.idColumn(col("srcLabel"), col("orderid")) as "src", g.idColumn(col("dstLabel"), col("deviceid")) as "dst", col("edgeLabel") as "~label"))

    println("\nWriting customer has address edges")
    val customerAddressEdges = customerAddresses.withColumn("srcLabel", lit("customer")).withColumn("dstLabel", lit("address")).withColumn("edgeLabel", lit("hasAddress"))
    // This edge connects a to a vertex label(address) that has a composite column
    g.updateEdges(customerAddressEdges.select(
      g.idColumn(
        col("srcLabel"),
        col("customerid")
      ) as "src",
      g.idColumn(
        col("dstLabel"),
        col("address"),
        col("postalcode")
      ) as "dst",
      col("edgeLabel") as "~label")
    )

    System.exit(0)
  }
}
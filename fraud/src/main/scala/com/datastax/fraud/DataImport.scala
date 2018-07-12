package com.datastax.fraud

import com.datastax.bdp.graph.spark.graphframe._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._


object DataImport {
  def main(args: Array[String]):Unit = {

    val graphName = "fraud"
    val source = "file"
//    val source = "db"
    val dbHost = "localhost"
    val dbName = "fraud"
    val dbUser = "root"
    val dbPassword = "foo"

    val spark = SparkSession
      .builder
      .appName("Data Import Application")
      .enableHiveSupport()
      .getOrCreate()

    val g = spark.dseGraph(graphName)

    var customers:DataFrame = null
    var sessions:DataFrame = null
    var orders:DataFrame = null
    var chargebacks:DataFrame = null
    var creditcards:DataFrame = null
    var devices:DataFrame = null

    var customerOrders:DataFrame = null
    var orderChargebacks:DataFrame = null
    var customerSessions:DataFrame = null
    var customerChargebacks:DataFrame = null
    var customerAddresses:DataFrame = null

    if (source == "file") {

      // Create Schemas for DataSets where explicit types are necessary
      // (Sometimes inferring the schema doesn't yield the correct type)
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

      // If we try to infer the schema, it thinks the postal code is an integer
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

      customers = spark.read.format("csv").option("header", "true").option("delimiter", "|").schema(customerSchema()).load("dsefs:///data/customers.csv")
      sessions = spark.read.format("csv").option("header", "true").option("delimiter", "|").schema(sessionSchema()).load("dsefs:///data/sessions.csv")
      orders = spark.read.format("csv").option("header", "true").option("delimiter", "|").schema(orderSchema()).load("dsefs:///data/orders.csv")
      chargebacks = spark.read.format("csv").option("header", "true").option("delimiter", "|").schema(chargebackSchema()).load("dsefs:///data/chargebacks.csv")
      creditcards = spark.read.format("csv").option("header", "true").option("delimiter", "|").option("inferSchema", "true").load("dsefs:///data/creditCards.csv")
      devices = spark.read.format("csv").option("header", "true").option("delimiter", "|").option("inferSchema", "true").load("dsefs:///data/devices.csv")

      customerOrders = spark.read.format("csv").option("header", "true").option("delimiter", "|").option("inferSchema", "true").load("dsefs:///data/customerOrders.csv")
      orderChargebacks = spark.read.format("csv").option("header", "true").option("delimiter", "|").option("inferSchema", "true").load("dsefs:///data/orderChargebacks.csv")
      customerSessions = spark.read.format("csv").option("header", "true").option("delimiter", "|").option("inferSchema", "true").load("dsefs:///data/customerSessions.csv")
      customerChargebacks = spark.read.format("csv").option("header", "true").option("delimiter", "|").option("inferSchema", "true").load("dsefs:///data/customerChargebacks.csv")
      customerAddresses = spark.read.format("csv").option("header", "true").option("delimiter", "|").schema(addressSchema()).load("dsefs:///data/customerAddresses.csv")

    } else if (source == "db") {

      // Read from MySQL
      val connection="jdbc:mysql://" + dbHost + "/" + dbName
      val mysqlProps = new java.util.Properties
      mysqlProps.setProperty("user", dbUser)
      mysqlProps.setProperty("password", dbPassword)

      customers = spark.read.jdbc(connection,"customers",mysqlProps)
      sessions = spark.read.jdbc(connection,"sessions",mysqlProps)
      orders = spark.read.jdbc(connection, "orders", mysqlProps)
      chargebacks = spark.read.jdbc(connection, "chargebacks", mysqlProps)
      creditcards = spark.read.jdbc(connection, "creditcards", mysqlProps)
      devices = spark.read.jdbc(connection, "devices", mysqlProps)

      customerOrders = spark.read.jdbc(connection, "customer_orders", mysqlProps)
      orderChargebacks = spark.read.jdbc(connection, "order_chargebacks", mysqlProps)
      customerSessions = spark.read.jdbc(connection, "customer_sessions", mysqlProps)
      customerChargebacks = spark.read.jdbc(connection, "customer_chargebacks", mysqlProps)
      customerAddresses = spark.read.jdbc(connection, "customer_addresses", mysqlProps)

    } else {
      throw new Exception("Source \"" + source + "\" is not valid.")
    }

    // Write out vertices
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
    // Address has a composite key (address, postalcode) and requires the idColumn syntax
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

    // Write out edges
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
    // This edge connects a to a vertex label (address) that has a composite key (address, postalcode)
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
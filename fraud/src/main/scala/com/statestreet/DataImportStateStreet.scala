package com.statestreet

import com.datastax.bdp.graph.spark.graphframe._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.Row


object DataImportStateStreet {
  def main(args: Array[String]):Unit = {

    val graphName = "statestreet"
    val inputPath = "dsefs:///statestreet/"
    // We're trying to avoid DSP-17870 where we don't evict the cache causing OOMs
    val cache = false
    val labelStr = "~label"
    val useNewAPI = {
      if (args.size == 0) {
        true
      } else {
        args(0) match {
          case "new" => true
          case _ => false
        }
      }
    }

    val runFullTest = {
      if (args.size == 0) {
        true
      } else {
        args(1) match {
          case "full" => true
          case _ => false
        }
      }
    }

    val spark = SparkSession
      .builder
      .appName("Data Import Application")
      .enableHiveSupport()
      .getOrCreate()

    val g = spark.dseGraph(graphName)

    // Create Schemas for DataSets where explicit types are necessary
    // (Sometimes inferring the schema doesn't yield the correct type)
    val superparentSchema:StructType = {
      StructType(Array(
        StructField("superparent_id", LongType, false),
        StructField("superparent_name", StringType, true),
        StructField("codeA", StringType, true),
        StructField("vtype", StringType, true),
        StructField("codeB", StringType, true),
        StructField("gencode", StringType, true)
      ))
    }

    val parentSchema:StructType = {
      StructType(Array(
        StructField("parent_id", LongType, false),
        StructField("parent_name", StringType, true),
        StructField("domicile", StringType, true),
        StructField("vtype", StringType, true),
        StructField("codeA", StringType, true)
      ))
    }

    val semiparentSchema:StructType = {
      StructType(Array(
        StructField("semiparent_id", LongType, false),
        StructField("semiparent_name", StringType, true),
        StructField("vtype", StringType, true),
        StructField("codeA", StringType, true),
        StructField("codeB", StringType, true),
        StructField("strategy", StringType, true)
      ))
    }

    val subparentSchema:StructType = {
      StructType(Array(
        StructField("subparent_id", LongType, false),
        StructField("subparent_name", StringType, true),
        StructField("vtype", StringType, true),
        StructField("codeA", StringType, true),
        StructField("codeB", StringType, true),
        StructField("strategy", StringType, true)
      ))
    }

    val topchildSchema:StructType = {
      StructType(Array(
        StructField("topchild_id", LongType, false),
        StructField("topchild_name", StringType, true),
        StructField("family", StringType, true),
        StructField("child_vtype", StringType, true),
        StructField("intcode", LongType, true),
        StructField("class", StringType, true),
        StructField("region", StringType, true),
        StructField("codeA", StringType, true)
      ))
    }

    val childnumsSchema:StructType = {
      StructType(Array(
        StructField("childnums_id", LongType, false),
        StructField("childnums_name", StringType, true),
        StructField("numsvtype", StringType, true)
      ))
    }

    val numsyearSchema:StructType = {
      StructType(Array(
        StructField("numsyear_id", LongType, false),
        StructField("vdate", StringType, true)
      ))
    }

    val numsmonthSchema:StructType = {
      StructType(Array(
        StructField("numsmonth_id", LongType, false),
        StructField("vdate", StringType, true)
      ))
    }

    val numsSchema:StructType = {
      StructType(Array(
        StructField("nums_id", LongType, false),
        StructField("vdate", StringType, true),
        StructField("number", LongType, true)
      ))
    }

    val childitemSchema:StructType = {
      StructType(Array(
        StructField("childitem_id", LongType, false),
        StructField("childitem_name", StringType, true),
        StructField("trade", StringType, true),
        StructField("vtype", StringType, true),
        StructField("region", StringType, true)
      ))
    }

    val valyearSchema:StructType = {
      StructType(Array(
        StructField("valyear_id", LongType, false),
        StructField("vdate", StringType, true)
      ))
    }

    val valmonthSchema:StructType = {
      StructType(Array(
        StructField("valmonth_id", LongType, false),
        StructField("vdate", StringType, true)
      ))
    }

    val valsSchema:StructType = {
      StructType(Array(
        StructField("vals_id", LongType, false),
        StructField("vdate", StringType, true),
        StructField("vvalue", DoubleType, true)
      ))
    }

    var superparentDF:DataFrame = spark.read.format("csv").option("header", "true").schema(superparentSchema).load(inputPath + "superparent.csv")
    var parentDF:DataFrame = spark.read.format("csv").option("header", "true").schema(parentSchema).load(inputPath + "parent.csv")
    var semiparentDF:DataFrame = spark.read.format("csv").option("header", "true").schema(semiparentSchema).load(inputPath + "semiparent.csv")
    var subparentDF:DataFrame = spark.read.format("csv").option("header", "true").schema(subparentSchema).load(inputPath + "subparent.csv")
    var topchildDF:DataFrame = spark.read.format("csv").option("header", "true").schema(topchildSchema).load(inputPath + "topchild.csv")
    var childnumsDF:DataFrame = spark.read.format("csv").option("header", "true").schema(childnumsSchema).load(inputPath + "childnums.csv")
    var numsyearDF:DataFrame = spark.read.format("csv").option("header", "true").schema(numsyearSchema).load(inputPath + "numsyear.csv")
    var numsmonthDF:DataFrame = spark.read.format("csv").option("header", "true").schema(numsmonthSchema).load(inputPath + "numsmonth.csv")
    var numsDF:DataFrame = spark.read.format("csv").option("header", "true").schema(numsSchema).load(inputPath + "nums.csv")
    var childitemDF:DataFrame = spark.read.format("csv").option("header", "true").schema(childitemSchema).load(inputPath + "childitem.csv")
    var valyearDF:DataFrame = spark.read.format("csv").option("header", "true").schema(valyearSchema).load(inputPath + "valyear.csv")
    var valmonthDF:DataFrame = spark.read.format("csv").option("header", "true").schema(valmonthSchema).load(inputPath + "valmonth.csv")
    var valsDF:DataFrame = spark.read.format("csv").option("header", "true").schema(valsSchema).load(inputPath + "values.csv")

    val schema = StructType(List(StructField("Element", StringType, true), StructField("Runtime(sec)", StringType, true)))

    var runtimes = spark.createDataFrame(spark.sparkContext.makeRDD(List(Row(null,null))), schema).na.drop
    val totalTimeStart = System.currentTimeMillis

    def timeIt[T](test: String, code: => T): T = {
      val start = System.currentTimeMillis
      try {
        code
      } finally {
        val end = System.currentTimeMillis
        val runtimeInSec = (end - start)/1000.0
        val newRow = spark.createDataFrame(spark.sparkContext.makeRDD(List(Row(test, runtimeInSec.toString))), schema)
        runtimes = runtimes.union(newRow)
      }
    }

    // Write out vertices
    if (useNewAPI) {
      println("\nUsing the new API")

      println("\nWriting valmonth vertices")
      timeIt("valmonth vertices", g.updateVertices("valmonth", valmonthDF))

      println("\nWriting values vertices")
      timeIt("values vertices", g.updateVertices("vals", valsDF))

      if (runFullTest) {
        println("\nWriting superparent vertices")
        timeIt("superparent vertices", g.updateVertices("superparent", superparentDF))

        println("\nWriting parent vertices")
        timeIt("parent vertices", g.updateVertices("parent", parentDF))

        println("\nWriting semiparent vertices")
        timeIt("semiparent vertices", g.updateVertices("semiparent", semiparentDF))

        println("\nWriting subparent vertices")
        timeIt("subparent vertices", g.updateVertices("subparent", subparentDF))

        println("\nWriting topchild vertices")
        timeIt("topchild vertices", g.updateVertices("topchild", topchildDF))

        println("\nWriting childnums vertices")
        timeIt("childnums vertices", g.updateVertices("childnums", childnumsDF))

        println("\nWriting numsyear vertices")
        timeIt("numsyear vertices", g.updateVertices("numsyear", numsyearDF))

        println("\nWriting numsmonth vertices")
        timeIt("numsmonth vertices", g.updateVertices("numsmonth", numsmonthDF))

        println("\nWriting nums vertices")
        timeIt("nums vertices", g.updateVertices("nums", numsDF))

        println("\nWriting childitem vertices")
        timeIt("childitem vertices", g.updateVertices("childitem", childitemDF))

        println("\nWriting valyear vertices")
        timeIt("valyear vertices", g.updateVertices("valyear", valyearDF))

      }

    } else {
      println("\nUsing the old API")

      println("\nWriting valmonth vertices")
      timeIt("valmonth vertices", g.updateVertices(valmonthDF.withColumn(labelStr, lit("valmonth")), Seq("valmonth"), cache))

      println("\nWriting values vertices")
      timeIt("values vertices", g.updateVertices(valsDF.withColumn(labelStr, lit("vals")), Seq("vals"), cache))

      if (runFullTest) {
        println("\nWriting superparent vertices")
        timeIt("superparent vertices", g.updateVertices(superparentDF.withColumn(labelStr, lit("superparent")), Seq("superparent"), cache))

        println("\nWriting parent vertices")
        timeIt("parent vertices", g.updateVertices(parentDF.withColumn(labelStr, lit("parent")), Seq("parent"), cache))

        println("\nWriting semiparent vertices")
        timeIt("semiparent vertices", g.updateVertices(semiparentDF.withColumn(labelStr, lit("semiparent")), Seq("semiparent"), cache))

        println("\nWriting subparent vertices")
        timeIt("subparent vertices", g.updateVertices(subparentDF.withColumn(labelStr, lit("subparent")), Seq("subparent"), cache))

        println("\nWriting topchild vertices")
        timeIt("topchild vertices", g.updateVertices(topchildDF.withColumn(labelStr, lit("topchild")), Seq("topchild"), cache))

        println("\nWriting childnums vertices")
        timeIt("childnums vertices", g.updateVertices(childnumsDF.withColumn(labelStr, lit("childnums")), Seq("childnums"), cache))

        println("\nWriting numsyear vertices")
        timeIt("numsyear vertices", g.updateVertices(numsyearDF.withColumn(labelStr, lit("numsyear")), Seq("numsyear"), cache))

        println("\nWriting numsmonth vertices")
        timeIt("numsmonth vertices", g.updateVertices(numsmonthDF.withColumn(labelStr, lit("numsmonth")), Seq("numsmonth"), cache))

        println("\nWriting nums vertices")
        timeIt("nums vertices", g.updateVertices(numsDF.withColumn(labelStr, lit("nums")), Seq("nums"), cache))

        println("\nWriting childitem vertices")
        timeIt("childitem vertices", g.updateVertices(childitemDF.withColumn(labelStr, lit("childitem")), Seq("childitem"), cache))

        println("\nWriting valyear vertices")
        timeIt("valyear vertices", g.updateVertices(valyearDF.withColumn(labelStr, lit("valyear")), Seq("valyear"), cache))

      }

    }

    println("\nDone writing vertices")
    println("\nNow writing edges")

    val childitemValyearSchema:StructType = {
      StructType(Array(
        StructField("childitem_id", LongType, false),
        StructField("valyear_id", LongType, false),
        StructField("connect_year", StringType, true)
      ))
    }

    val childnumsChildItemSchema:StructType = {
      StructType(Array(
        StructField("childnums_id", LongType, false),
        StructField("childitem_id", LongType, false),
        StructField("connect_date", StringType, true)
      ))
    }

    val childnumsNumsyearSchema:StructType = {
      StructType(Array(
        StructField("childnums_id", LongType, false),
        StructField("numsyear_id", LongType, false),
        StructField("connect_year", StringType, true)
      ))
    }

    val numsmonthNumsSchema:StructType = {
      StructType(Array(
        StructField("numsmonth_id", LongType, false),
        StructField("nums_id", LongType, false),
        StructField("connect_date", StringType, true)
      ))
    }

    val numsyearNumsmonthSchema:StructType = {
      StructType(Array(
        StructField("numsyear_id", LongType, false),
        StructField("numsmonth_id", LongType, false),
        StructField("connect_month", StringType, true)
      ))
    }

    val parentSemiparentSchema:StructType = {
      StructType(Array(
        StructField("parent_id", LongType, false),
        StructField("semiparent_id", LongType, false),
        StructField("connect_date", StringType, true)
      ))
    }

    val semiparentSubparentSchema:StructType = {
      StructType(Array(
        StructField("semiparent_id", LongType, false),
        StructField("subparent_id", LongType, false),
        StructField("connect_date", StringType, true)
      ))
    }

    val subparentTopChildSchema:StructType = {
      StructType(Array(
        StructField("subparent_id", LongType, false),
        StructField("topchild_id", LongType, false),
        StructField("connect_date", StringType, true)
      ))
    }

    val superparentParentSchema:StructType = {
      StructType(Array(
        StructField("superparent_id", LongType, false),
        StructField("parent_id", LongType, false),
        StructField("connect_date", StringType, true)
      ))
    }

    val topchildChildnumsSchema:StructType = {
      StructType(Array(
        StructField("topchild_id", LongType, false),
        StructField("childnums_id", LongType, false),
        StructField("connect_date", StringType, true)
      ))
    }

    val valmonthValsSchema:StructType = {
      StructType(Array(
        StructField("childitem_id", LongType, false), // <-- this should be valsmonth_id
        StructField("vals_id", LongType, false),
        StructField("connect_date", StringType, true)
      ))
    }

    val valyearValmonthSchema:StructType = {
      StructType(Array(
        StructField("valyear_id", LongType, false),
        StructField("valmonth_id", LongType, false),
        StructField("connect_month", StringType, true)
      ))
    }

    var childitem_valyear_df:DataFrame = spark.read.format("csv").option("header", "true").schema(childitemValyearSchema).load(inputPath + "childitem_valyear_conn.csv")
    var childnums_childitem_df:DataFrame = spark.read.format("csv").option("header", "true").schema(childnumsChildItemSchema).load(inputPath + "childnums_childitem_conn.csv")
    var childnums_numsyear_df:DataFrame = spark.read.format("csv").option("header", "true").schema(childnumsNumsyearSchema).load(inputPath + "childnums_numsyear_conn.csv")
    var numsmonth_nums_df:DataFrame = spark.read.format("csv").option("header", "true").schema(numsmonthNumsSchema).load(inputPath + "numsmonth_nums_conn.csv")
    var numsyear_numsmonth_df:DataFrame = spark.read.format("csv").option("header", "true").schema(numsyearNumsmonthSchema).load(inputPath + "numsyear_numsmonth_conn.csv")
    var parent_semiparent_df:DataFrame = spark.read.format("csv").option("header", "true").schema(parentSemiparentSchema).load(inputPath + "parent_semiparent_conn.csv")
    var semiparent_subparent_df:DataFrame = spark.read.format("csv").option("header", "true").schema(semiparentSubparentSchema).load(inputPath + "semiparent_subparent_conn.csv")
    var subparent_topchild_df:DataFrame = spark.read.format("csv").option("header", "true").schema(subparentTopChildSchema).load(inputPath + "subparent_topchild_conn.csv")
    var superparent_parent_df:DataFrame = spark.read.format("csv").option("header", "true").schema(superparentParentSchema).load(inputPath + "superparent_parent_conn.csv")
    var topchild_childnums_df:DataFrame = spark.read.format("csv").option("header", "true").schema(topchildChildnumsSchema).load(inputPath + "topchild_childnums_conn.csv")
    var valmonth_vals_df:DataFrame = spark.read.format("csv").option("header", "true").schema(valmonthValsSchema).load(inputPath + "valmonth_vals_conn.csv")
    var valyear_valmonth_df:DataFrame = spark.read.format("csv").option("header", "true").schema(valyearValmonthSchema).load(inputPath + "valyear_valmonth_conn.csv")


    if (useNewAPI) {
      println("\nUsing the new API")

      println("\nWriting valmonth vals edges")
      timeIt("valmonth vals edges", g.updateEdges(
        "valmonth",
        "valmonth_vals",
        "vals",
        valmonth_vals_df.select(
          col("childitem_id") as "out_valmonth_id",
          col("vals_id") as "in_vals_id",
          col("connect_date")
        )
      ))

      if (runFullTest) {
        println("\nWriting childitem valyear edges")
        timeIt("childitem valyear edges", g.updateEdges(
          "childitem",
          "childitem_valyear",
          "valyear",
          childitem_valyear_df.select(
            col("childitem_id") as "out_childitem_id",
            col("valyear_id") as "in_valyear_id",
            col("connect_year")
          )
        ))

        println("\nWriting childnums childitem edges")
        timeIt("childnums childitem edges", g.updateEdges(
          "childnums",
          "childnums_childitem",
          "childitem",
          childnums_childitem_df.select(
            col("childnums_id") as "out_childnums_id",
            col("childitem_id") as "in_childitem_id",
            col("connect_date")
          )
        ))

        println("\nWriting childnums numsyear edges")
        timeIt("childnums numsyear edges", g.updateEdges(
          "childnums",
          "childnums_numsyear",
          "numsyear",
          childnums_numsyear_df.select(
            col("childnums_id") as "out_childnums_id",
            col("numsyear_id") as "in_numsyear_id",
            col("connect_year")
          )
        ))

        println("\nWriting numsmonth nums edges")
        timeIt("numsmonth nums edges", g.updateEdges(
          "numsmonth",
          "numsmonth_nums",
          "nums",
          numsmonth_nums_df.select(
            col("numsmonth_id") as "out_numsmonth_id",
            col("nums_id") as "in_nums_id",
            col("connect_date")
          )
        ))

        println("\nWriting numsyear numsmonth edges")
        timeIt("numsyear numsmonth edges", g.updateEdges(
          "numsyear",
          "numsyear_numsmonth",
          "numsmonth",
          numsyear_numsmonth_df.select(
            col("numsyear_id") as "out_numsyear_id",
            col("numsmonth_id") as "in_numsmonth_id",
            col("connect_month")
          )
        ))

        println("\nWriting parent semiparent edges")
        timeIt("parent semiparent edges", g.updateEdges(
          "parent",
          "parent_semiparent",
          "semiparent",
          parent_semiparent_df.select(
            col("parent_id") as "out_parent_id",
            col("semiparent_id") as "in_semiparent_id",
            col("connect_date")
          )
        ))

        println("\nWriting semiparent subparent edges")
        timeIt("semiparent subparent edges", g.updateEdges(
          "semiparent",
          "semiparent_subparent",
          "subparent",
          semiparent_subparent_df.select(
            col("semiparent_id") as "out_semiparent_id",
            col("subparent_id") as "in_subparent_id",
            col("connect_date")
          )
        ))

        println("\nWriting subparent topchild edges")
        timeIt("subparent topchild edges", g.updateEdges(
          "subparent",
          "subparent_topchild",
          "topchild",
          subparent_topchild_df.select(
            col("subparent_id") as "out_subparent_id",
            col("topchild_id") as "in_topchild_id",
            col("connect_date")
          )
        ))

        println("\nWriting superparent parent edges")
        timeIt("superparent parent edges", g.updateEdges(
          "superparent",
          "superparent_parent",
          "parent",
          superparent_parent_df.select(
            col("superparent_id") as "out_superparent_id",
            col("parent_id") as "in_parent_id",
            col("connect_date")
          )
        ))

        println("\nWriting topchild childnums edges")
        timeIt("topchild childnums edges", g.updateEdges(
          "topchild",
          "topchild_childnums",
          "childnums",
          topchild_childnums_df.select(
            col("topchild_id") as "out_topchild_id",
            col("childnums_id") as "in_childnums_id",
            col("connect_date")
          )
        ))

        println("\nWriting valyear valmonth edges")
        timeIt("valyear valmonth edges", g.updateEdges(
          "valyear",
          "valyear_valmonth",
          "valmonth",
          valyear_valmonth_df.select(
            col("valyear_id") as "out_valyear_id",
            col("valmonth_id") as "in_valmonth_id",
            col("connect_month")
          )
        ))

      }

    } else {
      println("\nUsing the old API")

      println("\nWriting valmonth vals edges")
      var valmonth_vals_edges = valmonth_vals_df.withColumn("srcLabel", lit("valmonth")).withColumn("dstLabel", lit("vals")).withColumn("edgeLabel", lit("valmonth_vals"))
      timeIt("valmonth vals edges", g.updateEdges(valmonth_vals_edges.select(
        g.idColumn(
          col("srclabel"),
          col("childitem_id") as "valmonth_id"
        ) as "src",
        g.idColumn(
          col("dstLabel"),
          col("vals_id")
        ) as "dst",
        col("edgeLabel") as labelStr,
        col("connect_date")
      ), cache))

      if (runFullTest) {
        println("\nWriting childitem valyear edges")
        val childitem_valyear_edges = childitem_valyear_df.withColumn("srcLabel", lit("childitem")).withColumn("dstLabel", lit("valyear")).withColumn("edgeLabel", lit("childitem_valyear"))
        timeIt("childitem valyear edges", g.updateEdges(childitem_valyear_edges.select(
          g.idColumn(
            col("srcLabel"),
            col("childitem_id")
          ) as "src",
          g.idColumn(
            col("dstLabel"),
            col("valyear_id")
          ) as "dst",
          col("edgeLabel") as labelStr,
          col("connect_year")
        ), cache))

        println("\nWriting childnums childitem edges")
        var childnums_childitem_edges = childnums_childitem_df.withColumn("srcLabel", lit("childnums")).withColumn("dstLabel", lit("childitem")).withColumn("edgeLabel", lit("childnums_childitem"))
        timeIt("childnums childitem edges", g.updateEdges(childnums_childitem_edges.select(
          g.idColumn(
            col("srcLabel"),
            col("childnums_id")
          ) as "src",
          g.idColumn(
            col("dstLabel"),
            col("childitem_id")
          ) as "dst",
          col("edgeLabel") as labelStr,
          col("connect_date")
        ), cache))

        println("\nWriting childnums numsyear edges")
        var childnums_numsyear_edges = childnums_numsyear_df.withColumn("srcLabel", lit("childnums")).withColumn("dstLabel", lit("numsyear")).withColumn("edgelabel", lit("childnums_numsyear"))
        timeIt("childnums numsyear edges", g.updateEdges(childnums_numsyear_edges.select(
          g.idColumn(
            col("srcLabel"),
            col("childnums_id")
          ) as "src",
          g.idColumn(
            col("dstLabel"),
            col("numsyear_id")
          ) as "dst",
          col("edgeLabel") as labelStr,
          col("connect_year")
        ), cache))

        println("\nWriting numsmonth nums edges")
        var numsmonth_nums_edges = numsmonth_nums_df.withColumn("srcLabel", lit("numsmonth")).withColumn("dstLabel", lit("nums")).withColumn("edgelabel", lit("numsmonth_nums"))
        timeIt("numsmonth nums edges", g.updateEdges(numsmonth_nums_edges.select(
          g.idColumn(
            col("srcLabel"),
            col("numsmonth_id")
          ) as "src",
          g.idColumn(
            col("dstLabel"),
            col("nums_id")
          ) as "dst",
          col("edgeLabel") as labelStr,
          col("connect_date")
        ), cache))

        println("\nWriting numsyear numsmonth edges")
        var numsyear_numsmonth_edges = numsyear_numsmonth_df.withColumn("srcLabel", lit("numsyear")).withColumn("dstLabel", lit("numsmonth")).withColumn("edgeLabel", lit("numsyear_numsmonth"))
        timeIt("numsyear numsmonth edges", g.updateEdges(numsyear_numsmonth_edges.select(
          g.idColumn(
            col("srcLabel"),
            col("numsyear_id")
          ) as "src",
          g.idColumn(
            col("dstLabel"),
            col("numsmonth_id")
          ) as "dst",
          col("edgeLabel") as labelStr,
          col("connect_month")
        ), cache))

        println("\nWriting parent semiparent edges")
        val parent_semiparent_edges = parent_semiparent_df.withColumn("srcLabel", lit("parent")).withColumn("dstLabel", lit("semiparent")).withColumn("edgeLabel", lit("parent_semiparent"))
        timeIt("parent semiparent edges", g.updateEdges(parent_semiparent_edges.select(
          g.idColumn(
            col("srcLabel"),
            col("parent_id")
          ) as "src",
          g.idColumn(
            col("dstLabel"),
            col("semiparent_id")
          ) as "dst",
          col("edgeLabel") as labelStr,
          col("connect_date")
        ), cache))

        println("\nWriting semiparent subparent edges")
        val semiparent_subparent_edges = semiparent_subparent_df.withColumn("srcLabel", lit("semiparent")).withColumn("dstLabel", lit("subparent")).withColumn("edgeLabel", lit("semiparent_subparent"))
        timeIt("semiparent subparent edges", g.updateEdges(semiparent_subparent_edges.select(
          g.idColumn(
            col("srcLabel"),
            col("semiparent_id")
          ) as "src",
          g.idColumn(
            col("dstLabel"),
            col("subparent_id")
          ) as "dst",
          col("edgeLabel") as labelStr,
          col("connect_date")
        ), cache))

        println("\nWriting subparent topchild edges")
        val subparent_topchild_edges = subparent_topchild_df.withColumn("srcLabel", lit("subparent")).withColumn("dstLabel", lit("topchild")).withColumn("edgeLabel", lit("subparent_topchild"))
        timeIt("subparent topchild edges", g.updateEdges(subparent_topchild_edges.select(
          g.idColumn(
            col("srcLabel"),
            col("subparent_id")
          ) as "src",
          g.idColumn(
            col("dstLabel"),
            col("topchild_id")
          ) as "dst",
          col("edgeLabel") as labelStr,
          col("connect_date")
        ), cache))

        println("\nWriting superparent parent edges")
        val superparent_parent_edges = superparent_parent_df.withColumn("srcLabel", lit("superparent")).withColumn("dstLabel", lit("parent")).withColumn("edgeLabel", lit("superparent_parent"))
        timeIt("superparent parent edges", g.updateEdges(superparent_parent_edges.select(
          g.idColumn(
            col("srcLabel"),
            col("superparent_id")
          ) as "src",
          g.idColumn(
            col("dstLabel"),
            col("parent_id")
          ) as "dst",
          col("edgeLabel") as labelStr,
          col("connect_date")
        ), cache))

        println("\nWriting topchild childnums edges")
        val topchild_childnums_edges = topchild_childnums_df.withColumn("srcLabel", lit("topchild")).withColumn("dstLabel", lit("childnums")).withColumn("edgeLabel", lit("topchild_childnums"))
        timeIt("topchild childnums edges", g.updateEdges(topchild_childnums_edges.select(
          g.idColumn(
            col("srcLabel"),
            col("topchild_id")
          ) as "src",
          g.idColumn(
            col("dstLabel"),
            col("childnums_id")
          ) as "dst",
          col("edgeLabel") as labelStr,
          col("connect_date")
        ), cache))

        println("\nWriting valyear valmonth edges")
        var valyear_valmonth_edges = valyear_valmonth_df.withColumn("srcLabel", lit("valyear")).withColumn("dstLabel", lit("valmonth")).withColumn("edgeLabel", lit("valyear_valmonth"))
        timeIt("valyear valmonth edges", g.updateEdges(valyear_valmonth_edges.select(
          g.idColumn(
            col("srcLabel"),
            col("valyear_id")
          ) as "src",
          g.idColumn(
            col("dstLabel"),
            col("valmonth_id")
          ) as "dst",
          col("edgeLabel") as labelStr,
          col("connect_month")
        ), cache))

      }

    }

    println("\nDone writing edges")

    val totalTimeEnd = System.currentTimeMillis
    val totalRuntimeInSec = (totalTimeEnd - totalTimeStart)/1000.0
    val totalRuntimeRow = spark.createDataFrame(spark.sparkContext.makeRDD(List(Row("Total Runtime", totalRuntimeInSec.toString))), schema)
    runtimes = runtimes.union(totalRuntimeRow)

    runtimes.limit(100).write.json("dgf-perf-results.json") // write runtimes to dsefs

    System.exit(0)
  }
}
import com.datastax.bdp.graph.spark.graphframe._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import org.apache.tinkerpop.gremlin.process.traversal.P
import org.apache.tinkerpop.gremlin.structure.T

/**
  * The code is stored in the scala class just to get right highliting.
  * it is expected to be copy-pasted into spark console
  */

object Notes extends App {
  //not needed in spark-shell
  val spark = SparkSession.builder().getOrCreate()


  // load the graph
  val g = spark.dseGraph("test")
  g.V.show
  g.E.show

  //finding all Josh’s friends of friends:
  //Gremlin
  g.V.has("name", "josh").out("friend").out("friend").show
  //GraphFrame
  g.find("(a)-[e]->(b); (b)-[e2]->(c)").filter(" a.name = 'josh' and e.`~label` = 'friend' and e2.`~label` = 'friend'").select("c.*").show

  //drop all 'person' vertices
  g.V().hasLabel("person").drop().iterate()

  //use iterators
  import scala.collection.JavaConverters._
  for(i <-g.V().id().asScala) println (i)
  //get java Set
  g.V.id.toSet

  // use TinkerPop predicates
  g.V().has("age", P.gt(30)).show
  //and ids
  g.E().groupCount().by(T.label).show

  // define return type when get properties
  g.V().values[Any]("name").next()
  g.V().properties[Any]("age", "name").drop().iterate()

  // load verex table directly with Spark source
  val df = spark.read.format("com.datastax.bdp.graph.spark.sql.vertex").option("graph", "test").load()
  df.show

  // export graph to JSON
  g.V.write.json("dsefs:///tmp/v_json")
  g.E.write.json("dsefs:///tmp/e_json")

  //import graph from json to local copy
  val g2 = DseGraphFrameBuilder.dseGraph("test", spark.read.json("/tmp/v.json"), spark.read.json("/tmp/e.json"))

  //import graph into C* backend
  // do not forget to create schema for new graph
  val d = spark.dseGraph("test_import")
  d.updateVertices(spark.read.json("/tmp/v.json"))
  d.updateEdges(spark.read.json("/tmp/e.json"))

  // import custom graph
  val new_data = org.graphframes.examples.Graphs.friends
  val v = new_data.vertices.select (col("id") as "_id", lit("person") as "~label", col("name"), col("age"))
  val e = new_data.edges.select (g.idColumn(lit("person"), col("src")) as "src", g.idColumn(lit("person"), col("dst")) as "dst",
    col("relationship") as "~label")

  g.updateVertices (v)
  g.updateEdges (e)

}

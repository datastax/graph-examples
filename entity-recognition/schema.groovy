system.graph("entity").create()

:remote config alias g entity.g
//define properties
schema.propertyKey("age").Int().single().create()
schema.propertyKey("name").Text().single().create()
schema.propertyKey("passport_id").Text().single().create()
schema.propertyKey("entity_id").Text().single().create()

schema.vertexLabel("person").partitionKey("passport_id").clusteringKey("name").properties("age").create()
schema.vertexLabel("master").partitionKey("entity_id").properties("passport_id").create()
schema.vertexLabel("master").index("passport_index").secondary().by("passport_id").add()

schema.edgeLabel("is").single().connection("person", "master").create()

// examples
//Vertex marko = graph.addVertex(T.label, "person", "passport_id", "a1", "name", "marko", "age", 29)
//Vertex vadas = graph.addVertex(T.label, "person", "passport_id", "a2", "name", "vadas", "age", 27)
//Vertex josh = graph.addVertex(T.label, "person", "passport_id", "a3", "name", "josh", "age", 32)
//Vertex passport = graph.addVertex(T.label,"master" , "entity_id", java.util.UUID.randomUUID().toString() , "passport_id", "a3")
//josh.addEdge("is", passport)

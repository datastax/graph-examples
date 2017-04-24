system.graph("test").create()

:remote config alias g test.g
//define properties
schema.propertyKey("age").Int().create()
schema.propertyKey("name").Text().create()
schema.propertyKey("id").Text().single().create()

//define vertex with id property as a custom ID
schema.vertexLabel("person").partitionKey("id").properties("name", "age").create()
// two type of edges without properties
schema.edgeLabel("friend").connection("person", "person").create()
schema.edgeLabel("follow").connection("person", "person").create()

Vertex marko = graph.addVertex(T.label, "person", "id", "1", "name", "marko", "age", 29)
Vertex vadas = graph.addVertex(T.label, "person", "id", "2", "name", "vadas", "age", 27)
Vertex josh = graph.addVertex(T.label, "person", "id", "3", "name", "josh", "age", 32)
marko.addEdge("friend", vadas)
marko.addEdge("friend", josh)
josh.addEdge("follow", marko)
josh.addEdge("friend", marko)


// add one more property for streaming test
schema.propertyKey("messages").Text().multiple().create()
schema.vertexLabel("person").properties("messages").add()
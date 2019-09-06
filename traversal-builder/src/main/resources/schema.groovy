schema.config().option("graph.allow_scan").set(true)

schema.propertyKey("name").Text().single().ifNotExists().create()
schema.propertyKey("age").Int().single().ifNotExists().create()
schema.propertyKey("lang").Text().single().ifNotExists().create()
schema.propertyKey("weight").Decimal().single().ifNotExists().create()

schema.vertexLabel("person").partitionKey("name").properties("name","age").ifNotExists().create()
schema.vertexLabel("software").partitionKey("name").properties("name","lang").ifNotExists().create()

schema.edgeLabel("knows").single().properties("weight").ifNotExists().create()
schema.edgeLabel("knows").connection("person","person").add()

schema.edgeLabel("created").single().properties("weight").ifNotExists().create()
schema.edgeLabel("created").connection("person","software").add()

schema.edgeLabel("uses").single().properties("weight").ifNotExists().create()
schema.edgeLabel("uses").connection("person","software").add()
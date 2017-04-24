// Either create the graph in studio and add the following in a cell
//  or uncomment and run the following and run with the gremlin console
//:remote config alias reset
//system.graph("london_tube").drop()
//system.graph("london_tube").ifNotExists().create()
//:remote config alias g london_tube.g
//:remote config timeout max

schema.propertyKey("zone").Double().single().create()
schema.propertyKey("line").Text().single().create()
schema.propertyKey("name").Text().single().create()
schema.propertyKey("id").Int().single().create()
schema.propertyKey("is_rail").Boolean().single().create()
schema.propertyKey("lines").Text().multiple().create()
schema.edgeLabel("connectedTo").multiple().properties("line").create()
schema.vertexLabel("station").partitionKey("id").properties("name", "zone", "is_rail", "lines").create()
schema.vertexLabel("station").index("search").search().by("name").asText().by("zone").by("is_rail").by("lines").asText().add()
schema.vertexLabel("station").index("stationByName").materialized().by("name").add()
schema.vertexLabel("station").index("toStationByLine").outE("connectedTo").by("line").add()
schema.vertexLabel("station").index("fromStationByLine").inE("connectedTo").by("line").add()
schema.edgeLabel("connectedTo").connection("station", "station").add()


// Geodetic example - NO SEARCH INDEX

system.graph('geodetic51').create()
:remote config alias g geodetic51.g
schema.config().option('graph.allow_scan').set('true')

// Schema
// Note use of withGeoBounds()
schema.propertyKey('name').Text().create()
schema.propertyKey('point').Point().withGeoBounds().create()
schema.vertexLabel('location').properties('name','point').create()
schema.propertyKey('line').Linestring().withGeoBounds().create()
schema.vertexLabel('lineLocation').properties('name','line').create()
schema.propertyKey('polygon').Polygon().withGeoBounds().create()
schema.vertexLabel('polyLocation').properties('name','polygon').create()

// Geodetic example - NO SEARCH INDEX

:remote config alias g geodetic51.g
schema.config().option('graph.allow_scan').set('true')

// Create points
graph.addVertex(label,'location','name','Paris','point',Geo.point(2.352222, 48.856614))
graph.addVertex(label,'location','name','London','point',Geo.point(-0.127758,51.507351))
graph.addVertex(label,'location','name','Dublin','point',Geo.point(-6.26031, 53.349805))
graph.addVertex(label,'location','name','Aachen','point',Geo.point(6.083887, 50.775346))
graph.addVertex(label,'location','name','Tokyo','point',Geo.point(139.691706, 35.689487))

// Create linestrings
graph.addVertex(label, 'lineLocation', 'name', 'ParisLondon', 'line', "LINESTRING(2.352222 48.856614, -0.127758 51.507351)")
graph.addVertex(label, 'lineLocation', 'name', 'LondonDublin', 'line', "LINESTRING(-0.127758 51.507351, -6.26031 53.349805)")
graph.addVertex(label, 'lineLocation', 'name', 'ParisAachen', 'line', "LINESTRING(2.352222 48.856614, 6.083887 50.775346)")
graph.addVertex(label, 'lineLocation', 'name', 'AachenTokyo', 'line', "LINESTRING(6.083887 50.775346, 139.691706 35.689487)")

// Create polygons
graph.addVertex(label, 'polyLocation','name', 'ParisLondonDublin', 'polygon',Geo.polygon(2.352222, 48.856614, -0.127758, 51.507351, -6.26031, 53.349805))
graph.addVertex(label, 'polyLocation','name', 'LondonDublinAachen', 'polygon',Geo.polygon(-0.127758, 51.507351, -6.26031, 53.349805, 6.083887, 50.775346))
graph.addVertex(label, 'polyLocation','name', 'DublinAachenTokyo', 'polygon',Geo.polygon(-6.26031, 53.349805, 6.083887, 50.775346, 139.691706, 35.689487))

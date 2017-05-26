// Print outgoing edges
g.V().outE()

// Find all outgoing edges with a start date of Jan 1, 1960
g.V().outE().has('dateStart', '1960-01-01')

// Find the vertices at the end of the outgoing edges in the last query
g.V().outE().has('dateStart', '1960-01-01').outV()

// Get a value map of all the values for the vertices in the last query
g.V().outE().has('dateStart', '1960-01-01').outV().valueMap()

// Change the query to find all the values where the start date is later than Jan 1, 1960
g.V().outE().has('dateStart', gt('1960-01-01')).outV().valueMap()

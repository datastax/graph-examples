## Graph Vitals Tour

The following metrics are common Gremlin queries that our customers run. This table serves as a translation tool between a graph theory term, its meaning, and a way to calculate that metric in Gremlin.

| Graph Invariant Name        | Definition           | Computational Complexity  | Gremlin |
| ------------- |:-------------:| :-----:| :-----|
| Order    | The number of vertices `v` in a graph `G` | P | `g.V().count() = n` |
| Size     | The number of edges `e` in a graph `G` | P | `g.E().count() = m` |
| Degree | The number of edges incident to a vertex | P | `g.V("{someVertex}").bothE().count()` |
| Minimum Degree | The smallest degree of all vertices in G | P | `g.V().groupCount().by(bothE().count()).order(local).by(values, incr).limit(1)` |
| Maximum Degree | The largest degree of all vertices in G | P | `g.V().groupCount().by(bothE().count()).order(local).by(values, decr).limit(1)` |
| Average Degree | Average degree of all vertices in G | P | `(2*m)/n` |
| Degree Distribution | For all vertices in the graph, the probability distribution of their degrees over the whole network. | P | `g.V().groupCount().by(outE()).order(local).by(values, decr)` |
| Distribution of Vertices (Edges) per Label | The number of vertices (edges) per vertex (edge) label | P | `g.V/E().groupCount().by(label).order(local).by(values, decr)` |

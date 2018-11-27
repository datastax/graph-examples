## Graph Vitals Tour

The following metrics are common Gremlin queries that our customers run. This table serves as a translation tool between a graph theory term, its meaning, and a way to calculate that metric in Gremlin.

| Graph Invariant Name        | Definition           | Computational Complexity  | Gremlin |
| ------------- |:-------------:| :-----:| :-----|
| Order    | The number of vertices `v` in a graph `G` | P | `g.V().`<br>&nbsp; &nbsp; `count()` |
| Size     | The number of edges `e` in a graph `G` | P | `g.E().`<br>&nbsp; &nbsp; `count()` |
| Degree | The number of edges incident to a vertex | P | `g.V("{someVertex}").`<br>&nbsp; &nbsp; `bothE().`<br>&nbsp; &nbsp; `count()` |
| Minimum Degree | The smallest degree of all vertices in G | P | `g.V().`<br>&nbsp; &nbsp; `groupCount().`<br>&nbsp; &nbsp; `by(bothE().`<br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; `count()).`<br>&nbsp; &nbsp; `order(local).`<br>&nbsp; &nbsp; `by(values, incr).`<br>&nbsp; &nbsp; `limit(1)` |
| Maximum Degree | The largest degree of all vertices in G | P | `g.V().`<br>&nbsp; &nbsp; `groupCount().`<br>&nbsp; &nbsp; `by(bothE().`<br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; `count()).`<br>&nbsp; &nbsp; `order(local).`<br>&nbsp; &nbsp; `by(values, decr).`<br>&nbsp; &nbsp; `limit(1)`  |
| Average Degree | Average degree of all vertices in G | P | `(2*Size)/Order` |
| Degree Distribution | For all vertices in the graph, the probability distribution of their degrees over the whole network. | P | `g.V().`<br>&nbsp; &nbsp; `groupCount().`<br>&nbsp; &nbsp; `by(outE()).`<br>&nbsp; &nbsp; `order(local).`<br>&nbsp; &nbsp; `by(values, decr)` |
| Distribution of Vertices (Edges) per Label | The number of vertices (edges) per vertex (edge) label | P | `g.V/E().`<br>&nbsp; &nbsp; `groupCount().`<br>&nbsp; &nbsp; `by(label).`<br>&nbsp; &nbsp; `order(local).`<br>&nbsp; &nbsp; `by(values, decr)` |
| <img width=400/> |  |  | <img width=500/>|

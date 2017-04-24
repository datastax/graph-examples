# London Tube
The London Tube dataset is pretty small and is publicly available **[here]**

The purpose of this dataset is to demonstrate the usage of the path object with Gremlin

### I Schema explanation

The schema is pretty simple, we have a single vertex label _station_ with the following properties:

* name: the station name
* lines: a collection of tube **lines** to which this station belongs
* is_rail: whether the staton belongs to the railway system
* zone: the zone in which the station belongs to. It is not an integer since zone _2.5_ can exists ...

Each station is connected to others by one or multiple _connectedTo_ edge. Normally the connection between 2 stations is **undirected** but since **TinkerPop** is a **directed** graph, we'll only manifest this connection once with a **directed** edge and use the Gremlin step **`both()`** or **`bothE()`**

Eac edge has a single property _line_ which indicates the line that is connecting 2 stations

### II Schema creation

Just use the **schema.groovy** script or copy/paste its content in a _gremlin-console_ or **[Datastax Studio]** notebook


### III Data import (please create schema **FIRST**)

You have 2 choices to import data

* either by importing directly the **`london_tube.gryo`** file in _gremlin-console_ or **[Datastax Studio]** with: `graph.io(IoCore.gryo()).readGraph("london_tube.gryo");`
* or by using the **[Datastax Graph Loader]** script `london_tube_mapping.groovy` with the 2 data files `london_vertices.csv` & `london_edges.csv`

### IV Sample Gremlin traversals

First you need to allow full scan of the graph with the following option:

```groovy
schema.config().option('graph.allow_scan').set('true');
```

#### A. Give me the path from **South Kensington** to **Covent Garden** using the **Piccadilly** line

```groovy
g.V().has("station", "name", "South Kensington")
    .emit()
    .repeat(timeLimit(200).both("connectedTo").filter(bothE("connectedTo").has("line",Search.tokenPrefix("Piccadilly"))).simplePath())
    .has("name", "Covent Garden")
    .path().unfold()

```

`g.V().has("station", "name", "South Kensington")` == `Iterator<Station>` with a single element

We purposedly put `emit()` **before** the `repeat()` step so that the original **South Kensington** station is emitted alongside with all other stations on the journey.

For the `repeat()` clause, instead of putting a limit in terme of number of times we traverse the _connectedTo_ edge with the step `times(x)`, we rather set a time limit to avoid graph explosion with `timeLimit(y)`

We only collect stations which belong to the **Piccadilly** line with the prefix search filtering `has("line", Search.tokenPrefix("Piccadilly"))`. This predicate is DSE specific and is leveraging **[DSE Search]**

The `simplePath()` step is there to avoid re-traversing already visited paths

Finally we filter the target station to match **Covent Garden** with the step `has("name", "Covent Garden")`. If we were to stop our traversal at this step, there will be a single displayed station, which is **Covent Garden**. 

What we want is different: all the stations of the journey from **South Kensington** to **Covent Garden** and here `path()` steps comes in handy.

The path object implements the interface `Iterator<Object>`, `Object` can be anything:

* all labels created on the traversal
* all vertices visited by the traversal
* all edges visited by the traversal
* all side-effects or data structures created during the traversal

Since we only visit vertices on our traversal, the step `path()` is of type `Iterator<Iterator<Vertex>>`, the outer `Iterator` represents the Traversal object itself.

To access the inner `Iterator<Vertex>` we need to use the `unfold()` operator, it is similar to a `flatMap()` operator in Java/Scala.

So consequently `path().unfold()` become an `Iterator<Station>` and will display all stations from **South Kensington** to **Covent Garden**

#### B. Give me the shortest path from **South Kensington** to **Marble Arch** in term of station count

```groovy
g.V().has("station", "name", "South Kensington").
    emit().
    repeat(timeLimit(400).both("connectedTo").simplePath()).
    has("name", "Marble Arch").
    order().by(path().count(local), incr).
    limit(1).
    path().unfold()
```

There are 2 possible journey from **South Kensington** to **Marble Arch** on the London tube map (just check by having a look to the file `london-tube-map.pdf`)

Since we want the shortest path in term of **station count** we need to sort those 2 journeys using `order().by(path().count(local), incr)`

Since `path()` is an `Iterator<Iterator<Vertex>>`, we need `count(local)` to count the number of vertices in the nested collection e.g. in the `Iterator<Vertex>`.

We order those journeys by ascending order and we take the 1st one which give us the shortest path.


#### C. Give me the shortest path from **South Kensington** to **Marble Arch** in term of minimum line changes

```groovy
g.V().has("station", "name", "South Kensington").
    emit().
    repeat(timeLimit(1000).bothE("connectedTo").as("paths").otherV().simplePath()).
    has("name", "Marble Arch").
    order().by(select("paths").unfold().values("line").dedup().count(), incr).
    limit(1).
    path().unfold().
    hasLabel("station")
```

The first difference with the previous example is inside the `repeat()` step, we purposedly use `bothE("connectedTo").as("paths").otherV()` instead of a simple `both("connectedTo")` to force the traversal to navigate through _connectedTo_ edges instead of jumping from vertex to vertex. The idea is that the _connectedTo_ edges will be collected and made available in the Path object (`path()`)

Please notice the alias "paths" we assign to the edges.

For the `order()` clause, we collect all the edges (saved as "paths" label) on each traversal, take the _line_ property, deduplicate them and count them so we get distinct number of lines for each journey.

We order the journey by distinct lines count in ascending order and take the 1st result which should give us the shortest path in term of line change.

Please notice that instead of `path().unfold()` we now have to filter the output of the path object with `hasLabel("station")` to only display _station_ vertices and not _connectedTo_ edges.


And alternative to the previous traversal is given below:

```groovy
g.V().has("station", "name", "South Kensington").
    emit().
    repeat(timeLimit(1000).bothE("connectedTo").otherV().simplePath()).
    has("name", "Marble Arch").
    order().by(path().unfold().hasLabel("connectedTo").values("line").dedup().count(), incr).
    limit(1).
    path().unfold().
    hasLabel("station")
```

The only difference here is that instead giving an alias "paths" to the _connectedTo_ edges and recall them later with `select("paths")` we directly use `path().unfold().hasLabel("connectedTo")` to only access the _connectedTo_ edges in the `order().by(...)` clause

[here]: https://commons.wikimedia.org/wiki/London_Underground_geographic_maps/CSV

[Datastax Studio]: http://docs.datastax.com/en/dse/5.1/dse-dev/datastax_enterprise/studio/stdToc.html

[Datastax Graph Loader]: http://docs.datastax.com/en/dse/5.1/dse-dev/datastax_enterprise/graph/dgl/dglOverview.html

[DSE Search]: http://docs.datastax.com/en/dse/5.1/dse-dev/datastax_enterprise/search/searchAbout.html

package com.datastax.examples.builder;

import com.datastax.examples.builder.shortestPath.ShortestPathQueryBuilder;
import org.apache.tinkerpop.gremlin.process.traversal.Operator;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.T;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Daniel Kuppitz (http://gremlin.guru)
 */
class ShortestPathTraversals {

    private static List<ShortestPathTraversal> TRAVERSALS = new ArrayList<>();

    /*
     * Initialize shortest path traversal examples.
     */
    static {
        add("Find all shortest paths between every vertex pair",
                g -> new ShortestPathQueryBuilder<>(g.V().as("v"))
                        .mapVertices("name")
                        .target(__.where(P.gt("v")).by(T.id))
                        .build());
        add("Find a shortest path between every vertex pair",
                g -> new ShortestPathQueryBuilder<>(g.V().as("v"))
                        .mapVertices("name").limit(1)
                        .target(__.where(P.gt("v")).by(T.id))
                        .build());
        add("Find a shortest path between every vertex pair (maximum distance: 3)",
                g -> new ShortestPathQueryBuilder<>(g.V().as("v"))
                        .mapVertices("name").limit(1)
                        .maxDistance(3)
                        .target(__.where(P.gt("v")).by(T.id))
                        .build());
        add("Find all shortest paths from marko",
                g -> new ShortestPathQueryBuilder<>(g.V().has("person", "name", "marko"))
                        .mapVertices("name")
                        .build());
        add("Find all shortest paths from marko (distance included)",
                g -> new ShortestPathQueryBuilder<>(g.V().has("person", "name", "marko"))
                        .mapVertices("name")
                        .returnDistance()
                        .build());
        add("Find all shortest paths from marko (edges and distance included)",
                g -> new ShortestPathQueryBuilder<>(g.V().has("person", "name", "marko"))
                        .includeEdges()
                        .mapEdges(T.label)
                        .mapVertices("name")
                        .returnDistance()
                        .build());
        add("Find all shortest paths from marko to peter",
                g -> new ShortestPathQueryBuilder<>(g.V().has("person", "name", "marko"))
                        .mapVertices("name")
                        .target(__.has("name", "peter"))
                        .build());
        add("Find all shortest in-directed paths to josh",
                g -> new ShortestPathQueryBuilder<>(g.V())
                        .edges(Direction.IN)
                        .mapVertices("name")
                        .target(__.has("name", "josh"))
                        .build());
        add("Find all shortest paths from marko to josh",
                g -> new ShortestPathQueryBuilder<>(g.V().has("person", "name", "marko"))
                        .includeEdges()
                        .mapEdges(edgeMapper())
                        .mapVertices("name")
                        .target(__.has("name", "josh"))
                        .build());
        add("Find all shortest paths from marko to josh using a custom distance property",
                g -> new ShortestPathQueryBuilder<>(g.V().has("person", "name", "marko"))
                        .distance("weight")
                        .includeEdges()
                        .mapEdges(edgeMapper())
                        .mapVertices("name")
                        .target(__.has("name", "josh"))
                        .build());
        add("Find the shortest path from vadas to ripple using a custom distance property",
                g -> new ShortestPathQueryBuilder<>(g.V().has("person", "name", "vadas"))
                        .distance("weight")
                        .includeEdges()
                        .limit(1)
                        .mapEdges(edgeMapper())
                        .mapVertices("name")
                        .target(__.has("name", "ripple"))
                        .build());
        add("Find the longest path from vadas to ripple using a custom distance property",
                g -> new ShortestPathQueryBuilder<>(g.V().has("person", "name", "vadas"))
                        // distance = sum(1/weight) -> longest path (by weight) becomes the shortest path
                        .distance(__.sack(Operator.assign).by(__.constant(1.0)).sack(Operator.div).by("weight").sack())
                        .includeEdges()
                        .limit(1)
                        .mapEdges(edgeMapper())
                        .mapVertices("name")
                        .target(__.has("name", "ripple"))
                        .build());
    }

    private static void add(String description, Function<GraphTraversalSource, GraphTraversal> traversalBuilder) {
        TRAVERSALS.add(new ShortestPathTraversal(description, traversalBuilder));
    }

    /**
     * A default edge mapper, that maps an edge to {@code [T.label, weight]}.
     *
     * @return Default edge mapper
     */
    private static Traversal edgeMapper() {
        //noinspection unchecked
        return __.union(__.label(), __.values("weight")).fold();
    }

    /**
     * @return All shortest path traversal examples.
     */
    static Iterable<ShortestPathTraversal> get() {
        return TRAVERSALS;
    }

    /**
     * Gets the shortest path traversal example at the specified index.
     *
     * @param i Shortest path traversal example index
     * @return The shortest path traversal example or 0 if the index is invalid.
     */
    static ShortestPathTraversal get(int i) {
        if (i >= 0 && i < TRAVERSALS.size()) {
            return TRAVERSALS.get(i);
        }
        return null;
    }

    /**
     * @return The number of shortest path traversal examples.
     */
    static int size() {
        return TRAVERSALS.size();
    }

    static class ShortestPathTraversal {

        final String description;
        final Function<GraphTraversalSource, GraphTraversal> traversalBuilder;

        ShortestPathTraversal(String description, Function<GraphTraversalSource, GraphTraversal> traversalBuilder) {
            this.description = description;
            this.traversalBuilder = traversalBuilder;
        }
    }
}

package com.datastax.examples.builder.shortestPath;

import org.apache.tinkerpop.gremlin.process.traversal.Operator;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Column;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.T;

import java.util.Objects;

/**
 * @author Daniel Kuppitz (http://gremlin.guru)
 */
@SuppressWarnings("unused")
public class ShortestPathQueryBuilder<S, E> {

    private final static String START_LABEL = "sp.s1";
    private final static String START_LABEL2 = "sp.s2";
    private final static String END_LABEL = "sp.e1";
    private final static String END_LABEL2 = "sp.e2";
    private final static String PATH_LABEL = "sp.p";
    private final static String DISTANCE_LABEL = "sp.d";
    private final static String KEY_VALUE_LABEL = "sp.kv";

    private final GraphTraversal<S, E> traversal;

    private Traversal<?, ?> targetTraversal;
    private Traversal<?, ?> distanceTraversal;
    private Direction direction;
    private String[] edgeLabels;
    private Number maxDistance;
    private Object vertexMapper;
    private Object edgeMapper;
    private boolean includeEdges;
    private int limit;
    private boolean returnDistance;

    public ShortestPathQueryBuilder(GraphTraversal<S, E> traversal) {
        this.traversal = traversal;
        this.direction = Direction.BOTH;
        this.edgeLabels = new String[0];
    }

    /**
     * Sets the traversal the defines the target condition. If this traversal returns a result, the vertex is
     * considered to be the last vertex in the path.
     *
     * @param traversal Traversal that matches the last vertex in a path.
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder target(Traversal traversal) {
        this.targetTraversal = traversal;
        return this;
    }

    /**
     * Defines which edges to traverse, the direction and the labels. By default all edges will be traversed
     * into both direction.
     *
     * @param direction The {@link Direction} in which to traverse.
     * @param labels The edge labels that shall be considered (all edge labels, if not specified).
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder edges(Direction direction, String... labels) {
        this.direction = direction;
        this.edgeLabels = labels;
        return this;
    }

    /**
     * Sets the name of the edge property that shall be used for the distance calculation. If not specified, the
     * distance will be the number of hops.
     *
     * @param property The name of the edge property that represents the distance of the edge.
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder distance(String property) {
        return distance(__.values(property));
    }

    /**
     * Sets the traversal that computes the distance of the current edge. If not specified, the
     * distance will be the number of hops.
     *
     * @param traversal The traversal that computes the distance of the edge.
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder distance(Traversal traversal) {
        this.distanceTraversal = traversal;
        return this;
    }

    /**
     * Sets the maximum shortest path discovery distance. If the distance is calculated based on the number of
     * hops, then the discovery will end as soon as the maximum distance is reached. However, if the distance is
     * calculated based on edge properties or custom traversals, the maximum distance will only be used as a
     * post-filter, meaning that the discovery will not stop when the maximum distance is reached or exceeded. The
     * reason behind this is that, theoretically, custom distances can be negative and thus any path exceeded the
     * maximum distance could be back in a valid range after a few more hops.
     *
     * @param distance The maximum distance for all shortest paths.
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder maxDistance(Number distance) {
        this.maxDistance = distance;
        return this;
    }

    /**
     * Specifies that edges shall be included in the result.
     *
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder includeEdges() {
        this.includeEdges = true;
        return this;
    }

    /**
     * Maps all vertices in the path to the specified property.
     *
     * @param property The vertex property used to represent the vertex.
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder mapVertices(String property) {
        this.vertexMapper = property;
        return this;
    }

    /**
     * Maps all vertices in the path to the specified {@link T} token.
     *
     * @param token The token used to represent the vertex.
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder mapVertices(T token) {
        this.vertexMapper = token;
        return this;
    }

    /**
     * Applies the specified traversal to all vertices in the path. The result will be used to represent the vertex
     * in the path.
     *
     * @param traversal The traversal that maps a vertex to a different value.
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder mapVertices(Traversal traversal) {
        this.vertexMapper = traversal;
        return this;
    }

    /**
     * Maps all edges in the path to the specified property.
     *
     * @param property The edge property used to represent the edge.
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder mapEdges(String property) {
        this.edgeMapper = property;
        return this;
    }

    /**
     * Maps all edges in the path to the specified {@link T} token.
     *
     * @param token The token used to represent the edge.
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder mapEdges(T token) {
        this.edgeMapper = token;
        return this;
    }

    /**
     * Applies the specified traversal to all edges in the path. The result will be used to represent the edge
     * in the path.
     *
     * @param traversal The traversal that maps an edge to a different value.
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder mapEdges(Traversal traversal) {
        this.edgeMapper = traversal;
        return this;
    }

    /**
     * Maps all vertices and edges in the path to the specified property.
     *
     * @param property The property used to represent the element.
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder map(String property) {
        this.vertexMapper = this.edgeMapper = property;
        return this;
    }

    /**
     * Maps all vertices and edges in the path to the specified {@link T} token.
     *
     * @param token The token used to represent the element.
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder map(T token) {
        this.vertexMapper = this.edgeMapper = token;
        return this;
    }

    /**
     * Applies the specified traversal to all vertices and edges in the path. The result will be used to
     * represent the element in the path.
     *
     * @param traversal The traversal that maps an element to a different value.
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder map(Traversal traversal) {
        this.vertexMapper = this.edgeMapper = traversal;
        return this;
    }

    /**
     * Sets the maximum number of returned paths for any pair of vertices. By default the number of shortest
     * paths between any two vertices will not be limited.
     *
     * @param shortestPathsPerPair The maximum number of returned paths for any pair of vertices.
     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder limit(int shortestPathsPerPair) {
        this.limit = shortestPathsPerPair;
        return this;
    }

    /**
     * Specifies that not only the path, but also the distance for each path shall be returned. This changes the
     * output format; instead of paths, the traversal will return maps with 2 entries: {@code path} and {@code distance).

     * @return This {@link ShortestPathQueryBuilder} instance.
     */
    public ShortestPathQueryBuilder returnDistance() {
        this.returnDistance = true;
        return this;
    }

    /**
     * Build the shortest traversal, append it to the initially provided traversal, and return the extended traversal.
     *
     * @return The initial traversal extended by the shortest path traversal part.
     */
    public GraphTraversal<S, E> build() {

        // initialize the sack-value, which will keep track of the distance, with 0, label the start vertex, and
        // append the `repeat()` traversal
        traversal
                .sack(Operator.assign)
                    .by(__.constant(0)).as(START_LABEL)
                .repeat(buildRepeatTraversal());

        // if a target filter was specified, emit only vertices that match the filter-traversal,
        // otherwise emit all vertices
        if (targetTraversal != null) {
            traversal.emit(targetTraversal);
        } else {
            traversal.emit();
        }

        // label the last vertex
        traversal.as(END_LABEL);

        // if the number of shortest paths between each pair of vertices is 1 and the distance is calculated
        // based on the number of hops, a simple `dedup()` filter can be used
        if (limit == 1 && distanceTraversal == null) {
            traversal.dedup(START_LABEL, END_LABEL);
        }

        // get the path from the start vertex to here
        traversal.path().from(START_LABEL);

        // add path `by()` modulators for vertices ...
        appendByModulator(vertexMapper);

        // ... and edges
        if (traverseEdges() && !Objects.equals(vertexMapper, edgeMapper)) {
            if (edgeMapper != null) {
                if (vertexMapper == null)
                    traversal.by(); // vertexMapper not specified, map vertices to themselves
                appendByModulator(edgeMapper);
            } else {
                traversal.by(); // edges included, but edgeMapper not specified, map edges to themselves
            }
        }

        final String startLabel, endLabel;
        if (!includeEdges && distanceTraversal != null) {
            // worst case. edges had to be included in order to calculate the distance, but edges are not supposed
            // to be in the final result, hence they need to be removed here; the following part generates a new path
            // with all edges excluded
            traversal
                    .as(PATH_LABEL).sack().as(DISTANCE_LABEL)
                    .select(PATH_LABEL).sack(Operator.assign).by(__.skip(Scope.local, 2))
                    .limit(Scope.local, 1).as(START_LABEL2)
                    .until(__.not(__.sack().unfold()))
                        .repeat(__
                                .map(__.sack().limit(Scope.local, 1))
                                .sack(Operator.assign)
                                    .by(__.sack().skip(Scope.local, 2))).as(END_LABEL2)
                    .path().from(START_LABEL2)
                    .sack(Operator.assign).by(__.select(DISTANCE_LABEL));

            startLabel = START_LABEL2;
            endLabel = END_LABEL2;
        } else {
            startLabel = START_LABEL;
            endLabel = END_LABEL;
        }

        // if paths were not filtered using `dedup()`, group them by their start- and end-vertex and
        // then by their distance to determine the final result
        if (limit != 1 || distanceTraversal != null) {
            GraphTraversal<?, ?> distanceGroupTraversal = __.group().by(__.sack());
            if (limit > 0) {
                // save some memory if a hard limit for the number of shortest paths between
                // each pair of vertices was specified
                distanceGroupTraversal.by(__.limit(limit).fold());
            }
            traversal
                    .group()
                        .by(__.select(startLabel, endLabel))  // group by start- and end-vertex
                        .by(distanceGroupTraversal)           // group by distance
                    .unfold().select(Column.values)           // select paths grouped by distance for each vertex pair
                    .order(Scope.local)
                        .by(Column.keys)                      // order each map by the distance
                    .limit(Scope.local, 1);              // select map containing paths with the shortest distance

            if (returnDistance) {
                // for each path create a map that contains the path and its distance
                traversal.as(KEY_VALUE_LABEL)
                        .select(Column.values).unfold().unfold()
                        .project("path", "distance")
                            .by()
                            .by(__.select(KEY_VALUE_LABEL).select(Column.keys).unfold())
                            /*.by(distanceTraversal != null
                                    ? __.select(KEY_VALUE_LABEL).select(Column.keys)
                                    : includeEdges
                                    ? __.count(Scope.local).sack(Operator.assign).sack(Operator.div).by(__.constant(2)).sack()
                                    : __.count(Scope.local))*/;
            } else {
                traversal.select(Column.values).unfold().unfold();
            }
        } else if (returnDistance) {
            traversal
                    .project("path", "distance")
                    .by()
                    .by(__.sack());
        }

        return traversal;
    }

    /**
     * @return The inner {@code repeat()) traversal.
     */
    private Traversal<?, E> buildRepeatTraversal() {
        GraphTraversal<?, E> traversal = __.start();
        if (traverseEdges()) {
            switch (direction) {
                case OUT:
                    computeDistance(traversal.outE(edgeLabels)).inV();
                    break;
                case IN:
                    computeDistance(traversal.inE(edgeLabels)).outV();
                    break;
                default:
                    computeDistance(traversal.bothE(edgeLabels)).otherV();
                    break;
            }
        } else {
            switch (direction) {
                case OUT:
                    traversal.out(edgeLabels);
                    break;
                case IN:
                    traversal.in(edgeLabels);
                    break;
                default:
                    traversal.both(edgeLabels);
                    break;
            }
            computeDistance(traversal);
            if (maxDistance != null) {
                // stop traversing if maximum distance is reached or exceeded
                traversal.filter(__.sack().is(P.lt(maxDistance)));
            }
        }
        traversal.simplePath().from(START_LABEL);
        return traversal;
    }

    /**
     * Append a `by()` modulator, which is either a {@link String}, a {@link T} token, or a {@link Traversal}.
     */
    private void appendByModulator(Object modulator) {
        if (modulator != null) {
            if (modulator instanceof String) {
                traversal.by((String) modulator);
            } else if (modulator instanceof T) {
                traversal.by((T) modulator);
            } else {
                traversal.by((Traversal<?, ?>) modulator);
            }
        }
    }

    /**
     * Appends the distance calculation to the provided traversal.
     *
     * @param traversal The traversal to extend with the distance calculation.
     * @return The provided traversal.
     */
    private GraphTraversal<?, ?> computeDistance(GraphTraversal<?, ?> traversal) {
        return traversal.sack(Operator.sum).by(distanceTraversal != null ? distanceTraversal : __.constant(1));
    }

    /**
     * @return Whether edges will be traversed or not.
     */
    private boolean traverseEdges() {
        return includeEdges || distanceTraversal != null;
    }
}
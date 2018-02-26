package com.killrvideo;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.T;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides for pre-built data enrichment options for the {@link KillrVideoTraversalDsl#enrich(Enrichment...)}
 * step. These options will include extra information about the {@code Vertex} when output from that step. Note that
 * the enrichment examples presented here are examples to demonstrate this concept. The primary lesson here is to show
 * how one might merge map results as part of a DSL.These enrichment options may not be suitable for traversals in
 * production systems as counting all edges might add an unreasonable amount of time to an otherwise fast traversal.
 */
public class Enrichment {

    private List<String> projectedKey;

    private List<GraphTraversal> traversals;

    private Enrichment(String projectedKeys, GraphTraversal... traversals) {
        this(Collections.singletonList(projectedKeys), traversals);
    }

    private Enrichment(List<String> projectedKeys, GraphTraversal... traversals) {
        this(projectedKeys, Stream.of(traversals).collect(Collectors.toList()));
    }

    private Enrichment(List<String> projectedKeys, List<GraphTraversal> traversals) {
        if (projectedKeys.size() != traversals.size())
            throw new IllegalArgumentException("projectedKeys and traversals arguments must have an equal number of elements");
        
        this.projectedKey = projectedKeys;
        this.traversals = traversals;
    }

    /**
     * Include the {@code Vertex} itself as a value in the enriched output which might be helpful if additional
     * traversing on that element is required.
     */
    public static Enrichment vertex() {
        return new Enrichment(KV.KEY_VERTEX, __.identity());
    }

    /**
     * The number of incoming edges on the {@code Vertex}.
     */
    public static Enrichment inDegree() {
        return new Enrichment(KV.KEY_IN_DEGREE, __.inE().count());
    }

    /**
     * The number of outgoing edges on the {@code Vertex}.
     */
    public static Enrichment outDegree() {
        return new Enrichment(KV.KEY_OUT_DEGREE, __.outE().count());
    }

    /**
     * The total number of in and out edges on the {@code Vertex}.
     */
    public static Enrichment degree() {
        return new Enrichment(KV.KEY_DEGREE, __.bothE().count());
    }

    /**
     * Calculates the edge label distribution for the {@code Vertex}.
     */
    public static Enrichment distribution() {
        return new Enrichment(KV.KEY_DISTRIBUTION, __.bothE().groupCount().by(T.label));
    }

    /**
     * Chooses the keys to include in the output.
     */
    public static Enrichment keys(String... propertyKeys) {
        List<GraphTraversal> projectTraversals = Stream.of(propertyKeys).map(__::values).collect(Collectors.toList());
        return new Enrichment(Arrays.asList(propertyKeys), projectTraversals);
    }

    public List<String> getProjectedKey() {
        return projectedKey;
    }

    public List<GraphTraversal> getTraversals() {
        return traversals.stream().map(t -> t.asAdmin().clone()).collect(Collectors.toList());
    }
}

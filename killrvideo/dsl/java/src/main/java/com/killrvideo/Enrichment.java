package com.killrvideo;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.structure.T;

import java.util.Map;

/**
 * Provides for pre-built data enrichment options for the {@link KillrVideoTraversalDsl#enrich(Enrichment...)}
 * step. These options will include extra information about the {@code Vertex} when output from that step. Note that
 * the enrichment examples presented here are examples to demonstrate this concept. The primary lesson here is to show
 * how one might merge map results as part of a DSL.These enrichment options may not be suitable for traversals in
 * production systems as counting all edges might add an unreasonable amount of time to an otherwise fast traversal.
 */
public enum Enrichment {

    /**
     * Include the {@code Vertex} itself as a value in the enriched output which might be helpful if additional
     * traversing on that element is required.
     */
    VERTEX(__.project("_vertex").by()),

    /**
     * The number of incoming edges on the {@code Vertex}.
     */
    IN_DEGREE(__.project("_inDegree").by(__.inE().count())),

    /**
     * The number of outgoing edges on the {@code Vertex}.
     */
    OUT_DEGREE(__.project("_outDegree").by(__.outE().count())),

    /**
         * The total number of in and out edges on the {@code Vertex}.
     */
    DEGREE(__.project("_degree").by(__.bothE().count())),

    /**
     * Calculates the edge label distribution for the {@code Vertex}.
     */
    DISTRIBUTION(__.project("_distribution").by(__.bothE().groupCount().by(T.label)));

    private Traversal<Object, Map<String,Object>> t;

    Enrichment(Traversal<Object, Map<String,Object>> t) {
        this.t = t;
    }

    public Traversal<Object, Map<String,Object>> getTraversal() {
        return t.asAdmin().clone();
    }
}

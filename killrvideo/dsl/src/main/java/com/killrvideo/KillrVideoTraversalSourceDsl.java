package com.killrvideo;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategies;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.GremlinDsl;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalHelper;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import static com.killrvideo.KV.KEY_TITLE;
import static com.killrvideo.KV.KEY_USER_ID;
import static com.killrvideo.KV.VERTEX_MOVIE;
import static com.killrvideo.KV.VERTEX_USER;

/**
 *
 */
public class KillrVideoTraversalSourceDsl extends GraphTraversalSource {

    public KillrVideoTraversalSourceDsl(final Graph graph, final TraversalStrategies traversalStrategies) {
        super(graph, traversalStrategies);
    }

    public KillrVideoTraversalSourceDsl(final Graph graph) {
        super(graph);
    }

    /**
     *
     */
    public GraphTraversal<Vertex, Vertex> movies(String... titles) {
        GraphTraversal traversal = this.clone().V();
        traversal = traversal.hasLabel(VERTEX_MOVIE);
        if (titles.length == 1)
            traversal = traversal.has(KEY_TITLE, titles);
        else if (titles.length > 1)
            traversal = traversal.has(KEY_TITLE, P.within(titles));

        return traversal;
    }

    public GraphTraversal<Vertex, Vertex> users(String... userIds) {
        GraphTraversal traversal = this.clone().V();
        traversal = traversal.hasLabel(VERTEX_USER);
        if (userIds.length == 1)
            traversal = traversal.has(KEY_USER_ID, userIds);
        else if (userIds.length > 1)
            traversal = traversal.has(KEY_USER_ID, P.within(userIds));

        return traversal;
    }
}

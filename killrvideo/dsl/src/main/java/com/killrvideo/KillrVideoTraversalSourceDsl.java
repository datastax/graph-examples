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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.killrvideo.KV.KEY_TITLE;
import static com.killrvideo.KV.KEY_USER_ID;
import static com.killrvideo.KV.VERTEX_MOVIE;
import static com.killrvideo.KV.VERTEX_USER;

/**
 * The KillrVideo DSL definition that produces the custom {@code GraphTraversalSource}.
 * <p/>
 * The DSL definition must be a class that extends {@code GraphTraversalSource} and should be referenced in the
 * {@code GremlinDsl} annotation on the {@code GraphTraversal} extension - in this example
 * {@link KillrVideoTraversalDsl}. The methods on this class will be exposed with the other traversal start steps
 * on {@code GraphTraversalSource}.
 */
public class KillrVideoTraversalSourceDsl extends GraphTraversalSource {

    public KillrVideoTraversalSourceDsl(final Graph graph, final TraversalStrategies traversalStrategies) {
        super(graph, traversalStrategies);
    }

    public KillrVideoTraversalSourceDsl(final Graph graph) {
        super(graph);
    }

    /**
     * Gets movies by their title.
     */
    public GraphTraversal<Vertex, Vertex> movies(String title, String... additionalTitles) {
        List<String> titles = Stream.concat(Stream.of(title), Stream.of(additionalTitles)).collect(Collectors.toList());
        GraphTraversal traversal = this.clone().V();
        traversal = traversal.hasLabel(VERTEX_MOVIE);
        if (titles.size() == 1)
            traversal = traversal.has(KEY_TITLE, titles.get(0));
        else if (titles.size() > 1)
            traversal = traversal.has(KEY_TITLE, P.within(titles));

        return traversal;
    }

    /**
     * Gets users by their identifier.
     */
    public GraphTraversal<Vertex, Vertex> users(String userId, String... additionalUserIds) {
        List<String> userIds = Stream.concat(Stream.of(userId), Stream.of(additionalUserIds)).collect(Collectors.toList());
        GraphTraversal traversal = this.clone().V();
        traversal = traversal.hasLabel(VERTEX_USER);
        if (userIds.size() == 1)
            traversal = traversal.has(KEY_USER_ID, userIds.get(0));
        else if (userIds.size() > 1)
            traversal = traversal.has(KEY_USER_ID, P.within(userIds));

        return traversal;
    }
}

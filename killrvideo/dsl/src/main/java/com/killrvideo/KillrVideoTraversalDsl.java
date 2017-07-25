package com.killrvideo;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.GremlinDsl;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.process.traversal.P;

import java.util.Map;

import static com.killrvideo.KV.EDGE_ACTOR;
import static com.killrvideo.KV.EDGE_RATED;
import static com.killrvideo.KV.KEY_AGE;
import static com.killrvideo.KV.KEY_RATING;
import static com.killrvideo.KV.VERTEX_PERSON;
import static org.apache.tinkerpop.gremlin.process.traversal.Order.decr;
import static org.apache.tinkerpop.gremlin.process.traversal.P.gt;
import static org.apache.tinkerpop.gremlin.process.traversal.P.lt;
import static org.apache.tinkerpop.gremlin.process.traversal.Scope.local;
import static org.apache.tinkerpop.gremlin.structure.Column.keys;
import static org.apache.tinkerpop.gremlin.structure.Column.values;

/**
 * The KillrVideo DSL definition that produces the custom {@code GraphTraversal} and specifies the custom
 * {@code GraphTraversalSource}.
 * <p/>
 * A DSL definition must be an interface and extend {@code GraphTraversal.Admin} and should be annotated with the
 * {@codd GremlinDsl} annotation. Methods that are added to this interface become steps that are "appended" to the
 * common steps of the Gremlin language. These methods must:
 * <ul>
 *     <li>Return a {@code GraphTraversal}</li>
 *     <li>Use common Gremlin steps or other DSL steps to compose the returned {@code GraphTraversal}</li>
 * </ul>
 * These methods are only applied to a {@code GraphTraversal}, but recall that a {@code GraphTraversal} is spawned
 * from a {@code GraphTraversalSource}. To be clear, the "g" in {@code g.V()} is a {@code GraphTraversalSource} and
 * the {@code V()} is a start step. To include DSL-based start steps on a custom {@code GraphTraversalSource} the
 * "traversalSource" parameter is supplied to the {@code GremlinDsl} annotation which specifies the fully qualified
 * name of the class that contains those DSL-based start steps.
 */
@GremlinDsl(traversalSource = "com.killrvideo.KillrVideoTraversalSourceDsl")
public interface KillrVideoTraversalDsl<S, E> extends GraphTraversal.Admin<S, E> {

    /**
     * Traverses from a "movie" to an "person" over the "actor" edge.
     */
    public default GraphTraversal<S, Vertex> actors() {
        return out(EDGE_ACTOR).hasLabel(VERTEX_PERSON);
    }

    /**
     * Traverses from a "movie" to a "rated" edge.
     */
    public default GraphTraversal<S, Edge> ratings() {
        return inE(EDGE_RATED);
    }

    /**
     * Calls {@link #rated(int, int)} with both arguments as zero.
     */
    public default GraphTraversal<S, Vertex> rated() {
        return rated(0,0);
    }

    /**
     * Traverses from a "user" to a "movie" over the "rated" edge, filtering those edges as specified. If both arguments
     * are zero then there is no rating filter.
     *
     * @param min minimum rating to consider
     * @param max maximum rating to consider
     */
    public default GraphTraversal<S, Vertex> rated(int min, int max) {
        if (min < 0 || max > 10) throw new IllegalArgumentException("min must be a value between 0 and 10");
        if (max < 0 || max > 10) throw new IllegalArgumentException("min must be a value between 0 and 10");
        if (min != 0 && max != 0 && min > max) throw new IllegalArgumentException("min cannot be greater than max");

        if (min == 0 && max == 0)
            return out(EDGE_RATED);
        else if (min == 0)
            return outE(EDGE_RATED).has(KEY_RATING, gt(min)).inV();
        else if (max == 0)
            return outE(EDGE_RATED).has(KEY_RATING, lt(min)).inV();
        else
            return outE(EDGE_RATED).has(KEY_RATING, P.between(min, max)).inV();
    }

    /**
     * Assumes incoming "rated" edges and filters based on the age of the "user" enforcing the logic that the
     * {@code start} age should exclude minors (i.e. 18 and older).
     *
     * @param start the start age which must be 18 or greater
     * @param end the end age
     * @return
     */
    public default GraphTraversal<S, Map<Object,Object>> byAges(int start, int end) {
        if (start < 18) throw new IllegalArgumentException("Age must be 18 or older");
        if (start > end) throw new IllegalArgumentException("Start age must be greater than end age");
        if (end > 120) throw new IllegalArgumentException("Now you're just being crazy");

        return filter(__.outV().has(KEY_AGE, P.between(start,end))).group().by(KEY_RATING).by(__.count());
    }

    /**
     * A simple recommendation algorithm that starts from a "user" and examines movies the user has seen filtered by
     * the {@code minRating} which removes movies that hold a rating lower than the value specified. It then samples
     * the actors in the movies the user has seen and uses that to find other movies those actors have been in that
     * the user has not yet seen. Those movies are grouped, counted and sorted based on that count to produce the
     * recommendation.
     *
     * @param recommendations the number of recommended movies to return
     * @param minRating the minimum rating to allow for
     */
    public default GraphTraversal<S, Vertex> recommend(int recommendations, int minRating) {
        if (recommendations <= 0) throw new IllegalArgumentException("recommendations must be greater than zero");

        return rated(minRating, 0).
                aggregate("seen").
                local(__.outE(EDGE_ACTOR).sample(3).inV().fold()).
                unfold().in(EDGE_ACTOR).where(P.without("seen")).
                groupCount().
                order(local).
                  by(values, decr).
                limit(local,recommendations).
                select(keys).
                unfold();
    }
}

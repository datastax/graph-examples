package com.killrvideo;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;

import java.util.List;
import java.util.Map;

import static com.killrvideo.KV.KEY_TITLE;

/**
 *
 */
public final class KillrVideoApp {
    public static void main(String [] args) {
        Graph graph = EmptyGraph.instance();

        Cluster cluster = Cluster.open();
        try {
            DriverRemoteConnection conn = DriverRemoteConnection.using(Cluster.open(), "killrvideo.g");
            KillrVideoTraversalSource killr = graph.traversal(KillrVideoTraversalSource.class).withRemote(conn);

            System.out.println("Actors for Young Guns");
            List names = killr.movies("Young Guns").actors().values("name").toList();
            names.forEach(System.out::println);

            System.out.println("Ratings by age for Young Guns");
            Map ratingsByAge = killr.movies("Young Guns").ratings().byAges(18, 40).next();
            System.out.println(ratingsByAge);

            System.out.println("Failed Validation");
            try {
                killr.movies("Young Guns").ratings().byAges(17,40).next();
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                Thread.sleep(500);
            }

            System.out.println("Five Recommendations for u460");
            killr.users("u460").recommend(5, 7).values(KEY_TITLE).forEachRemaining(System.out::println);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            cluster.close();
            System.exit(0);
        }


    }
}

package com.killrvideo;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.BasicConfigurator;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;

import java.util.Collections;
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

            printHeader("Actors for Young Guns", "killr.movies(\"Young Guns\").actors().values(\"name\")");
            List names = killr.movies("Young Guns").actors().values("name").toList();
            names.forEach(System.out::println);

            printHeader("Ratings by age for Young Guns", "killr.movies(\"Young Guns\").ratings().byAges(18, 40)");
            Map ratingsByAge = killr.movies("Young Guns").ratings().byAges(18, 40).next();
            System.out.println(ratingsByAge);

            printHeader("Failed Validation", "killr.movies(\"Young Guns\").ratings().byAges(17,40)");
            try {
                killr.movies("Young Guns").ratings().byAges(17,40).next();
            } catch (IllegalArgumentException iae) {
                System.out.println("Caught IllegalArgumentException: " + iae.getMessage());
                Thread.sleep(500);
            }

            printHeader("Five Recommendations for u460", "killr.users(\"u460\").recommend(5, 7).values(KEY_TITLE)");
            killr.users("u460").recommend(5, 7).values(KEY_TITLE).forEachRemaining(System.out::println);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            cluster.close();
            System.exit(0);
        }
    }

    private static void printHeader(String title) {
        printHeader(title, null);
    }

    private static void printHeader(String title, String subtitle) {
        String st = "";
        String t = String.format("\n* %s", title);
        System.out.println(t);
        if (subtitle != null && !subtitle.isEmpty()) {
            st = String.format("[%s]", subtitle);
            System.out.println(st);
        }

        String line = String.join("", Collections.nCopies(st.isEmpty() ? t.length() : st.length(), "-"));
        System.out.println(line);
    }

}

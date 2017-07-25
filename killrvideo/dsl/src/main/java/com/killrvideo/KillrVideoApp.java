package com.killrvideo;

import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.killrvideo.KV.KEY_TITLE;

/**
 * A simple console application which demonstrates some of the features of the KillrVideo DSL.
 */
public final class KillrVideoApp {

    public static void main(String [] args) {
        Graph graph = EmptyGraph.instance();

        Cluster cluster = Cluster.open();
        try {
            DriverRemoteConnection conn = DriverRemoteConnection.using(Cluster.open(), "killrvideo.g");
            KillrVideoTraversalSource killr = graph.traversal(KillrVideoTraversalSource.class).withRemote(conn);

            printHeader("Actors for Young Guns", "killr.movies(\"Young Guns\").actors().values(\"name\")");
            List<Object> names = killr.movies("Young Guns").actors().values("name").toList();
            names.forEach(System.out::println);

            printHeader("Ratings Distribution by Age for Young Guns", "killr.movies(\"Young Guns\").ratings().byAges(18, 40)");
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

            printHeader("Add a movie and a actors for that movie", "killr.ensureMovie(\"m100000\", \"Manos: The Hands of Fate\",...).ensureActor(...)");
            killr.ensureMovie("m100000", "Manos: The Hands of Fate", "USA", "Sun City Films", 1966, 70).
                    ensureActor("p1000000", "Tom Neyman").
                    ensureActor("p1000001", "John Reynolds").
                    ensureActor("p1000002", "Diane Mahree").
                    forEachRemaining(v -> System.out.println("Added 3 actors to 'Manos: The Hands of Fate'"));

            printHeader("Get the actors for the newly added movie", "killr.movies(\"Manos: The Hands of Fate\").actors().values(\"name\")");
            names = killr.movies("Manos: The Hands of Fate").actors().values("name").toList();
            names.forEach(System.out::println);

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

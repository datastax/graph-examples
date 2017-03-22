package com.datastax.examples.northwind;

import com.datastax.driver.dse.DseCluster;
import com.datastax.driver.dse.DseSession;
import com.datastax.driver.dse.graph.GraphOptions;
import com.datastax.driver.dse.graph.GraphResultSet;
import com.datastax.driver.dse.graph.SimpleGraphStatement;
import com.datastax.driver.dse.graph.Vertex;

public class Northwind {

    public static void main(String [] args) throws Exception {

        DseCluster dseCluster = DseCluster.builder()
                .addContactPoint("localhost")
                .withGraphOptions(new GraphOptions().setGraphName("northwind"))
                .build();

        DseSession dseSession = dseCluster.connect();

        basicAdd(dseSession);

        System.exit(0);
    }

    /**
     * Create a vertex and edge using the synchronous methods
     */
    private static void basicAdd(DseSession dseSession) {

        String name = "Catherine Dewey";
        int age = 38;

        dseSession.executeGraph(
                new SimpleGraphStatement(
                        "g.addV(label, 'networkMember', 'name', name, 'age', age)")
                        .set("name", name)
                        .set("age", age)
        );

        // Add edge between two existing vertices
        dseSession.executeGraph(
                new SimpleGraphStatement(
                        "customer = g.V().has('customer', 'name', name).next();" +
                                "networkMember = g.V().has('networkMember', 'name', name).next();" +
                                "customer.addEdge('isMember', networkMember);")
                        .set("name", name)
        );

        // Check to make sure the vertex was added
        GraphResultSet rs = dseSession.executeGraph(
                new SimpleGraphStatement(
                        "g.V().has('networkMember', 'name', name)")
                        .set("name", name)
        );
        Vertex v = rs.one().asVertex();

        System.out.println(v);
    }

}

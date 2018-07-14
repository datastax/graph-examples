/**
 * The FraudSample class provides simply demonstrates connecting to a DSE cluster
 * using the DataStax Enterprise Java Driver and adding two vertices and an edge.
 *
 * Important Assumptions:  Before running the sample code, make sure you run the Notebook
 * Exercises in the fraud asset, specifically schema creation and data load.  This code
 * assumes the graph (named fraud) and schema is already created.
 *
 * This code example will simply add 2 vertices and and edge.  It will
 * add a customer and address vertex and establish the relationship 'hasAddress' between
 * them.  The sample code will also retrieve the email address for that customer as a way
 * to validate the insert.  It is recommended that after running the code that you retrieve the
 * full address for the new customer in DataStax Studio.
 * The customer is "10000000-0000-0000-0000-0000000000800".
 *
 */
package com.datastax.fraud;

import com.datastax.driver.dse.*;
import com.datastax.driver.dse.graph.*;
import com.datastax.dse.graph.api.DseGraph;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FraudSample {
    private Logger logger = LoggerFactory.getLogger(FraudSample.class);
    private DseSession _dseSession=null;
    private DseCluster _dseCluster=null;
    private GraphTraversalSource g;
    private String _contactPoint;
    private String _customerID = "10000000-0000-0000-0000-0000000000800";

    public FraudSample (String contactPoint) {
        /*
        Typically, multiple contact points are specified since the DSE Cluster is
        always available.  The driver needs only to connect to one node and the entire
        cluster (all nodes) are available to it.  If a node is added/removed/down the driver is
        aware of the topology changes or if a nodes goes down.
         */

        _contactPoint = contactPoint;
        connectCluster();
    }
    public static void main(String[] args) {
        System.out.println("Hello, This FraudSample code using DSE Graph");
        Logger logger = LoggerFactory.getLogger(FraudSample.class);
        String contactPoint="node0";
        if (args.length >0 ){
            contactPoint = args[0];
        }

        FraudSample fraudSample = new FraudSample(contactPoint);
        fraudSample.writeCustomer();
        fraudSample.readCustomer();
        fraudSample.closeCluster();
        logger.info("Exiting, status = 0");
        System.exit(0);

    }
    private void readCustomer() {
        logger.info("Can we find the new customer address?");
        GraphResultSet results =_dseSession.executeGraph(DseGraph.statementFromTraversal(g.V()
                .has("customer", "customerid", _customerID)
                .out("hasAddress").valueMap()));

        logger.info("Validate Customer Address: " + results.one());
        logger.info("Finished reading data for customer: " + _customerID);
    }

    private void closeCluster() {
        _dseCluster.close();
    }

    private void connectCluster(){
        String graphName = "fraud";
        try {
            _dseCluster = DseCluster.builder()
                    .addContactPoint(_contactPoint)
                    .withGraphOptions(new GraphOptions()
                            .setGraphName(graphName))
                    .build();
            logger.error("Connecting to cluster using: " + _contactPoint);
            _dseSession = _dseCluster.connect();
        } finally {
            if (_dseSession == null) {
                logger.error("Error connecting to cluster:" + _contactPoint);
                _dseCluster.close();
                System.exit(1);
            }
        }

        g = DseGraph.traversal(_dseSession);

    }
    private void writeCustomer() {
        /*
        We will use the Graph Fluent API
         */

        logger.info("Inserting Customer and Address Vertices for customer: " + _customerID);

        GraphTraversal traversal = g.addV("customer")
                .property("customerid", _customerID)
                .property("createdtime", "2018-07-11T00:00:00Z")
                .property("email","TinaTully@yahoo.com" )
                .property("firstname", "Tina")
                .property("lastname", "Tully")
                .as("customer")
                .addV( "address")
                .property("address", "21 Madeup Road")
                .property("city", "Boston")
                .property("postalcode", "02111")
                .property("countrycode", "US")
                .property("state", "MA")
                .as("address")
                .addE("hasAddress")
                .from("customer")
                .to("address");

        GraphStatement statement = DseGraph.statementFromTraversal(traversal);
            _dseSession.executeGraph(statement);
        logger.info("Successfully finished inserting customer: " + _customerID);
    }
}

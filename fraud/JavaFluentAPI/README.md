# graph-fraud
Just a simple Graph Fluent API example using a simple Fraud schema defined elsewhere
The application will default to connecting to a DataStax Enterprise cluster using node0 and
insert 2 vertices and an edge between them.  The app will then execute a traversal to return the
address for the customer just added.

Invoke by connecting to node0 and invoking fraud.jar as follows:

java -jar fraud.jar

or optionally specify an the local ip  address



# graph-fraud
Just a simple Graph Fluent API example using a simple Fraud schema defined elsewhere
The application will default to connecting to a DataStax Enterprise cluster using node0 and
insert 2 vertices and an edge between them.  The app will then execute a traversal to return the
address for the customer just added.

To build, execute the following in the JavaFluentAPI directory::

```
./build-example.sh
```

Invoke as follows in the JavaFluentAPI directory (by default it connects to `node0):

```
./run-example.sh
```

or optionally specify a hostname or ip address:

```
./run-example.sh 1.2.3.4
```

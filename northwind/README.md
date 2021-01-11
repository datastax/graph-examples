| :exclamation: This example only works for classic graph databases   |
|----------------------------------------|

# Northwind

The Northwind graph example is a subset of Microsoft's Northwind dataset found [here](https://northwinddatabase.codeplex.com).
The data has been prepared and serialized with the [Kryo](http://tinkerpop.apache.org/docs/current/reference/#gryo-reader-writer) format in the data directory.  It is to be used as a learning tool for 
translating SQL queries to gremlin traversals as featured on the [sql2gremlin](http://sql2gremlin.com) page.  Once you've loaded the data, you will be able to run the various example traversals from the *sql2gremlin* page in Studio or on the gremlin console.
However, while using DSE Graph in Development mode, you'll have to explicitly enable use of scans and lambdas to run the examples that require those:

```
    schema.config().option('graph.allow_scan').set('true')
    graph.schema().config().option('graph.traversal_sources.g.restrict_lambda').set(false)
```
<sub>Note that scans are disabled by default for performance reasons; lambdas are disabled by default for both performance reasons and security concerns.</sub>

You can choose to let it create the schema for you (default) or you can create the schema explicitly (recommended for Production) and
load the data with `create_schema` set to **false** in the script or from the command-line.

## Datamodel visualization

View the live schema visualization <a href="https://s3.amazonaws.com/datastax-graph-schema-viewer/index.html#/?schema=northwind.json" target="_blank">here</a>
[![datamodel screenshot](datamodel-screenshot.png)](https://s3.amazonaws.com/datastax-graph-schema-viewer/index.html#/?schema=northwind.json)<br/>

## Create the schema
You can create the schema several different ways both explicitly and implcitly

### Explicit - Design Time
The schema is defined in `schema.groovy`.
* You can create your graph in Studio and copy and paste the schema statements to run there.  
* Alternately, the statements can be run from the gremlin console.

Notes
* If using studio, then set the notebook cell mode to `graph` instead of `cql`. 
* If you create the graph inside DSE Studio then you should create the graph DB with the "classic" engine.

### Implicit - Load Time via graphloader
| :exclamation: graphloader only works for classic graph databases   |
|----------------------------------------|

The `northwind-mapping.groovy` config file has automated schema creation disabled. The assumption is that you will create the schema with `schema.groovy` or something like it.

The data loading utility [graphloader](https://downloads.datastax.com/#graph-loader) can create the schema as part of the data load. 
1 Edit the `northwind-mapping.groovy` file.  Change the following line.
  * `config create_schema: false` 
to 
  * `config create_schema: true`

## Load Exmaple Data
| :exclamation:  graphloader only works for classic graph databases   |
|-----------------------------------------|


(graphloader)[https://docs.datastax.com/en/dse/6.7/dse-dev/datastax_enterprise/graph/dgl/graphloaderTOC.html] must be downloaded separately from this example. Download the version that matches your DSE/graph version. 
* Via (Datastax graphloader downloads)[https://downloads.datastax.com/#graph-loader] . 
* Via `curl https://downloads.datastax.com/enterprise/dse-graph-loader-6.8.1-bin.tar.gz -o dse-graph-loader-6.8.1-bin.tar.gz`

If you load the Kryo file from within the northwind directory, you don't need to specify the path.  It will
default to the data subdirectory to get the northwind.kryo file.  Otherwise, specify the full path with the `inputfile` parameter.

Examples of loading the northwind data:

```
# From the northwind directory
graphloader -graph northwind -address localhost northwind-mapping.groovy
```

```
# Alternatively, explicitly specify where the data files are
graphloader -graph northwind -address localhost northwind-mapping.groovy -inputpath ~/graph-examples/northwind/data/
```

## Supplemental data

** Supplemental data is not currently working as we change to custom vertex ids **

Some supplemental data has been added in csv files to provide some more connectivity within the data.  It is generated data,
that includes things like relationships between customers (isRelatedTo and isFriendsWith), customer product ratings (rated),
and so forth.  The relationships include the relationship type, the friendships include an affinity score, and the identities
come with a confidence level to make the relationships more interesting to play with.

Examples of loading the supplemental data:

```
# From the northwind directory
graphloader -graph northwind -address localhost supplemental-data-mapping.groovy
```

```
# Alternatively, explicitly specify where the data files are
graphloader -graph northwind -address localhost supplemental-data-mapping.groovy -inputpath ~/graph-examples/northwind/data/
```

## Dropping the Schema
You can drop the schema to reset the environment
```
schema.drop()
```
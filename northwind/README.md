# Northwind

The Northwind graph example is a subset of Microsoft's Northwind data set found [here](https://northwinddatabase.codeplex.com).
The data has been prepared and serialized in the kryo format in the data directory.  It is meant as to be a learning tool to 
translate from sql queries to gremlin traversals and is featured on the [sql2gremlin](http://sql2gremlin.com).  Once you've loaded the
data, you should be able to run the various example traversals from the sql2gremlin page in Studio or on the gremlin console.
However with DSE Graph, you'll have to explicitly enable use of scans and lambdas to run the examples that require those.

You can choose to let it create the schema for you (default) or create the schema explicitly (recommended for production) and
then load the data with `create_schema` set to false in the script or from the command-line.

![Northwind Data Model](https://github.com/datastax/graph-examples/northwind/datamodel-screenshot.png)

## Example loading

You can load the kryo file from within the northwind directory and you don't need to specify the path.  It will
default to the data subdirectory to get the northwind.kryo file.  Otherwise, specify the full path with the
`inputfile` parameter.

Examples of loading the northwind data:

```
# From the northwind directory
graphloader -graph northwind -address localhost northwind-mapping.groovy
```

```
graphloader -graph northwind -address localhost northwind-mapping.groovy -inputpath ~/repos/graph-examples/northwind/data/
```

## Supplemental data

Some supplemental data has been added in csv files to give some more connectivity to the data.  It's generated data
that includes things like relationships between customers (isRelatedTo and isFriendsWith), customer product ratings (rated),
and so forth.  The relationships include the relationship type, the friendships include an affinity score, and the identities
come with a confidence level to make the relationships more interesting to play with.

Examples of loading the supplemental data:

```
# From the northwind directory
graphloader -graph northwind -address localhost supplemental-data-mapping.groovy
```

```
graphloader -graph northwind -address localhost supplemental-data-mapping.groovy -inputpath ~/repos/graph-examples/northwind/data/
```
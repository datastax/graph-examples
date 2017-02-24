# Northwind

The Northwind graph example is a subset of Microsoft's Northwind data set found [here](https://northwinddatabase.codeplex.com).
It is featured on the [sql2gremlin](http://sql2gremlin.com) site and the kryo file is from there.  Once you've loaded the
data, you should be able to run the various example traversals from the sql2gremlin page.  However with DSE Graph, you'll
have to explicitly enable use of scans and lambdas to run the examples that require those.

## Example loading

You can load the kryo file from within the northwind directory and you don't need to specify the path.  It will
default to the data subdirectory to get the northwind.kryo file.  Otherwise, specify the full path with the
`inputfile` parameter.

Examples:

```
graphloader -graph northwind -address localhost northwind-mapping.groovy
```

```
graphloader -graph northwind -address localhost northwind-mapping.groovy -inputpath ~/repos/graph-examples/northwind/data/
```

## Supplemental data

To be documented...
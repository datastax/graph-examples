# Fraud

This is a fraud detection use case using a graph intended for the financial industry.

## Create the schema

Included is a `schema.groovy` file.  You can create your graph in Studio and copy and paste the schema statements
to run there.  Alternately, the statements can be run from the gremlin console.

## Example loading

Example of loading the fraud data:

```
# From the fraud directory
graphloader -graph fraud -address localhost fraud-mapping.groovy
```

```
# Alternatively, explicitly specify where the data files are
graphloader -graph fraud -address localhost fraud-mapping.groovy -inputpath ~/graph-examples/fraud/data/
```
# graph-examples

This repo contains a collection of graph examples.  The intent is to provide more complete and extensive examples than what is reasonable to include in DataStax documentation or blogposts.  This will include DataStax Graph Loader mapping scripts, schemas, example traversals, things to try in DataStax Studio, and application code examples.  Feel free to use and modify any of these for your own purposes.  There is no warranty or implied official support, but hopefully the examples will be useful as a starting point to show various ways of loading and experimenting with graph data.  And if you see anything that could be improved or added, issue reports and pull requests are always welcome!

Download DSE (includes DSE Graph), DataStax Studio, and the DSE Graph Loader at https://academy.datastax.com/downloads.

### Additional datasets

For additional interesting datasets, you might consider the following resources:

- [Stanford Large Network Dataset Collection](https://snap.stanford.edu/data/) - a collection of connected data in various categories such as social data, web graphs, product co-purchasing networks, review data, etc.
- [Awesome Public Datasets](https://github.com/caesar0301/awesome-public-datasets) - a github repository with links to collections, large and small, of interesting public data.  These are further categorized by industry and data type such as energy, government, machine learning, time series, sports data, transportation, etc.
- [CRAWDAD](http://crawdad.org/) - the Community Resource for Archiving Wireless Data At Dartmouth.  This includes wireless network trace data from a variety of sources.

### Graph Resources and Help:

- DataStax Enterprise Graph [documentation](http://docs.datastax.com/en/latest-dse/datastax_enterprise/graph/graphTOC.html)
- DataStax Academy [DS330 course](https://academy.datastax.com/resources/ds330-datastax-enterprise-graph) on DSE Graph, a self-paced course covering both DSE Graph and Gremlin as a traversal language.
- Apache TinkerPop [documentation](http://tinkerpop.apache.org/docs/current/reference/), [getting started tutorial](http://tinkerpop.apache.org/docs/current/tutorials/getting-started/), and [common recipes](http://tinkerpop.apache.org/docs/current/recipes/)
- [DataStax Academy Slack](https://academy.datastax.com/slack) in the dse-graph channel
- Stack overflow with the [datastax-enterprise/datastax-enterprise-graph](http://stackoverflow.com/questions/tagged/datastax-enterprise+datastax-enterprise-graph) tag
- [DataStax support portal](https://support.datastax.com) (if you are a DataStax customer)

### Other posts and resources

- [Getting started with the DataStax Graph Loader](http://www.datastax.com/dev/blog/dgl-basics)
- [Gremlin's Time Machine](https://www.datastax.com/dev/blog/gremlins-time-machine) - a post about how to use the TinkerPop SubgraphStrategy to traverse your graph at a specific version or time in its history.
- [graphoendodonticology](https://www.datastax.com/2017/03/graphoendodonticology) - a resource to troubleshoot your graph
- [Reducing Computational Complexity with Correlate Traversals](https://www.datastax.com/2017/04/reducing-computational-complexity-with-correlate-traversals)
- Large graph loading best practices, [part 1](http://www.datastax.com/dev/blog/large-graph-loading-best-practices-strategies-part-1) and [part 2](http://www.datastax.com/dev/blog/large-graph-loading-tactics-part-2)
- Fighting Fraud with Graph Databases Webinar [recording](https://www.youtube.com/watch?v=H5MmSL1c9Zs) and [slides](https://www.slideshare.net/DataStax/webinar-fighting-fraud-with-graph-databases).  Presented by DataStax and Cambridge Intelligence.
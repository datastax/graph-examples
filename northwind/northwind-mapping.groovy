//Configures the data loader to create the schema
config create_schema: true, load_new: true

def inputpath = '/Users/jeremy/advanced-graph-training/northwind/data/';
def inputfile = inputpath + 'northwind.kryo';

//Defines the data input source (a file which is specified via command line arguments)
source = Graph.file(inputfile).gryo()

//Specifies what data source to load using which mapper
load(source.vertices()).asVertices {
    labelField "~label"
    key "~id", "id"
}

load(source.edges()).asEdges {
    labelField "~label"
    outV "outV", {
        labelField "~label"
        key "~id", "id"
    }
    inV "inV", {
        labelField "~label"
        key "~id", "id"
    }
}

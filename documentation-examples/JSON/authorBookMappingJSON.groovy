/* SAMPLE INPUT
author: {"author_name":"Julia Child","gender":"F"}
book : {"name":"The Art of French Cooking, Vol. 1","year":"1961","ISBN":"none"}
authorBook: {"name":"The Art of French Cooking, Vol. 1","author":"Julia Child"}
 */

// CONFIGURATION
// Configures the data loader to create the schema
config create_schema: false, load_new: true, load_vertex_threads: 3

// DATA INPUT
if (inputpath == '') {
    inputfileV = new java.io.File('.').getCanonicalPath() + '/data/vertices/'
    inputfileE = new java.io.File('.').getCanonicalPath() + '/data/edges/'
}
else {
    inputfileV = inputpath + '/vertices/'
    inputfileE = inputpath + '/edges/'
}

authorInput = File.json(inputfileV + 'author.json')
bookInput = File.json(inputfileV + 'book.json')
authorBookInput = File.json(inputfileE + 'authorBook.json')

//Specifies what data source to load using which mapper (as defined inline)
  
load(authorInput).asVertices {
    label "author"
    key "name"
}

load(bookInput).asVertices {
    label "book"
    key "name"
}

load(authorBookInput).asEdges {
    label "authored"
    outV "aname", {
        label "author"
        key "name"
    }
    inV "bname", {
        label "book"
        key "name"
    }
}

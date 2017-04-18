config load_new: true, create_schema: false

dir = "/tmp/tube/"

vertexInput = File.csv(dir + "london_vertices.csv").delimiter(",").header("id","name","zone","is_rail","lines")
edgesInput = File.csv(dir + "london_edges.csv").delimiter(",").header("from_id","to_id","line")

vextexInput = vertexInput.map{it["lines"] = it["lines"].toString().split("\\|"); it["zone"] = it["zone"].toString().toDouble(); it["id"] = it["id"].toString().toInteger(); it}
edgesInput = edgesInput.map{it["from_id"] = it["from_id"].toString().toInteger(); it["to_id"] = it["to_id"].toString().toInteger(); it}

load(vextexInput).asVertices {
	isNew()
	label "station"
	key "id"
}

load(edgesInput).asEdges {
	isNew()
	label "connectedTo"
	outV "from_id", {
		exists()
		label "station"
		key "id"
	}
	inV "to_id", {
                exists()
                label "station"
                key "id"
        }
}

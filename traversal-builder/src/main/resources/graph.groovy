g.V().drop().iterate()
g.addV("person").property("name", "marko").property("age", 29).as("marko").
  addV("person").property("name", "vadas").property("age", 27).as("vadas").
  addV("software").property("name", "lop").property("lang", "java").as("lop").
  addV("person").property("name", "josh").property("age", 32).as("josh").
  addV("software").property("name", "ripple").property("lang", "java").as("ripple").
  addV("person").property("name", "peter").property("age", 35).as("peter").
  addE("knows").from("marko").to("vadas").property("weight", 0.5).
  addE("knows").from("marko").to("josh").property("weight", 1.0).
  addE("created").from("marko").to("lop").property("weight", 0.4).
  addE("created").from("josh").to("ripple").property("weight", 1.0).
  addE("created").from("josh").to("lop").property("weight", 0.4).
  addE("created").from("peter").to("lop").property("weight", 0.2).
  addE("uses").from("peter").to("ripple").property("weight", 0.5).iterate()
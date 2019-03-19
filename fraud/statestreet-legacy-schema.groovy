// set RF=1 due to limited space
system.graph('statestreet').replication("{'class' : 'NetworkTopologyStrategy', 'SearchGraphAnalytics' : 1}").systemReplication("{'class' : 'NetworkTopologyStrategy', 'SearchGraphAnalytics' : 1}").create()
:remote config alias g statestreet.g

schema.propertyKey('superparent_id').Int().single().ifNotExists().create()
schema.propertyKey('superparent_name').Text().single().ifNotExists().create()


schema.propertyKey('codeA').Text().single().ifNotExists().create()


schema.propertyKey("codeB").Text().single().ifNotExists().create()
schema.propertyKey("vtype").Text().single().ifNotExists().create()
schema.propertyKey("gencode").Text().single().ifNotExists().create()

schema.propertyKey("parent_id").Int().single().ifNotExists().create()
schema.propertyKey("parent_name").Text().single().ifNotExists().create()
schema.propertyKey("domicile").Text().single().ifNotExists().create()

schema.propertyKey("referenced_parent_id").Int().single().ifNotExists().create()
schema.propertyKey("referenced_superparent_id").Int().single().ifNotExists().create()

schema.propertyKey("semiparent_id").Int().single().ifNotExists().create()
schema.propertyKey("semiparent_name").Text().single().ifNotExists().create()
schema.propertyKey("strategy").Text().single().ifNotExists().create()

schema.propertyKey("subparent_id").Int().single().ifNotExists().create()
schema.propertyKey("subparent_name").Text().single().ifNotExists().create()

schema.propertyKey("topchild_id").Int().single().ifNotExists().create()
schema.propertyKey("topchild_name").Text().single().ifNotExists().create()
schema.propertyKey("family").Text().single().ifNotExists().create()
schema.propertyKey("childvtype").Text().single().ifNotExists().create()

// Information Classification: General
schema.propertyKey("intcode").Int().single().ifNotExists().create()
schema.propertyKey("class").Text().single().ifNotExists().create()
schema.propertyKey("region").Text().single().ifNotExists().create()

schema.propertyKey("childnums_id").Int().single().ifNotExists().create()
schema.propertyKey("childnums_name").Text().single().ifNotExists().create()
schema.propertyKey("numsvtype").Text().single().ifNotExists().create()

schema.propertyKey("numsyear_id").Int().single().ifNotExists().create()
schema.propertyKey("vdate").Text().single().ifNotExists().create()

schema.propertyKey("numsmonth_id").Int().single().ifNotExists().create()

schema.propertyKey("nums_id").Int().single().ifNotExists().create()
schema.propertyKey("nums_name").Text().single().ifNotExists().create()
schema.propertyKey("number").Int().single().ifNotExists().create()

schema.propertyKey("valyear_id").Int().single().ifNotExists().create()

schema.propertyKey("valmonth_id").Int().single().ifNotExists().create()

schema.propertyKey("vals_id").Int().single().ifNotExists().create()
schema.propertyKey("vals_name").Text().single().ifNotExists().create()
schema.propertyKey("value").Double().single().ifNotExists().create()

// Information Classification: General

schema.propertyKey("connect_year").Text().single().ifNotExists().create()
schema.propertyKey("connect_month").Text().single().ifNotExists().create()
schema.propertyKey("connect_date").Text().single().ifNotExists().create()




schema.propertyKey("childitem_name").Text().single().ifNotExists().create()
schema.propertyKey("childitem_id").Int().single().ifNotExists().create()
schema.propertyKey("trade").Text().single().ifNotExists().create()

schema.propertyKey("vvalue").Double().single().ifNotExists().create()


schema.vertexLabel('superparent').partitionKey('superparent_id').properties('superparent_name','codeA','vtype','codeB','gencode').ifNotExists().create()


schema.vertexLabel('parent').partitionKey('parent_id').properties('parent_name','domicile','vtype','codeA').ifNotExists().create()


schema.vertexLabel('semiparent').partitionKey('semiparent_id').properties('semiparent_name','vtype','codeA','codeB','strategy').ifNotExists().create()

schema.vertexLabel('subparent').partitionKey('subparent_id').properties('subparent_name','codeA','codeB','vtype','strategy').ifNotExists().create()
schema.vertexLabel('topchild').partitionKey('topchild_id').properties('topchild_name','family','childvtype','intcode','class','region','codeA').ifNotExists().create()


schema.vertexLabel('childnums').partitionKey('childnums_id').properties('childnums_name', 'numsvtype').ifNotExists().create()
schema.vertexLabel('childnums').partitionKey('childnums_id').properties('childnums_name', 'numsvtype').ifNotExists().create()

schema.vertexLabel('numsyear').partitionKey('numsyear_id').properties('vdate').ifNotExists().create()
schema.vertexLabel('numsmonth').partitionKey('numsmonth_id').properties('vdate').ifNotExists().create()

schema.vertexLabel('nums').partitionKey('nums_id').properties('nums_name', 'number','vdate').ifNotExists().create()
schema.vertexLabel('childitem').partitionKey('childitem_id').properties('childitem_name','trade','vtype','region').ifNotExists().create()


schema.vertexLabel('valyear').partitionKey('valyear_id').properties('vdate').ifNotExists().create()
schema.vertexLabel('valmonth').partitionKey('valmonth_id').properties('vdate').ifNotExists().create()

schema.vertexLabel('vals').partitionKey('vals_id').properties('vals_name', 'vvalue','vdate').ifNotExists().create()



// Build the edges
// ------------------
schema.edgeLabel('superparent_parent').single().properties('referenced_superparent_id','referenced_parent_id','connect_date').ifNotExists().create()
schema.edgeLabel('superparent_parent').connection('superparent','parent').add()

schema.edgeLabel('parent_semiparent').single().properties('parent_id','semiparent_id','connect_date').ifNotExists().create()
schema.edgeLabel('parent_semiparent').connection('parent','semiparent').add()

schema.edgeLabel('semiparent_subparent').single().properties('semiparent_id','subparent_id','connect_date').ifNotExists().create()
schema.edgeLabel('semiparent_subparent').connection('semiparent','subparent').add()


schema.edgeLabel('subparent_topchild').single().properties('subparent_id','topchild_id','connect_date').ifNotExists().create()
schema.edgeLabel('subparent_topchild').connection('subparent','topchild').add()


schema.edgeLabel('topchild_childnums').single().properties('topchild_id','childnums_id','connect_date').ifNotExists().create()
schema.edgeLabel('topchild_childnums').connection('topchild', 'childnums').add()

schema.edgeLabel('childnums_numsyear').single().properties('childnums_id','numsyear_id','connect_year').ifNotExists().create()
schema.edgeLabel('childnums_numsyear').connection('childnums','numsyear').add()

schema.edgeLabel('numsyear_numsmonth').single().properties('numsyear_id','numsmonth_id','connect_month').ifNotExists().create()
schema.edgeLabel('numsyear_numsmonth').connection('numsyear', 'numsmonth').add()


schema.edgeLabel('numsmonth_nums').single().properties('numsmonth_id','nums_id','connect_date').ifNotExists().create()
schema.edgeLabel('numsmonth_nums').connection('numsmonth', 'nums').add()

schema.edgeLabel('childnums_childitem').single().properties('childnums_id','childitem_id','connect_date').ifNotExists().create()
schema.edgeLabel('childnums_childitem').connection('childnums', 'childitem').add()

schema.edgeLabel('childnums_numsyear').connection('childnums', 'numsyear').add()
schema.edgeLabel('valyear_valmonth').single().properties('valyear_id','valmonth_id','connect_month').ifNotExists().create()

schema.edgeLabel('valyear_valmonth').connection('valyear', 'valmonth').add()
schema.edgeLabel('valmonth_vals').single().properties('valmonth_id','vals_id','connect_date').ifNotExists().create()

schema.edgeLabel('valmonth_vals').connection('valmonth','vals').add()


schema.edgeLabel('childitem_valyear').single().properties('childitem_id','valyear_id','connect_year').ifNotExists().create()
schema.edgeLabel('childitem_valyear').connection('childitem','valyear').add()




// Define edge indexes
//schema.vertexLabel("person").index("toPersonByType").outE("relatedTo").by("relationshipType").ifNotExists().add();
//schema.vertexLabel("movie").index("toUsersByRating").inE("rated").by("relationshipType").add();


//schema.vertexLabel("numsmonth").index("numsmonth_numsEconnect_date").outE("numsmonth_nums").by("connect_date").ifNotExists().add();

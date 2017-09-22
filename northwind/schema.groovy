system.graph('northwind').ifNotExists().create()
:remote config alias g northwind.g

// Define the possible properties for any vertex or edge label
schema.propertyKey('id').Int().single().ifNotExists().create()

schema.propertyKey('customerId').Text().single().ifNotExists().create()
schema.propertyKey('name').Text().single().ifNotExists().create()
schema.propertyKey('title').Text().single().ifNotExists().create()
schema.propertyKey('address').Text().single().ifNotExists().create()
schema.propertyKey('city').Text().single().ifNotExists().create()
schema.propertyKey('postalCode').Text().single().ifNotExists().create()
schema.propertyKey('phone').Text().single().ifNotExists().create()
schema.propertyKey('fax').Text().single().ifNotExists().create()
schema.propertyKey('company').Text().single().ifNotExists().create()

schema.propertyKey('titleOfCourtesy').Text().single().ifNotExists().create()
schema.propertyKey('firstName').Text().single().ifNotExists().create()
schema.propertyKey('lastName').Text().single().ifNotExists().create()
schema.propertyKey('extension').Text().single().ifNotExists().create()
schema.propertyKey('homePhone').Text().single().ifNotExists().create()
schema.propertyKey('hireDate').Bigint().single().ifNotExists().create()
schema.propertyKey('notes').Text().single().ifNotExists().create()

schema.propertyKey('orderDate').Bigint().single().ifNotExists().create()
schema.propertyKey('shipName').Text().single().ifNotExists().create()
schema.propertyKey('shipAddress').Text().single().ifNotExists().create()
schema.propertyKey('shipCity').Text().single().ifNotExists().create()
schema.propertyKey('shipPostalCode').Text().single().ifNotExists().create()
schema.propertyKey('shippedDate').Bigint().single().ifNotExists().create()
schema.propertyKey('requiredDate').Bigint().single().ifNotExists().create()
schema.propertyKey('freight').Decimal().single().ifNotExists().create()

schema.propertyKey('unitPrice').Decimal().single().ifNotExists().create()
schema.propertyKey('discount').Decimal().single().ifNotExists().create()
schema.propertyKey('quantity').Int().single().ifNotExists().create()

schema.propertyKey('type').Text().single().ifNotExists().create()
schema.propertyKey('unitsInStock').Int().single().ifNotExists().create()
schema.propertyKey('unitsOnOrder').Int().single().ifNotExists().create()
schema.propertyKey('reorderLevel').Int().single().ifNotExists().create()
schema.propertyKey('discontinued').Boolean().single().ifNotExists().create()

schema.propertyKey('description').Text().single().ifNotExists().create()

// Define the vertex labels with associated properties
schema.vertexLabel('customer').properties('id', 'customerId', 'title', 'name', 'address', 'city', 'postalCode', 'phone', 'fax', 'company').ifNotExists().create()
schema.vertexLabel('employee').properties('id', 'title', 'titleOfCourtesy', 'firstName', 'lastName', 'address', 'city', 'postalCode', 'extension', 'homePhone', 'hireDate', 'notes').ifNotExists().create()
schema.vertexLabel('order').properties('id', 'orderDate', 'shipName', 'shipAddress', 'shipCity', 'shipPostalCode', 'shippedDate', 'requiredDate', 'freight').ifNotExists().create()
schema.vertexLabel('item').properties('id', 'unitPrice', 'discount', 'quantity').ifNotExists().create()
schema.vertexLabel('product').properties('id', 'name', 'type', 'unitPrice', 'unitsInStock', 'unitsOnOrder', 'reorderLevel', 'discontinued').ifNotExists().create()
schema.vertexLabel('category').properties('id', 'name', 'description').ifNotExists().create()
schema.vertexLabel('country').properties('id', 'name').ifNotExists().create()
schema.vertexLabel('region').properties('id', 'name').ifNotExists().create()

// Define the edge labels with cardinality and how they connect vertices
schema.edgeLabel('sold').single().connection('employee', 'order').ifNotExists().create()
schema.edgeLabel('ordered').single().connection('customer', 'order').ifNotExists().create()
schema.edgeLabel('contains').single().connection('order', 'item').ifNotExists().create()
schema.edgeLabel('livesInCountry').single().connection('customer', 'country').ifNotExists().create()
schema.edgeLabel('livesInRegion').single().connection('customer', 'region').ifNotExists().create()
schema.edgeLabel('inCategory').single().connection('product', 'category').ifNotExists().create()
schema.edgeLabel('is').single().connection('item', 'product').ifNotExists().create()
schema.edgeLabel('reportsTo').single().connection('employee', 'employee').ifNotExists().create()

// Add materialized views for what are essentially alternate keys to the data
schema.vertexLabel('customer').index('byCustomerId').materialized().by('customerId').ifNotExists().add()
schema.vertexLabel('product').index('byName').materialized().by('name').ifNotExists().add()
schema.vertexLabel('category').index('byName').materialized().by('name').ifNotExists().add()
schema.vertexLabel('country').index('byName').materialized().by('name').ifNotExists().add()
schema.vertexLabel('region').index('byName').materialized().by('name').ifNotExists().add()

// Add a search index on product and customer name to be able to things like regex or fuzzy searching by name
schema.vertexLabel('customer').index('search').search().by('name').ifNotExists().add()
schema.vertexLabel('product').index('search').search().by('name').ifNotExists().add()
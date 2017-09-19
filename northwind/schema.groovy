system.graph('northwind').create()
:remote config alias g northwind.g

// Define the possible properties for any vertex or edge label
schema.propertyKey('id').Int().single().create()

schema.propertyKey('customerId').Text().single().create()
schema.propertyKey('name').Text().single().create()
schema.propertyKey('title').Text().single().create()
schema.propertyKey('address').Text().single().create()
schema.propertyKey('city').Text().single().create()
schema.propertyKey('postalCode').Text().single().create()
schema.propertyKey('phone').Text().single().create()
schema.propertyKey('fax').Text().single().create()
schema.propertyKey('company').Text().single().create()

schema.propertyKey('titleOfCourtesy').Text().single().create()
schema.propertyKey('firstName').Text().single().create()
schema.propertyKey('lastName').Text().single().create()
schema.propertyKey('extension').Text().single().create()
schema.propertyKey('homePhone').Text().single().create()
schema.propertyKey('hireDate').Bigint().single().create()
schema.propertyKey('notes').Text().single().create()

schema.propertyKey('orderDate').Bigint().single().create()
schema.propertyKey('shipName').Text().single().create()
schema.propertyKey('shipAddress').Text().single().create()
schema.propertyKey('shipCity').Text().single().create()
schema.propertyKey('shipPostalCode').Text().single().create()
schema.propertyKey('shippedDate').Bigint().single().create()
schema.propertyKey('requiredDate').Bigint().single().create()
schema.propertyKey('freight').Decimal().single().create()

schema.propertyKey('unitPrice').Decimal().single().create()
schema.propertyKey('discount').Decimal().single().create()
schema.propertyKey('quantity').Int().single().create()

schema.propertyKey('type').Text().single().create()
schema.propertyKey('unitsInStock').Int().single().create()
schema.propertyKey('unitsOnOrder').Int().single().create()
schema.propertyKey('reorderLevel').Int().single().create()
schema.propertyKey('discontinued').Boolean().single().create()

schema.propertyKey('description').Text().single().create()

// Define the vertex labels with associated properties
schema.vertexLabel('customer').properties('id', 'customerId', 'title', 'name', 'address', 'city', 'postalCode', 'phone', 'fax', 'company').create()
schema.vertexLabel('employee').properties('id', 'title', 'titleOfCourtesy', 'firstName', 'lastName', 'address', 'city', 'postalCode', 'extension', 'homePhone', 'hireDate', 'notes').create()
schema.vertexLabel('order').properties('id', 'orderDate', 'shipName', 'shipAddress', 'shipCity', 'shipPostalCode', 'shippedDate', 'requiredDate', 'freight').create()
schema.vertexLabel('item').properties('id', 'unitPrice', 'discount', 'quantity').create()
schema.vertexLabel('product').properties('id', 'name', 'type', 'unitPrice', 'unitsInStock', 'unitsOnOrder', 'reorderLevel', 'discontinued').create()
schema.vertexLabel('category').properties('id', 'name', 'description').create()
schema.vertexLabel('country').properties('id', 'name').create()
schema.vertexLabel('region').properties('id', 'name').create()

// Define the edge labels with cardinality and how they connect vertices
schema.edgeLabel('sold').single().connection('employee', 'order').create()
schema.edgeLabel('ordered').single().connection('customer', 'order').create()
schema.edgeLabel('contains').single().connection('order', 'item').create()
schema.edgeLabel('livesInCountry').single().connection('customer', 'country').create()
schema.edgeLabel('livesInRegion').single().connection('customer', 'region').create()
schema.edgeLabel('inCategory').single().connection('product', 'category').create()
schema.edgeLabel('is').single().connection('item', 'product').create()
schema.edgeLabel('reportsTo').single().connection('employee', 'employee').create()

// Add materialized views for what are essentially alternate keys to the data
schema.vertexLabel('customer').index('byCustomerId').materialized().by('customerId').add()
schema.vertexLabel('product').index('byName').materialized().by('name').add()
schema.vertexLabel('category').index('byName').materialized().by('name').add()
schema.vertexLabel('country').index('byName').materialized().by('name').add()
schema.vertexLabel('region').index('byName').materialized().by('name').add()

// Add a search index on product and customer name to be able to things like regex or fuzzy searching by name
schema.vertexLabel('customer').index('search').search().by('name').add()
schema.vertexLabel('product').index('search').search().by('name').add()
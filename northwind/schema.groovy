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
schema.vertexLabel('customer').properties('fax', 'company', 'city', 'phone', 'title', 'name', 'address', 'customerId', 'id', 'postalCode').ifNotExists().create()
schema.vertexLabel('employee').properties('city', 'id', 'address', 'titleOfCourtesy', 'lastName', 'notes', 'postalCode', 'hireDate', 'firstName', 'extension', 'title', 'homePhone').ifNotExists().create()
schema.vertexLabel('order').properties('shipName', 'shipPostalCode', 'shippedDate', 'freight', 'id', 'shipCity', 'shipAddress', 'requiredDate', 'orderDate').ifNotExists().create()
schema.vertexLabel('item').properties('unitPrice', 'quantity', 'discount', 'id').ifNotExists().create()
schema.vertexLabel('product').properties('name', 'unitsOnOrder', 'reorderLevel', 'type', 'unitsInStock', 'unitPrice', 'id', 'discontinued').ifNotExists().create()
schema.vertexLabel('category').properties('id', 'description', 'name').ifNotExists().create()
schema.vertexLabel('country').properties('id', 'name').ifNotExists().create()
schema.vertexLabel('region').properties('id', 'name').ifNotExists().create()

// Define the edge labels with default single cardinality and how they connect vertices
schema.edgeLabel('sold').connection('employee', 'order').ifNotExists().create()
schema.edgeLabel('ordered').connection('customer', 'order').ifNotExists().create()
schema.edgeLabel('contains').connection('order', 'item').ifNotExists().create()
schema.edgeLabel('livesInCountry').connection('customer', 'country').ifNotExists().create()
schema.edgeLabel('livesInRegion').connection('customer', 'region').ifNotExists().create()
schema.edgeLabel('inCategory').connection('product', 'category').ifNotExists().create()
schema.edgeLabel('is').connection('item', 'product').ifNotExists().create()
schema.edgeLabel('reportsTo').connection('employee', 'employee').ifNotExists().create()

// Add a search index on product and customer name to be able to things like regex or fuzzy searching by name
schema.vertexLabel('customer').index('search').materialized().by('name').ifNotExists().add()
schema.vertexLabel('product').index('search').materialized().by('name').ifNotExists().add()

// Added by graphloader if not present
schema.vertexLabel('customer').index('byid').materialized().by('id').ifNotExists().add()
schema.vertexLabel('employee').index('byid').materialized().by('id').ifNotExists().add()
schema.vertexLabel('order').index('byid').materialized().by('id').ifNotExists().add()
schema.vertexLabel('item').index('byid').materialized().by('id').ifNotExists().add()
schema.vertexLabel('product').index('byid').materialized().by('id').ifNotExists().add()
schema.vertexLabel('category').index('byid').materialized().by('id').ifNotExists().add()
schema.vertexLabel('country').index('byid').materialized().by('id').ifNotExists().add()
schema.vertexLabel('region').index('byid').materialized().by('id').ifNotExists().add()

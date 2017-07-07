// Define the possible properties for any vertex or edge label
schema.propertyKey('id').Text().create()

schema.propertyKey('customerId').Text().create()
schema.propertyKey('name').Text().create()
schema.propertyKey('title').Text().create()
schema.propertyKey('address').Text().create()
schema.propertyKey('city').Text().create()
schema.propertyKey('postalCode').Text().create()
schema.propertyKey('phone').Text().create()
schema.propertyKey('fax').Text().create()
schema.propertyKey('company').Text().create()

schema.propertyKey('titleOfCourtesy').Text().create()
schema.propertyKey('firstName').Text().create()
schema.propertyKey('lastName').Text().create()
schema.propertyKey('extension').Text().create()
schema.propertyKey('homePhone').Text().create()
schema.propertyKey('hireDate').Bigint().create()
schema.propertyKey('notes').Text().create()

schema.propertyKey('orderDate').Bigint().create()
schema.propertyKey('shipName').Text().create()
schema.propertyKey('shipAddress').Text().create()
schema.propertyKey('shipCity').Text().create()
schema.propertyKey('shipPostalCode').Text().create()
schema.propertyKey('shippedDate').Bigint().create()
schema.propertyKey('requiredDate').Bigint().create()
schema.propertyKey('freight').Decimal().create()

schema.propertyKey('unitPrice').Decimal().create()
schema.propertyKey('discount').Decimal().create()
schema.propertyKey('quantity').Int().create()

schema.propertyKey('type').Text().create()
schema.propertyKey('unitsInStock').Int().create()
schema.propertyKey('unitsOnOrder').Int().create()
schema.propertyKey('reorderLevel').Int().create()
schema.propertyKey('discontinued').Boolean().create()

schema.propertyKey('description').Text().create()

schema.propertyKey('age').Int().create()

schema.propertyKey('affinityScore').Int().create()

schema.propertyKey('relationship').Text().create()

schema.propertyKey('confidence').Int().create()

schema.propertyKey('rating').Int().create()

// Define the vertex labels with associated properties
schema.vertexLabel('customer').partitionKey('id').properties('customerId', 'title', 'name', 'address', 'city', 'postalCode', 'phone', 'fax', 'company').create()
schema.vertexLabel('employee').partitionKey('id').properties('title', 'titleOfCourtesy', 'firstName', 'lastName', 'address', 'city', 'postalCode', 'extension', 'homePhone', 'hireDate', 'notes').create()
schema.vertexLabel('order').partitionKey('id').properties('orderDate', 'shipName', 'shipAddress', 'shipCity', 'shipPostalCode', 'shippedDate', 'requiredDate', 'freight').create()
schema.vertexLabel('item').partitionKey('id').properties('unitPrice', 'discount', 'quantity').create()
schema.vertexLabel('product').partitionKey('id').properties('name', 'type', 'unitPrice', 'unitsInStock', 'unitsOnOrder', 'reorderLevel', 'discontinued').create()
schema.vertexLabel('category').partitionKey('id').properties('name', 'description').create()
schema.vertexLabel('country').partitionKey('id').properties('name').create()
schema.vertexLabel('region').partitionKey('id').properties('name').create()
schema.vertexLabel('networkMember').partitionKey('id').properties('name', 'age').create()

// Define the edge labels with cardinality and how they connect vertices
schema.edgeLabel('sold').single().connection('employee', 'order').create()
schema.edgeLabel('ordered').single().connection('customer', 'order').create()
schema.edgeLabel('contains').single().connection('order', 'item').create()
schema.edgeLabel('livesInCountry').single().connection('customer', 'country').create()
schema.edgeLabel('livesInRegion').single().connection('customer', 'region').create()
schema.edgeLabel('inCategory').single().connection('product', 'category').create()
schema.edgeLabel('is').single().connection('item', 'product').create()
schema.edgeLabel('reportsTo').single().connection('employee', 'employee').create()
schema.edgeLabel('isNetworkMember').single().properties('confidence').connection('customer', 'networkMember').create()
schema.edgeLabel('isFriendsWith').single().properties('affinityScore').connection('networkMember', 'networkMember').create()
schema.edgeLabel('isRelatedTo').single().properties('relationship').connection('networkMember', 'networkMember').create()
schema.edgeLabel('rated').single().properties('rating').connection('customer', 'product').create()

// Add materialized views for what are essentially alternate keys to the data
schema.vertexLabel('customer').index('byCustomerId').materialized().by('customerId').add()
schema.vertexLabel('category').index('byName').materialized().by('name').add()
schema.vertexLabel('country').index('byName').materialized().by('name').add()
schema.vertexLabel('region').index('byName').materialized().by('name').add()

// Add a search index on product and customer name to be able to things like regex or fuzzy searching by name
schema.vertexLabel('customer').index('search').search().by('name').add()
schema.vertexLabel('product').index('search').search().by('name').add()

// Add a search index on network member on name and age
schema.vertexLabel('networkMember').index('search').search().by('name').by('age').add()
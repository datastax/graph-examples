//schema.clear()

schema.propertyKey('customerId').Uuid().ifNotExists().create()
schema.propertyKey('firstName').Text().ifNotExists().create()
schema.propertyKey('lastName').Text().ifNotExists().create()
schema.propertyKey('streetAddress1').Text().ifNotExists().create()
schema.propertyKey('streetAddress2').Text().ifNotExists().create()
schema.propertyKey('city').Text().ifNotExists().create()
schema.propertyKey('state').Text().ifNotExists().create()
schema.propertyKey('postalCode').Text().ifNotExists().create()
schema.propertyKey('countryCode').Text().ifNotExists().create()
schema.propertyKey('phoneNumber').Text().ifNotExists().create()
schema.propertyKey('created').Timestamp().ifNotExists().create()
schema.propertyKey('orderId').Uuid().ifNotExists().create()
schema.propertyKey('orderDate').Timestamp().ifNotExists().create()
schema.propertyKey('outcome').Text().ifNotExists().create()
schema.propertyKey('amount').Decimal().ifNotExists().create()
schema.propertyKey('creditCardHashed').Text().ifNotExists().create()
//schema.propertyKey('ipAddress').Inet().ifNotExists().create()
schema.propertyKey('ipAddress').Text().ifNotExists().create()
schema.propertyKey('deviceId').Uuid().ifNotExists().create()
schema.propertyKey('sessionId').Uuid().ifNotExists().create()
schema.propertyKey('sessionDate').Timestamp().ifNotExists().create()
schema.propertyKey('chargebackNumber').Int().ifNotExists().create()
schema.propertyKey('chargebackDate').Timestamp().ifNotExists().create()
schema.propertyKey('relatedBy').Text().ifNotExists().create()
schema.propertyKey('merchantId').Uuid().ifNotExists().create()
schema.propertyKey('company-name').Text().ifNotExists().create()

// Vertex Labels
schema.vertexLabel('customer').partitionKey('customerId').properties('firstName', 'lastName', 'streetAddress1', 'streetAddress1', 'city', 'state', 'postalCode', 'countryCode', 'phoneNumber', 'created').ifNotExists().create()
schema.vertexLabel('order').partitionKey('orderId').properties('orderDate', 'outcome', 'creditCardHashed', 'ipAddress', 'amount', 'deviceId', 'merchantId').ifNotExists().create()
schema.vertexLabel('session').partitionKey('sessionId').properties('ipAddress', 'deviceId', 'sessionDate').ifNotExists().create()
schema.vertexLabel('chargeback').partitionKey('chargebackNumber').properties('chargebackDate', 'amount', 'creditCardHashed').ifNotExists().create()
schema.vertexLabel('merchant').partitionKey('merchantId').properties('company-name','postalCode', 'created').ifNotExists().create()

// Edge Labels
schema.edgeLabel('isRelatedTo').properties('relatedBy').connection('customer', 'customer').ifNotExists().create()
schema.edgeLabel('places').connection('customer', 'order').ifNotExists().create()
schema.edgeLabel('logsInto').connection('customer', 'session').ifNotExists().create()
schema.edgeLabel('chargedWith').connection('customer', 'chargeback').ifNotExists().create()
schema.edgeLabel('resultsIn').connection('order', 'chargeback').ifNotExists().create()
schema.edgeLabel('boughtFrom').connection('order', 'merchant').ifNotExists().create()

// Vertex Indexes
// Secondary
//     schema.vertexLabel('author').index('byName').secondary().by('name').add()
// Materialized	  		
//     schema.vertexLabel('recipe').index('byRecipe').materialized().by('name').add()
//     schema.vertexLabel('meal').index('byMeal').materialized().by('name').add()
//     schema.vertexLabel('ingredient').index('byIngredient').materialized().by('name').add()
//     schema.vertexLabel('reviewer').index('byReviewer').materialized().by('name').add()
// Search
// schema.vertexLabel('recipe').index('search').search().by('instructions').asText().add()
// schema.vertexLabel('recipe').index('search').search().by('instructions').asString().add()
// If more than one property key is search indexed
// schema.vertexLabel('recipe').index('search').search().by('instructions').asText().by('category').asString().add()

//schema.config().option('graph.schema_mode').set('Development')
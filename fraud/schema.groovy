// Properties
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

// Vertex Labels
schema.vertexLabel('customer').partitionKey('customerId').properties('firstName', 'lastName', 'streetAddress1', 'streetAddress2', 'city', 'state', 'postalCode', 'countryCode', 'phoneNumber', 'created').ifNotExists().create()
schema.vertexLabel('order').partitionKey('orderId').properties('orderDate', 'outcome', 'creditCardHashed', 'ipAddress', 'amount', 'deviceId').ifNotExists().create()
schema.vertexLabel('session').partitionKey('sessionId').properties('ipAddress', 'deviceId', 'sessionDate').ifNotExists().create()
schema.vertexLabel('chargeback').partitionKey('chargebackNumber').properties('chargebackDate', 'amount', 'creditCardHashed').ifNotExists().create()

// Edge Labels
schema.edgeLabel('isRelatedTo').properties('relatedBy').connection('customer', 'customer').ifNotExists().create()
schema.edgeLabel('places').connection('customer', 'order').ifNotExists().create()
schema.edgeLabel('logsInto').connection('customer', 'session').ifNotExists().create()
schema.edgeLabel('chargedWith').connection('customer', 'chargeback').ifNotExists().create()
schema.edgeLabel('resultsIn').connection('order', 'chargeback').ifNotExists().create()
// Property keys
schema.propertyKey('customerId').Uuid().ifNotExists().create()
schema.propertyKey('firstName').Text().ifNotExists().create()
schema.propertyKey('lastName').Text().ifNotExists().create()
schema.propertyKey('email').Text().ifNotExists().create()
schema.propertyKey('address').Text().ifNotExists().create()
schema.propertyKey('city').Text().ifNotExists().create()
schema.propertyKey('state').Text().ifNotExists().create()
schema.propertyKey('postalCode').Text().ifNotExists().create()
schema.propertyKey('countryCode').Text().ifNotExists().create()
schema.propertyKey('phone').Text().ifNotExists().create()
schema.propertyKey('createdTime').Timestamp().ifNotExists().create()

schema.propertyKey('sessionId').Uuid().ifNotExists().create()
schema.propertyKey('ipAddress').Text().ifNotExists().create()
schema.propertyKey('deviceId').Uuid().ifNotExists().create()

schema.propertyKey('orderId').Uuid().ifNotExists().create()
schema.propertyKey('outcome').Text().ifNotExists().create()
schema.propertyKey('creditCardHashed').Text().ifNotExists().create()
schema.propertyKey('amount').Decimal().ifNotExists().create()

schema.propertyKey('chargebackNumber').Int().ifNotExists().create()

schema.propertyKey('type').Text().ifNotExists().create()
schema.propertyKey('os').Text().ifNotExists().create()
schema.propertyKey('osVersion').Text().ifNotExists().create()

// Vertex labels
schema.vertexLabel('customer').partitionKey('customerId').properties('firstName', 'lastName', 'email', 'address', 'city', 'state', 'postalCode', 'countryCode', 'phone', 'createdTime').ifNotExists().create()
schema.vertexLabel('session').partitionKey('sessionId').properties('ipAddress', 'deviceId', 'createdTime').ifNotExists().create()
schema.vertexLabel('order').partitionKey('orderId').properties('createdTime', 'outcome', 'creditCardHashed', 'ipAddress', 'amount', 'deviceId').ifNotExists().create()
schema.vertexLabel('chargeback').partitionKey('chargebackNumber').properties('createdTime', 'amount', 'creditCardHashed').ifNotExists().create()
schema.vertexLabel('creditCard').partitionKey('creditCardHashed').properties('type').ifNotExists().create()
schema.vertexLabel('device').partitionKey('deviceId').properties('type', 'os', 'osVersion').ifNotExists().create()

// Edge labels
schema.edgeLabel('places').connection('customer', 'order').ifNotExists().create()
schema.edgeLabel('usesCard').connection('order', 'creditCard').ifNotExists().create()
schema.edgeLabel('logsInto').connection('customer', 'session').ifNotExists().create()
schema.edgeLabel('chargedWith').connection('customer', 'chargeback').ifNotExists().create()
schema.edgeLabel('fromCard').connection('chargeback', 'creditCard').ifNotExists().create()
schema.edgeLabel('resultsIn').connection('order', 'chargeback').ifNotExists().create()
schema.edgeLabel('using').connection('session', 'device').connection('order', 'device').ifNotExists().create()

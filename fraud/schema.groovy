system.graph('fraud').ifNotExists().create()
:remote config alias g fraud.g

// Property keys
schema.propertyKey('customerid').Uuid().ifNotExists().ifNotExists().create()
schema.propertyKey('firstname').Text().ifNotExists().ifNotExists().create()
schema.propertyKey('lastname').Text().ifNotExists().ifNotExists().create()
schema.propertyKey('email').Text().ifNotExists().ifNotExists().create()
schema.propertyKey('address').Text().ifNotExists().ifNotExists().create()
schema.propertyKey('city').Text().ifNotExists().ifNotExists().create()
schema.propertyKey('state').Text().ifNotExists().ifNotExists().create()
schema.propertyKey('postalcode').Text().ifNotExists().ifNotExists().create()
schema.propertyKey('countrycode').Text().ifNotExists().ifNotExists().create()
schema.propertyKey('phone').Text().ifNotExists().ifNotExists().create()
schema.propertyKey('createdtime').Timestamp().ifNotExists().ifNotExists().create()

schema.propertyKey('sessionid').Uuid().ifNotExists().ifNotExists().create()
schema.propertyKey('ipaddress').Text().ifNotExists().ifNotExists().create()
schema.propertyKey('deviceid').Uuid().ifNotExists().ifNotExists().create()

schema.propertyKey('orderid').Uuid().ifNotExists().ifNotExists().create()
schema.propertyKey('outcome').Text().ifNotExists().ifNotExists().create()
schema.propertyKey('creditcardhashed').Text().ifNotExists().ifNotExists().create()
schema.propertyKey('amount').Decimal().ifNotExists().ifNotExists().create()

schema.propertyKey('chargebacknumber').Int().ifNotExists().ifNotExists().create()

schema.propertyKey('type').Text().ifNotExists().ifNotExists().create()
schema.propertyKey('os').Text().ifNotExists().ifNotExists().create()
schema.propertyKey('osversion').Text().ifNotExists().ifNotExists().create()

// Vertex labels
schema.vertexLabel('customer').partitionKey('customerid').properties('firstname', 'lastname', 'email', 'phone', 'createdtime').ifNotExists().ifNotExists().create()
schema.vertexLabel('address').partitionKey('address', 'postalcode').properties('city', 'state', 'countrycode').ifNotExists().ifNotExists().create()
schema.vertexLabel('session').partitionKey('sessionid').properties('ipaddress', 'deviceid', 'createdtime').ifNotExists().ifNotExists().create()
schema.vertexLabel('order').partitionKey('orderid').properties('createdtime', 'outcome', 'creditcardhashed', 'ipaddress', 'amount', 'deviceid').ifNotExists().ifNotExists().create()
schema.vertexLabel('chargeback').partitionKey('chargebacknumber').properties('createdtime', 'amount', 'creditcardhashed').ifNotExists().ifNotExists().create()
schema.vertexLabel('creditCard').partitionKey('creditcardhashed').properties('type').ifNotExists().ifNotExists().create()
schema.vertexLabel('device').partitionKey('deviceid').properties('type', 'os', 'osversion').ifNotExists().ifNotExists().create()

// Edge labels
schema.edgeLabel('hasAddress').connection('customer', 'address').ifNotExists().ifNotExists().create()
schema.edgeLabel('places').connection('customer', 'order').ifNotExists().ifNotExists().create()
schema.edgeLabel('usesCard').connection('order', 'creditCard').ifNotExists().ifNotExists().create()
schema.edgeLabel('logsInto').connection('customer', 'session').ifNotExists().ifNotExists().create()
schema.edgeLabel('chargedWith').connection('customer', 'chargeback').ifNotExists().ifNotExists().create()
schema.edgeLabel('fromCard').connection('chargeback', 'creditCard').ifNotExists().ifNotExists().create()
schema.edgeLabel('resultsIn').connection('order', 'chargeback').ifNotExists().ifNotExists().create()
schema.edgeLabel('using').connection('session', 'device').connection('order', 'device').ifNotExists().ifNotExists().create()

//Configures the data loader to create the schema
config create_schema: false, load_new: true

// If the user specifies an inputpath on the command-line, use that.
// Otherwise check the data directory from the data directory from where the loader is run.
if (inputpath == '')
    path = new java.io.File('.').getCanonicalPath() + '/data/'
else
    path = inputpath + '/'

customers = File.csv(path + 'customers.csv').delimiter('|')
sessions = File.csv(path + 'sessions.csv').delimiter('|')
orders = File.csv(path + 'orders.csv').delimiter('|')
chargebacks = File.csv(path + 'chargebacks.csv').delimiter('|')
creditCards = File.csv(path + 'creditCards.csv').delimiter('|')
devices = File.csv(path + 'devices.csv').delimiter('|')

customerAddresses = File.json(path + 'customerAddresses.json')
customerOrders = File.csv(path + 'customerOrders.csv').delimiter('|')
orderChargebacks = File.csv(path + 'orderChargebacks.csv').delimiter('|')
customerSessions = File.csv(path + 'customerSessions.csv').delimiter('|')
customerChargebacks = File.csv(path + 'customerChargebacks.csv').delimiter('|')

load(customers).asVertices {
    label 'customer'
    key 'customerId'
    ignore 'address'
    ignore 'city'
    ignore 'state'
    ignore 'postalCode'
    ignore 'countryCode'
}

load(customers).asVertices {
    label 'address'
    key address: 'address', postalCode: 'postalCode'
    ignore 'customerId'
    ignore 'firstName' 
    ignore 'lastName'
    ignore 'email'
    ignore 'phone'
    ignore 'createdTime'
}

load(sessions).asVertices {
    label 'session'
    key 'sessionId'
}

load(orders).asVertices {
    label 'order'
    key 'orderId'
}

load(chargebacks).asVertices {
    label 'chargeback'
    key 'chargebackNumber'
}

load(creditCards).asVertices {
    label 'creditCard'
    key 'creditCardHashed'
}

load(devices).asVertices {
    label 'device'
    key 'deviceId'
}

load(customerOrders).asEdges {
    label 'places'
    outV 'customerId', {
        label 'customer'
        key 'customerId'
    }
    inV 'orderId', {
        label 'order'
        key 'orderId'
    }
}

load(orders).asEdges {
    label 'usesCard'
    outV 'orderId', {
        label 'order'
        key 'orderId'
    }
    inV 'creditCardHashed', {
        label 'creditCard'
        key 'creditCardHashed'
    }
    ignore 'createdTime'
    ignore 'outcome'
    ignore 'ipAddress'
    ignore 'amount'
    ignore 'deviceId'
}

load(orderChargebacks).asEdges {
    label 'resultsIn'
    outV 'orderId', {
        label 'order'
        key 'orderId'
    }
    inV 'chargebackNumber', {
        label 'chargeback'
        key 'chargebackNumber'
    }
    ignore 'amount'
    ignore 'createdTime'
}

load(chargebacks).asEdges {
    label 'fromCard'
    outV 'chargebackNumber', {
        label 'chargeback'
        key 'chargebackNumber'
    }
    inV 'creditCardHashed', {
        label 'creditCard'
        key 'creditCardHashed'
    }
    ignore 'amount'
    ignore 'createdTime'
}

load(customerSessions).asEdges {
    label 'logsInto'
    outV 'customerId', {
        label 'customer'
        key 'customerId'
    }
    inV 'sessionId', {
        label 'session'
        key 'sessionId'
    }
}

load(customerChargebacks).asEdges {
    label 'chargedWith'
    outV 'customerId', {
        label 'customer'
        key 'customerId'
    }
    inV 'chargebackNumber', {
        label 'chargeback'
        key 'chargebackNumber'
    }
}

load(sessions).asEdges {
    label 'using'
    outV 'sessionId', {
        label 'session'
        key 'sessionId'
    }
    inV 'deviceId', {
        label 'device'
        key 'deviceId'
    }
    ignore 'ipAddress'
    ignore 'createdTime'
}

load(orders).asEdges {
    label 'using'
    outV 'orderId', {
        label 'order'
        key 'orderId'
    }
    inV 'deviceId', {
        label 'device'
        key 'deviceId'
    }
    ignore 'createdTime'
    ignore 'outcome'
    ignore 'ipAddress'
    ignore 'creditCardHashed'
    ignore 'amount'
}

// load(customerAddresses).asEdges {
//     label 'hasAddress'
//     outV 'customer', {
//         label 'customer'
//         key 'customerId'
//     }
//     inV 'address', {
//         label 'address'
//         key address: 'address', postalCode: 'postalCode'
//     }
// }
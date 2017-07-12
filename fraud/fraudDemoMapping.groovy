//Configures the data loader to create the schema
config create_schema: false, load_new: true

// If the user specifies an inputpath on the command-line, use that.
// Otherwise check the data directory from the data directory from where the loader is run.
if (inputpath == '')
    path = new java.io.File('.').getCanonicalPath() + '/data/'
else
    path = inputpath + '/'

customerInput = File.csv(inputpath + 'customers.csv').delimiter('|')
sessionInput = File.csv(inputpath + 'sessions.csv').delimiter('|')
orderInput = File.csv(inputpath + 'orders.csv').delimiter('|')
chargebackInput = File.csv(inputpath + 'chargebacks.csv').delimiter('|')
//merchantInput = File.csv(inputpath + 'merchants.csv').delimiter('|')

customerOrderInput = File.csv(inputpath + 'customerOrders.csv').delimiter('|')
orderChargebackInput = File.csv(inputpath + 'orderChargebacks.csv').delimiter('|')
//orderMerchantInput = File.csv(inputpath + 'orderMerchants.csv').delimiter('|')
customerSessionInput = File.csv(inputpath + 'customerSessions.csv').delimiter('|')
customerChargebackInput = File.csv(inputpath + 'customerChargebacks.csv').delimiter('|')
relatedCustomerInput = File.csv(inputpath + 'relatedCustomers.csv').delimiter('|')

load(customerInput).asVertices {
    label "customer"
    key "customerId"
}

load(sessionInput).asVertices {
    label "session"
    key "sessionId"
}

load(orderInput).asVertices {
    label "order"
    key "orderId"
}

load(chargebackInput).asVertices {
    label "chargeback"
    key "chargebackNumber"
}

//load(merchantInput).asVertices {
//    label "merchant"
//    key "merchantId"
//}

load(customerOrderInput).asEdges {
    label "places"
    outV "customerId", {
        label "customer"
        key "customerId"
    }
    inV "orderId", {
        label "order"
        key "orderId"
    }
}

load(orderChargebackInput).asEdges {
    label "resultsIn"
    outV "orderId", {
        label "order"
        key "orderId"
    }
    inV "chargebackNumber", {
        label "chargeback"
        key "chargebackNumber"
    }
}

//load(orderMerchantInput).asEdges {
//    label "boughtFrom"
//    outV "orderId", {
//        label "order"
//        key "orderId"
//    }
//    inV "merchantId", {
//        label "merchant"
//        key "merchantId"
//    }
//}

load(customerSessionInput).asEdges {
    label "logsInto"
    outV "customerId", {
        label "customer"
        key "customerId"
    }
    inV "sessionId", {
        label "session"
        key "sessionId"
    }
}

load(customerChargebackInput).asEdges {
    label "chargedWith"
    outV "customerId", {
        label "customer"
        key "customerId"
    }
    inV "chargebackNumber", {
        label "chargeback"
        key "chargebackNumber"
    }
}

load(relatedCustomerInput).asEdges {
    label "isRelatedTo"
    outV "parentCustomerId", {
        label "customer"
        key "customerId"
    }
    inV "childCustomerId", {
        label "customer"
        key "customerId"
    }
}

//run with a command like this
// graphloader fraudDemoMapping.groovy -graph fraud -address localhost -dryrun true

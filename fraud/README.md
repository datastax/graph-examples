# Fraud

This is a fraud detection use case using a graph intended for the financial industry.

## Create the schema

Included is a `schema.groovy` file.  You can create your graph in Studio and copy and paste the schema statements
to run there.  Alternately, the statements can be run from the gremlin console.

## Example loading

Example of loading the fraud data:

```
# From the fraud directory
graphloader -graph fraud -address localhost fraud-mapping.groovy
```

```
# Alternatively, explicitly specify where the data files are
graphloader -graph fraud -address localhost fraud-mapping.groovy -inputpath ~/graph-examples/fraud/data/
```

## Scenarios

- [Scenario 1](#scenario1): Legitimate - User registers and eventually places a order
- [Scenario 2](#scenario2): Suspicious - User registers and places an order with previously used device id (might be husband and wife)
- [Scenario 3](#scenario3): Fraud - User registers and places an order with highly used device id
- [Scenario 4](#scenario4): Fraud - Order placed using the same credit card as an order which resulted in a chargeback
- [Scenario 5](#scenario5): Fraud - Order placed using the same device as an order which resulted in a chargeback
- [Scenario 6](#scenario6): Fraud - Order placed using a credit card which is linked to a device which was used by a customer who placed an order which resulted in a chargeback
- [Scenario 7](#scenario7): Suspicious - Four levels of linkage are suspicious even without a chargeback

<a name='scenario1'/>

### Scenario 1

#### Legitimate - User registers and eventually places a order

**Traversal to Visualize:** `g.V().has("customer", "customerid", "10000000-0000-0000-0000-000000000001").emit().repeat(both().simplePath()).times(4)`

**Scenario details**

Event: Registration - unique customer name, address, email, etc.
Event: Session - same customer id as registration, unique device id, IP address

User logs in 1 day later and changes information
Event: Session -- same customer id,  device id and IP address as previous

User logs in 1 day later from mobile device for some other legit reason
Event: Session -- same customer id, different device id, different IP address

User logs in 1 day later and places an order
Event: Session -- same customer id, same device id as first login, different IP address
Event: Order  - unique credit card - order is approved

<a name='scenario2'/>

### Scenario 2

#### Suspicious - User registers and places an order with previously used device id (might be husband and wife)

**Traversal to Visualize:** `g.V().has("address", "address", "102 Bellevue Blvd").has("postalcode", "21201").emit().repeat(both().simplePath()).times(4)`

**Velocity Variables**
```
// Direct connections
myOrder = "40000000-0000-0000-0002-000000000002"
myCustomerID = g.V().has("order", "orderid", myOrder).inE("places").outV().values("customerid").next()
connectionsByAddress = g.V().has("customer", "customerid", myCustomerID).outE("hasAddress").inV().inE("hasAddress").outV().dedup().count().next()
connectionsByIp = g.V().has("customer", "customerid", myCustomerID).outE("logsinto").inV().inE("logsinto").inV().values("ipAddress").dedup().count().next()
connectionsByMachine = g.V().has("customer", "customerid", myCustomerID).outE("logsinto").inV().inE("logsinto").inV().outE().dedup().count().next()
```

**Scenario details**

Event: Registration - unique customer name, address, email, same physical address as another customer
Event: Session - same customer id as registration, device id seen on 1 other customer registrations (the one with the same physical address), IP address seen on 1 other customer registrations
Event: Order - unique credit card - order is approved

<a name='scenario3'/>

### Scenario 3

#### Fraud - User registers and places an order with highly used device id

**Traversals to Visualize:**
- `g.V().has("device", "deviceid", "30000000-0000-0000-0015-000000000004").inE()`
- `g.V().has("device", "deviceid", "30000000-0000-0000-0015-000000000004").emit().repeat(both()).times(2)`

**Velocity Variables**
```
// Users Per Device
degrees = 3
myOrder = "40000000-0000-0000-0003-000000000003"
deviceid = g.V().has("order","orderid", myOrder).values("deviceid").next()
subgraph = g.V().has("device", "deviceid", deviceid)
    .repeat(
        __.bothE('using', 'logsinto')
        .subgraph('sg').otherV()
        .simplePath()).
    times(degrees).cap('sg').next()
sg = subgraph.traversal()
usersPerDevice = sg.V().hasLabel('customer').count().next()
```

**Scenario details**

Event: Registration - unique customer name, address, email, etc.
Event: Session - same customer id as registration, unique device id, unique IP address

Event: Registration 1 day later - unique customer name, address, email, etc.
Event: Session - same customer id as registration, device id seen on 1 other customer registrations, IP address seen on 1 other customer registrations

Event: Registration 1 day later - unique customer name, address, email, etc.
Event: Session - same customer id as registration, device id seen on 2 other customer registrations, IP address seen on 2 other customer registrations

Event: Registration 1 day later - unique customer name, address, email, etc.
Event: Session - same customer id as registration, device id seen on 3 other customer registrations, IP address seen on 3 other customer registrations

Event: Registration 1 day later - unique customer name, address, email, etc.
Event: Session - same customer id as registration, device id seen on 4 other customer registrations, IP address seen on 4 other customer registrations

Event: Registration 1 day later - unique customer name, address, email, etc.
Event: Session - same customer id as registration, device id seen on 5 other customer registrations, IP address seen on 5 other customer registrations

Event: Registration 1 day later - unique customer name, address, email, etc.
Event: Session - same customer id as registration, device id seen on 6 other customer registrations, IP address seen on 6 other customer registrations

Event: Registration - unique customer name, address, email, etc.
Event: Session - same customer id as registration, device id seen on 7 other customer registrations, IP address seen on 7 other customer registrations

Event: Registration 1 day later - unique customer name, address, email, etc.
Event: Session - same customer id as registration, device id seen on 8 other customer registrations, IP address seen on 8 other customer registrations

Event: Registration - unique customer name, address, email, etc.
Event: Session - same customer id as registration, device id seen on 9 other customer registrations, IP address seen on 9 other customer registrations
Event: Order - unique credit card, same customer id as above registration - order is declined

<a name='scenario4'/>

### Scenario 4

#### Fraud - Order placed using the same credit card as an order which resulted in a chargeback

**Traversal to Visualize:** `g.V().has('creditcard', 'creditcardhashed', 'a1ab1822888276fdb587a16b2dc7b697').emit().repeat(both()).times(2)`

**Velocity Variables**
```
// Related to a chargeback (via anything)
degrees = 3
numChargebacks = g.V().has('order', 'orderid', '40000000-0000-0000-0004-000000000005')
    .emit(hasLabel("chargeback"))
    .repeat(both())
    .times(degrees)
    .hasLabel("chargeback").dedup().count()
```

**Scenario details**

Event: Registration - unique customer name, address, email, etc.
Event: Session - same customer id as registration, unique device id, IP address
Event: Order - unique credit card - order is approved

Event: Chargeback 90 days later - matched to previous order & credit card

Event: Registration 1 day later - unique customer name, address, email, etc.
Event: Session - same customer id as above registration, unique device id, IP address
Event: Order - credit card same as above order - order is declined

<a name='scenario5'/>

### Scenario 5

#### Fraud - Order placed using the same device as an order which resulted in a chargeback

**Traversal to Visualize:** `g.V().hasLabel('order').has('orderid', '40000000-0000-0000-0991-000000000008').emit().repeat(both().simplePath()).times(5)`

**Velocity Variables**
```
// Related to a charge back via Machine's subgraph
degrees = 3
myDeviceId = g.V().has('order', 'orderid', '40000000-0000-0000-0004-000000000005').values("deviceid").next()
subgraph = g.V().has('device', 'deviceid', myDeviceId).repeat(
        __.bothE()
        .subgraph('sg').otherV()
        .simplePath()).
    times(degrees).cap('sg').next()
sg = subgraph.traversal()
numChargebacksByMachine = sg.V().hasLabel('chargeback').dedup().count()
```

**Scenario details**

Event: Registration - unique customer name, address, email, etc.
Event: Session - same customer id as registration, unique device id, IP address

Event: Session 1 day later - same customer id as above registration, unique (different) device id, unique (different) IP address
Event: Order - unique credit card - order is approved

Event: Chargeback 90 days later - matched to previous order & credit card

Event: Registration 1 day later - unique customer name, address, email, etc.
Event: Session - same customer id as above registration, same device id as customer's initial session, unique IP address
Event: Order - device id linked to a customer which is linked to a chargeback - order is declined

<a name='scenario6'/>

### Scenario 6

#### Fraud - Order placed using a credit card which is linked to a device which was used by a customer who placed an order which resulted in a chargeback

**Traversal to Visualize:** `g.V().has('order', 'orderid', '40000000-0000-0000-0003-000000000188').emit().repeat(both().simplePath()).times(6)`

**Velocity Variables**
```
// Related to a charge back via Credit Card's subgraph (5 degrees)
degrees = 5
ccId = g.V().has('order', 'orderid', '40000000-0000-0000-0003-000000000188').values("creditcardhashed").next()
subgraph = g.V().has('creditcard', 'creditcardhashed', ccId).repeat(
        __.bothE()
        .subgraph('sg').otherV()
        .simplePath()).
    times(degrees).cap('sg').next()
sg = subgraph.traversal()
numChargebacksbyCreditCard = sg.V().hasLabel('chargeback').dedup().count()
```

**Scenario details**

Event: Registration - unique customer name, address, email, etc.
Event: Session - same customer id (C11111) as registration, unique device id (e.g. D11111), IP address

Event: Session 1 day later - same customer id (C11111) as above registration, unique (different) device id, unique (different) IP address
Event: Order (O11111) - unique credit card - order is approved

Event: Registration 10 days later - unique customer name, address, email, etc.
Event: Session - same customer id (C22222) as above registration, same device id as customer's initial session (e.g., D11111), unique IP address
Event: Order (O22222) - device id linked to a customer (suspicious), unique credit card# (4111 1111 1111 1111) - order is approved

Event: chargeback 90 days later - matched to first order & credit card

Event: Registration 10 days later - unique customer name, address, email, etc.
Event: Session - same customer id (C33333) as above registration, unique device id, unique IP address
Event: Order (O33333) - credit card (4111 1111 1111 1111) matched to O22222, customer from O22222 (C22222) linked by device id linked to customer (C11111) who placed an order with a chargeback - order is declined

<a name='scenario7'/>

### Scenario 7

#### Suspicious - Four levels of linkage are suspicious even without a chargeback

**Traversal to Visualize:** `g.V().has('address', 'address', '650 Del Prado Drive').has('postalcode', '89005').emit().repeat(both().simplePath()).times(8)`

**Velocity Variables**
```
// Number of credit cards or customers in network
degrees = 9
subgraph = g.V().has('order', 'orderid', "40000000-0000-0000-0148-000000000304").repeat(
        __.bothE()
        .subgraph('sg').otherV()
        .simplePath()).
    times(degrees).cap('sg').next()
sg = subgraph.traversal()
numCustomers = sg.V().hasLabel('customer').dedup().count().next()
numCreditCards = sg.V().hasLabel('creditcard').dedup().count().next()
```

**Scenario details**

Event: Registration - unique customer name, address, email, etc.
Event: Session - same customer id (C11111) as registration, unique device id (e.g. D11111), IP address
Event: Order (O11111) - unique credit card - order is approved

Event: Registration 10 days later - unique customer name, address, email, etc.
Event: Session - same customer id (C22222) as above registration, same device id as customer C11111's session (e.g., D11111), unique IP address
Event: Order (O22222) - device id linked to a customer (suspicious), unique credit card# (4111 1111 1111 1111) - order is approved

Event: Registration 10 days later - unique customer name (Joe Banks), address, email (volcanojoe@gmail.com), etc.
Event: Session - same customer id (C33333) as above registration, unique device id, unique IP address
Event: Order (O33333) - credit card (4111 1111 1111 1111) matched to O22222

Event: Registration 10 days later - customer name (Joe Banks) and same physical address as C33333, unique email
Event: Session - same customer id (C44444) as above registration, unique device id, unique IP address
Event: Order (O44444) - Name and Email linked to customer from O33333 -- order is declined -- too many layers of account linkage (despite the fact that there are no links to chargebacks or other "hard" fraud indicators)

## To Do:

- Take out one of the Joe Banks, possibly add other linkage
- Scenario 2 - have more than two people at the same address
- Tell the story about both visualizing existing fraud/suspicion as well as building simple rules around classifying automatically - like count the number of people involved
- Enrich the data with related to with a graph frames job as well as add grandchildren like for session->order
- Add some indexes on things we query by so we don't need `allow_scan` enabled
- Add credit card metadata including issuer, postal code, other location data
- Link customers to orders via sessions instead of separate path, remove redundant IP and device information from orders - should only be needed on associated session
- Add geoIP information to the session data?
- Open ticket to display better date formatting, potential workaround is to add formatted properties via groovy and display those

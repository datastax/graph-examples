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

### Scenario 1 - User Registers and eventually places an order (legitimate)
Event: Registration - unique Customer name, address, email, etc.
Event: Session - same CustomerID as registration, unique DeviceID, IP Address

User logs in 1 day later and changes information
Event: Session -- same CustomerID,  DeviceID and IP Address as previous

User logs in 1 day later from mobile device for some other legit reason
Event: Session -- same CustomerID, different DeviceID, different IP address

User logs in 1 day later and places an order
Event: Session -- same CustomerID, same DeviceID as first login, different IP address
Event: Order  - unique CC - order is approved

### Scenario 2 - User Registers and places an order with previously used DeviceID (suspicious -- might be husband and wife?)
Event: Registration - unique Customer name, address, email, same physical address as another customer
Event: Session - same CustomerID as registration, DeviceID seen on 1 other customer registrations (the one with the same physical address), IP Address seen on 1 other customer registrations
Event: Order - unique CC - order is approved

### Scenario 3 - User Registers and places an order with highly used DeviceID (fraud)
Event: Registration - unique Customer name, address, email, etc.
Event: Session - same CustomerID as registration, unique DeviceID, unique IP Address

Event: Registration 1 day later - unique Customer name, address, email, etc.
Event: Session - same CustomerID as registration, DeviceID seen on 1 other customer registrations, IP Address seen on 1 other customer registrations

Event: Registration 1 day later - unique Customer name, address, email, etc.
Event: Session - same CustomerID as registration, DeviceID seen on 2 other customer registrations, IP Address seen on 2 other customer registrations

Event: Registration 1 day later - unique Customer name, address, email, etc.
Event: Session - same CustomerID as registration, DeviceID seen on 3 other customer registrations, IP Address seen on 3 other customer registrations

Event: Registration 1 day later - unique Customer name, address, email, etc.
Event: Session - same CustomerID as registration, DeviceID seen on 4 other customer registrations, IP Address seen on 4 other customer registrations

Event: Registration 1 day later - unique Customer name, address, email, etc.
Event: Session - same CustomerID as registration, DeviceID seen on 5 other customer registrations, IP Address seen on 5 other customer registrations

Event: Registration 1 day later - unique Customer name, address, email, etc.
Event: Session - same CustomerID as registration, DeviceID seen on 6 other customer registrations, IP Address seen on 6 other customer registrations

Event: Registration - unique Customer name, address, email, etc.
Event: Session - same CustomerID as registration, DeviceID seen on 7 other customer registrations, IP Address seen on 7 other customer registrations

Event: Registration 1 day later - unique Customer name, address, email, etc.
Event: Session - same CustomerID as registration, DeviceID seen on 8 other customer registrations, IP Address seen on 8 other customer registrations

Event: Registration - unique Customer name, address, email, etc.
Event: Session - same CustomerID as registration, DeviceID seen on 9 other customer registrations, IP Address seen on 9 other customer registrations
Event: Order - unique CC, same Customer ID as above registration - order is declined

### Scenario 4 - Order placed using the same CC as an order which resulted in a Chargeback (fraud)
Event: Registration - unique Customer name, address, email, etc.
Event: Session - same CustomerID as registration, unique DeviceID, IP Address
Event: Order - unique CC - order is approved

Event: Chargeback 90 days later - matched to previous order & CC

Event: Registration 1 day later - unique Customer name, address, email, etc.
Event: Session - same CustomerID as above registration, unique DeviceID, IP Address
Event: Order - CC same as above order - order is declined

### Scenario 5 - Order placed using the same Device as an order which resulted in a Chargeback (fraud)
Event: Registration - unique Customer name, address, email, etc.
Event: Session - same CustomerID as registration, unique DeviceID, IP Address

Event: Session 1 day later - same CustomerID as above registration, unique (different) DeviceID, unique (different) IP Address
Event: Order - unique CC - order is approved

Event: Chargeback 90 days later - matched to previous order & CC

Event: Registration 1 day later - unique Customer name, address, email, etc.
Event: Session - same CustomerID as above registration, same DeviceID as customer's initial session, unique IP Address
Event: Order - Device ID linked to a Customer which is linked to a Chargeback - order is declined

### Scenario 6 - Order placed using a Credit Card which is linked to a Device which was used by a customer who placed an order which resulted in a Chargeback (fraud) :)
Event: Registration - unique Customer name, address, email, etc.
Event: Session - same CustomerID (C11111) as registration, unique DeviceID (e.g. D11111), IP Address

Event: Session 1 day later - same CustomerID (C11111) as above registration, unique (different) DeviceID, unique (different) IP Address
Event: Order (O11111) - unique CC - order is approved

Event: Registration 10 days later - unique Customer name, address, email, etc.
Event: Session - same CustomerID (C22222) as above registration, same DeviceID as customer's initial session (e.g., D11111), unique IP Address
Event: Order (O22222) - Device ID linked to a Customer (suspicious), unique CC# (4111 1111 1111 1111) - order is approved

Event: Chargeback 90 days later - matched to first order & CC

Event: Registration 10 days later - unique Customer name, address, email, etc.
Event: Session - same CustomerID (C33333) as above registration, unique DeviceID, unique IP Address
Event: Order (O33333) - CC (4111 1111 1111 1111) matched to O22222, Customer from O22222 (C22222) linked by Device ID linked to Customer (C11111) who placed an order with a Chargeback - order is declined

### Scenario 7 - 4 Levels of linkage (suspicious - even without Chargeback)
Event: Registration - unique Customer name, address, email, etc.
Event: Session - same CustomerID (C11111) as registration, unique DeviceID (e.g. D11111), IP Address
Event: Order (O11111) - unique CC - order is approved

Event: Registration 10 days later - unique Customer name, address, email, etc.
Event: Session - same CustomerID (C22222) as above registration, same DeviceID as customer C11111's session (e.g., D11111), unique IP Address
Event: Order (O22222) - Device ID linked to a Customer (suspicious), unique CC# (4111 1111 1111 1111) - order is approved

Event: Registration 10 days later - unique Customer name (Joe Smith), address, email (Jsmith@yahoo.com), etc.
Event: Session - same CustomerID (C33333) as above registration, unique DeviceID, unique IP Address
Event: Order (O33333) - CC (4111 1111 1111 1111) matched to O22222

Event: Registration 10 days later - Customer name (Joe Smith) & email (Jsmith@yahoo.com) matched to C33333, unique address
Event: Session - same CustomerID (C44444) as above registration, unique DeviceID, unique IP Address
Event: Order (O44444) - Name and Email linked to Customer from O33333 -- order is declined -- too many layers of account linkage (despite the fact that there are no links to chargebacks or other "hard" fraud indicators)

## To Do:

- Add traversals
- Add credit card metadata including issuer, postal code, other location data
- Link customers to orders via sessions instead of separate path, remove redundant IP and device information from orders - should only be needed on associated session
- Add geoIP information to the session data?
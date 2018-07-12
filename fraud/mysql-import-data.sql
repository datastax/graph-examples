load data local infile '~/repos/graph-examples/fraud/data/customers.csv'
into table fraud.customers
fields terminated by '|' escaped by ''
ignore 1 lines;

load data local infile '~/repos/graph-examples/fraud/data/customerAddresses.csv'
into table fraud.customer_addresses
fields terminated by '|' escaped by ''
ignore 1 lines;

load data local infile '~/repos/graph-examples/fraud/data/sessions.csv'
into table fraud.sessions
fields terminated by '|' escaped by ''
ignore 1 lines;

load data local infile '~/repos/graph-examples/fraud/data/devices.csv'
into table fraud.devices
fields terminated by '|' escaped by ''
ignore 1 lines;

load data local infile '~/repos/graph-examples/fraud/data/orders.csv'
into table fraud.orders
fields terminated by '|' escaped by ''
ignore 1 lines;

load data local infile '~/repos/graph-examples/fraud/data/chargebacks.csv'
into table fraud.chargebacks
fields terminated by '|' escaped by ''
ignore 1 lines;

load data local infile '~/repos/graph-examples/fraud/data/creditCards.csv'
into table fraud.creditcards
fields terminated by '|' escaped by ''
ignore 1 lines;

load data local infile '~/repos/graph-examples/fraud/data/customerOrders.csv'
into table fraud.customer_orders
fields terminated by '|' escaped by ''
ignore 1 lines;

load data local infile '~/repos/graph-examples/fraud/data/orderChargebacks.csv'
into table fraud.order_chargebacks
fields terminated by '|' escaped by ''
ignore 1 lines;

load data local infile '~/repos/graph-examples/fraud/data/customerSessions.csv'
into table fraud.customer_sessions
fields terminated by '|' escaped by ''
ignore 1 lines;

load data local infile '~/repos/graph-examples/fraud/data/customerChargebacks.csv'
into table fraud.customer_chargebacks
fields terminated by '|' escaped by ''
ignore 1 lines;
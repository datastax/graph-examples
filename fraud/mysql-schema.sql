drop database if exists fraud;
create database fraud;
use `fraud`;

create table customers (
	customerid char(36) primary key,
	firstname varchar(64),
	lastname varchar(64),
	email varchar(128),
	phone varchar(64),
	createdtime timestamp
);

create table customer_addresses (
	customerid char(36) primary key,
	address varchar(128),
	city varchar(64),
	state varchar(64),
	postalcode varchar(64),
	countrycode varchar(64),
)

create table sessions (
	sessionid char(36) primary key,
	deviceid char(36),
	ipaddress varchar(128),
	createdtime timestamp
);

create table devices (
	deviceid char(36) primary key,
	type varchar(64),
	os varchar(64),
	osversion varchar(64)
);

create table orders (
	orderid char(36) primary key,
	createdtime timestamp,
	outcome varchar(64),
	creditcardhashed varchar(64),
	ipaddress varchar(128),
	amount decimal,
	deviceid char(36)
);

create table chargebacks (
	chargebacknumber integer primary key,
	amount decimal,
	createdtime timestamp,
	creditcardhashed varchar(64)
);

create table creditcards (
	creditcardhashed varchar(64) primary key,
	type varchar(64)
);

create table customer_orders (
	customerid char(36),
	orderid char(36),
	primary key (customerid, orderid)
);

create table order_chargebacks (
	orderid char(36),
	chargebacknumber integer,
	primary key (orderid, chargebacknumber)
);

create table customer_sessions (
	customerid char(36),
	sessionid char(36),
	primary key (customerid, sessionid)
);

create table customer_chargebacks (
	customerid char(36),
	chargebacknumber integer,
	primary key(customerid, chargebacknumber)
);
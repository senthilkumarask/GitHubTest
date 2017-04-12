-- drop table BBB_SHIP_METHOD_PRICES;

-- drop table BBB_SHIPPING_METHODS_MASTER;

CREATE TABLE BBB_SHIPPING_METHODS_MASTER (
	ship_method_cd 		varchar2(254)	NOT NULL,
	ship_method_name 	varchar2(254)	NULL,
	ship_method_description varchar2(254)	NULL,
	min_days_to_ship 	INTEGER	NULL,
	max_days_to_ship 	INTEGER	NULL,
	cut_off_time 		DATE	NULL,
	PRIMARY KEY(ship_method_cd)
);

CREATE TABLE BBB_SHIP_METHOD_PRICES (
	ship_method_price_id 	varchar2(254)	NOT NULL,
	site_id 		varchar2(254)	NULL REFERENCES site_configuration(id),
	ship_method_cd 		varchar2(254)	NULL REFERENCES BBB_SHIPPING_METHODS_MASTER(ship_method_cd),
	state_id 		varchar2(254)	NULL REFERENCES BBB_STATES(state_id),
	lower_limit 		number(28, 20)	NULL,
	upper_limit 		number(28, 20)	NULL,
	price 			varchar2(254)	NULL,
	free_ship_threshold 	number(28, 20)	NULL,
	is_gift_card 		number(1)	NULL,
	CHECK (is_gift_card IN (0, 1)),
	PRIMARY KEY(ship_method_price_id)
);


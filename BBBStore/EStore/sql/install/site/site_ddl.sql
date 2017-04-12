-- drop table BBB_REGISTRY_TYPE_SITE_MAPPINGS;

-- drop table BBB_APPLICABLE_PAYMENT_CARDS;

-- drop table BBB_NEXUS_STATES;

-- drop table BBB_STATES_LIST;

-- drop table BBB_APPLICABLE_SHIP_METHODS;

-- drop table BBB_PAYMENT_CARDS;

-- drop table BBB_SITE_ATTRIBUTES;


CREATE TABLE BBB_SITE_ATTRIBUTES (
	id 			varchar2(254)	NOT NULL REFERENCES site_configuration(id),
	resource_bundle 	varchar2(254)	NULL,
	site_icon 		varchar2(254)	NULL,
	default_country 	varchar2(254)	NULL,
	css_file 		varchar2(254)	NULL,
	order_shipped_from_address varchar2(254)	NULL,
	back_in_stock_from_address varchar2(254)	NULL,
	new_password_from_address varchar2(254)	NULL,
	default_ship_method 	varchar2(254)	NULL REFERENCES BBB_SHIPPING_METHODS_MASTER(ship_method_cd),
	PRIMARY KEY(id)
);



CREATE TABLE BBB_APPLICABLE_SHIP_METHODS (
	id 			varchar2(254)	NOT NULL REFERENCES site_configuration(id),
	ship_method_cd 		varchar2(254)	NOT NULL REFERENCES BBB_SHIPPING_METHODS_MASTER(ship_method_cd),
	PRIMARY KEY(id, ship_method_cd)
);



CREATE TABLE BBB_STATES_LIST (
	id 			varchar2(254)	NOT NULL REFERENCES site_configuration(id),
	state_id 		varchar2(254)	NOT NULL REFERENCES BBB_STATES(state_id),
	PRIMARY KEY(id, state_id)
);



CREATE TABLE BBB_NEXUS_STATES (
	id 			varchar2(254)	NOT NULL REFERENCES site_configuration(id),
	state_id 		varchar2(254)	NOT NULL REFERENCES BBB_STATES(state_id),
	PRIMARY KEY(id, state_id)
);



CREATE TABLE BBB_REGISTRY_TYPE_SITE_MAPPING (
	id 			varchar2(254)	NOT NULL REFERENCES site_configuration(id),
	registry_type_id 	varchar2(254)	NOT NULL REFERENCES BBB_REGISTRY_TYPE(registry_type_id),
	PRIMARY KEY(id, registry_type_id)
);



CREATE TABLE BBB_PAYMENT_CARDS (
	payment_card_id 	varchar2(254)	NOT NULL,
	card_name 		varchar2(254)	NULL,
	card_code 		varchar2(254)	NULL,
	card_image 		varchar2(254)	NULL,
	PRIMARY KEY(payment_card_id)
);

CREATE TABLE BBB_APPLICABLE_PAYMENT_CARDS (
	id 			varchar2(254)	NOT NULL REFERENCES site_configuration(id),
	payment_card_id 	varchar2(254)	NOT NULL REFERENCES BBB_PAYMENT_CARDS(payment_card_id),
	PRIMARY KEY(id, payment_card_id)
);


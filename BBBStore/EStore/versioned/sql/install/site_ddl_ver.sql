-- drop table BBB_REGISTRY_TYPE_SITE_MAPPINGS;

-- drop table BBB_APPLICABLE_PAYMENT_CARDS;

-- drop table BBB_NEXUS_STATES;

-- drop table BBB_STATES_LIST;

-- drop table BBB_APPLICABLE_SHIP_METHODS;

-- drop table BBB_PAYMENT_CARDS;

-- drop table BBB_SITE_ATTRIBUTES;


CREATE TABLE BBB_SITE_ATTRIBUTES (
	asset_version INTEGER NOT NULL,
	id 			varchar2(254)	NOT NULL,
	resource_bundle 	varchar2(254)	NULL,
	site_icon 		varchar2(254)	NULL,
	default_country 	varchar2(254)	NULL,
	css_file 		varchar2(254)	NULL,
	order_shipped_from_address varchar2(254)	NULL,
	back_in_stock_from_address varchar2(254)	NULL,
	new_password_from_address varchar2(254)	NULL,
	default_ship_method 	varchar2(254)	NULL,
	PRIMARY KEY(id,asset_version)
);

CREATE TABLE BBB_APPLICABLE_SHIP_METHODS (
	asset_version INTEGER NOT NULL,
	id 			varchar2(254)	NOT NULL,
	ship_method_cd 		varchar2(254)	NOT NULL,
	PRIMARY KEY(id, ship_method_cd,asset_version)
);

CREATE TABLE BBB_STATES_LIST (
	asset_version INTEGER NOT NULL,
	id 			varchar2(254)	NOT NULL,
	state_id 		varchar2(254)	NOT NULL,
	PRIMARY KEY(id, state_id,asset_version)
);

CREATE TABLE BBB_NEXUS_STATES (
	asset_version INTEGER NOT NULL,
	id 			varchar2(254)	NOT NULL,
	state_id 		varchar2(254)	NOT NULL,
	PRIMARY KEY(id, state_id,asset_version)
);

CREATE TABLE BBB_REGISTRY_TYPE (
	asset_version INTEGER NOT NULL,
	workspace_id varchar2(254) NOT NULL,
	branch_id varchar2(254) NOT NULL,
	is_head number(1) NOT NULL,
	version_deleted number(1) NOT NULL,
	version_editable number(1) NOT NULL,
	pred_version INTEGER NULL,
	checkin_date TIMESTAMP NULL,
	registry_type_id 	varchar2(254)	NOT NULL,
	registry_type_name 	varchar2(254)	NOT NULL,
	registry_type_desc 	varchar2(254)	NOT NULL,
	registry_type_index 	varchar2(254)	NOT NULL,
	PRIMARY KEY(registry_type_id, asset_version)
);
CREATE TABLE BBB_REGISTRY_TYPE_SITE_MAPPING (
	asset_version INTEGER NOT NULL,
	id 			varchar2(254)	NOT NULL,
	registry_type_id 	varchar2(254)	NOT NULL,
	PRIMARY KEY(id, registry_type_id,asset_version)
);


CREATE TABLE BBB_PAYMENT_CARDS (
	asset_version INTEGER NOT NULL,
	workspace_id varchar2(254) NOT NULL,
	branch_id varchar2(254) NOT NULL,
	is_head number(1) NOT NULL,
	version_deleted number(1) NOT NULL,
	version_editable number(1) NOT NULL,
	pred_version INTEGER NULL,
	checkin_date TIMESTAMP NULL,
	payment_card_id 	varchar2(254)	NOT NULL,
	card_name 		varchar2(254)	NULL,
	card_code 		varchar2(254)	NULL,
	card_image 		varchar2(254)	NULL,
	PRIMARY KEY(payment_card_id,asset_version)
);

CREATE TABLE BBB_APPLICABLE_PAYMENT_CARDS (
	asset_version INTEGER NOT NULL,
	id 			varchar2(254)	NOT NULL,
	payment_card_id 	varchar2(254)	NOT NULL,
	PRIMARY KEY(id, payment_card_id,asset_version)
);
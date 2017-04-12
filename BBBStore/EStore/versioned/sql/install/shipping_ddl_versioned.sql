-- drop table BBB_SHIP_METHOD_PRICES;

-- drop table BBB_SHIPPING_METHODS_MASTER;

CREATE TABLE BBB_SHIPPING_METHODS_MASTER (
	asset_version INTEGER NOT NULL,
	workspace_id varchar2(254) NOT NULL,
	branch_id varchar2(254) NOT NULL,
	is_head number(1) NOT NULL,
	version_deleted number(1) NOT NULL,
	version_editable number(1) NOT NULL,
	pred_version INTEGER NULL,
	checkin_date TIMESTAMP NULL,
	ship_method_cd 		varchar2(254)	NOT NULL,
	ship_method_name 	varchar2(254)	NULL,
	ship_method_description varchar2(254)	NULL,
	min_days_to_ship 	INTEGER	NULL,
	max_days_to_ship 	INTEGER	NULL,
	cut_off_time 		DATE	NULL,
	PRIMARY KEY(ship_method_cd,asset_version)
);

CREATE TABLE BBB_SHIP_METHOD_PRICES (
	asset_version INTEGER NOT NULL,
	workspace_id varchar2(254) NOT NULL,
	branch_id varchar2(254) NOT NULL,
	is_head number(1) NOT NULL,
	version_deleted number(1) NOT NULL,
	version_editable number(1) NOT NULL,
	pred_version INTEGER NULL,
	checkin_date TIMESTAMP NULL,
	ship_method_price_id 	varchar2(254)	NOT NULL,
	site_id 		varchar2(254)	NULL,
	ship_method_cd 		varchar2(254)	NULL,
	state_id 		varchar2(254)	NULL,
	lower_limit 		number(28, 20)	NULL,
	upper_limit 		number(28, 20)	NULL,
	price 			varchar2(254)	NULL,
	free_ship_threshold 	number(28, 20)	NULL,
	is_gift_card 		number(1)	NULL,
	CHECK (is_gift_card IN (0, 1)),
	PRIMARY KEY(ship_method_price_id,asset_version)
);


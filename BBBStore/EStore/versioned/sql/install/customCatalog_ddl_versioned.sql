-- drop table BBB_SUB_DEPT;

-- drop table BBB_DEPT;

-- drop table BBB_SKU_ATTRIBUTE_SITES;

-- drop table BBB_SKU_ATTRIBUTES_INFO;

-- drop table BBB_APLCBLE_SHIP_METHOD_STATES;

-- drop table BBB_STATES;

-- drop table BBB_REBATE_INFO;

-- drop table BBB_OTHER_MEDIA_SITES;

-- drop table BBB_OTHER_MEDIA;

-- drop table BBB_SKU_MEDIA_RELN;

-- drop table BBB_ROLLUP_TYPE;

-- drop table BBB_PRD_ROLLUP_RELN;

-- drop table BBB_BRANDS;

-- drop table BBB_PRD_RELN;

-- drop table BBB_SKU_OTHER_MEDIA;

-- drop table BBB_SKU_ELIGIBLE_SHIPMETHODS;

-- drop table BBB_SKU_FREE_SHIPPING;

-- drop table BBB_SKU_ATTRIBUTES;

-- drop table BBB_SKU_NONSHIPPABLE;

-- drop table BBB_SKU_REBATES;

-- drop table BBB_SKU_MEDIA;

-- drop table BBB_SKU;

-- drop table BBB_PRD_COLL_ROLLUP_RELN;

-- drop table BBB_PRD_PRD_RELN;

-- drop table BBB_PRODUCT_TABS;

-- drop table BBB_PROD_COLOR_PICTURES;

-- drop table BBB_PRD_MEDIA;

-- drop table BBB_PRODUCT;

-- drop table BBB_CATEGORY;


CREATE TABLE BBB_CATEGORY (
	category_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	is_college 		number(1)	NULL,
	sequence_num 		INTEGER	NULL,
	node_type 		varchar2(254)	NULL,
	phantom_category 	number(1)	NULL,
	disable_flag 		number(1)	NULL,
	shop_guide_id 		varchar2(254)	NULL,
	CHECK (is_college IN (0, 1)),
	CHECK (phantom_category IN (0, 1)),
	CHECK (disable_flag IN (0, 1)),
	PRIMARY KEY(category_id, asset_version)
);




CREATE TABLE BBB_BRANDS (
	brand_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	brand_name 		varchar2(254)	NULL,
	brand_descrip 		varchar2(254)	NULL,
	brand_image 		varchar2(254)	NULL,
	version_deleted 	number(1)	NULL,
	version_editable 	number(1)	NULL,
	pred_version 		INTEGER	NULL,
	workspace_id 		varchar2(254)	NULL,
	branch_id 		varchar2(254)	NULL,
	is_head 		number(1)	NULL,
	checkin_date 		DATE	NULL,
	CHECK (version_deleted IN (0, 1)),
	CHECK (version_editable IN (0, 1)),
	CHECK (is_head IN (0, 1)),
	PRIMARY KEY(brand_id, asset_version)
);

CREATE TABLE BBB_ROLLUP_TYPE (
	rollup_type_code 	varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	rollup_attribute 	varchar2(254)	NULL,
	rollup_order 		INTEGER	NULL,
	version_deleted 	number(1)	NULL,
	version_editable 	number(1)	NULL,
	pred_version 		INTEGER	NULL,
	workspace_id 		varchar2(254)	NULL,
	branch_id 		varchar2(254)	NULL,
	is_head 		number(1)	NULL,
	checkin_date 		DATE	NULL,
	CHECK (version_deleted IN (0, 1)),
	CHECK (version_editable IN (0, 1)),
	CHECK (is_head IN (0, 1)),
	PRIMARY KEY(rollup_type_code, asset_version)
);

CREATE TABLE BBB_PRD_ROLLUP_RELN (
	asset_version 		INTEGER	NOT NULL,
	product_id 		varchar2(254),
	rollup_order 		INTEGER	NOT NULL,
	rollup_type_code 	varchar2(254),
	PRIMARY KEY(product_id, rollup_order, asset_version)
);




CREATE TABLE BBB_PRODUCT (
	product_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	web_offered_flag 	number(1)	NULL,
	disable_flag 		number(1)	NULL,
	collection_flag 	number(1)	NULL,
	DISABLE_FOREVER_PDP_FLAG      number(1)	NULL,
  	BAB_DISABLE_FOREVER_PDP_FLAG  number(1)	NULL,
  	CA_DISABLE_FOREVER_PDP_FLAG   number(1)	NULL,
  	GS_DISABLE_FOREVER_PDP_FLAG   number(1)	NULL,
	price_range_descrip 	varchar2(254)	NULL,
	sku_low_price 		number(28, 20)	NULL,
	sku_high_price 		number(28, 20)	NULL,
	swatch_flag 		number(1)	NULL,
	brand_id 		varchar2(254)	NULL,
	email_out_of_stock 	number(1)	NULL,
	rollup_type_code 	varchar2(254)	NULL,
	show_images_in_collection varchar2(254)	NULL,
	rollup_type_col 	varchar2(254)	NULL,
	college_id 		varchar2(254)	NULL,
	CHECK (web_offered_flag IN (0, 1)),
	CHECK (disable_flag IN (0, 1)),
	CHECK (collection_flag IN (0, 1)),
	CHECK (swatch_flag IN (0, 1)),
	CHECK (email_out_of_stock IN (0, 1)),
	PRIMARY KEY(product_id, asset_version)
);



CREATE TABLE BBB_PRD_RELN (
	product_relan_id 	varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	product_id 		varchar2(254)	NULL,
	lead_prd_flag 		number(1)	NULL,
	version_deleted 	number(1)	NULL,
	version_editable 	number(1)	NULL,
	pred_version 		INTEGER	NULL,
	workspace_id 		varchar2(254)	NULL,
	branch_id 		varchar2(254)	NULL,
	is_head 		number(1)	NULL,
	checkin_date 		DATE	NULL,
	CHECK (lead_prd_flag IN (0, 1)),
	CHECK (version_deleted IN (0, 1)),
	CHECK (version_editable IN (0, 1)),
	CHECK (is_head IN (0, 1)),
	PRIMARY KEY(product_relan_id, asset_version)
);

CREATE TABLE BBB_PRD_PRD_RELN (
	product_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	sequence_num 		INTEGER	NOT NULL,
	product_relan_id 	varchar2(254)	NULL,
	PRIMARY KEY(product_id, asset_version, sequence_num)
);



CREATE TABLE BBB_PRD_COLL_ROLLUP_RELN (
	asset_version 		INTEGER	NOT NULL,
	product_relan_id 	varchar2(254),
	rollup_order 		INTEGER	NOT NULL,
	rollup_type_code 	varchar2(254),
	PRIMARY KEY(product_relan_id, rollup_order, asset_version)
);


CREATE TABLE BBB_PRD_MEDIA (
	product_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	zoom_image_id 		varchar2(254)	NULL,
	zoom_index 		INTEGER	NULL,
	collection_thumbnail 	varchar2(254)	NULL,
	PRIMARY KEY(product_id, asset_version)
);



CREATE TABLE BBB_PROD_COLOR_PICTURES (
	product_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	color_cd 		varchar2(254)	NULL,
	medium_image_id 	varchar2(254)	NULL,
	swatch_image_id 	varchar2(254)	NULL,
	zoom_image_id 		varchar2(254)	NULL,
	PRIMARY KEY(product_id, asset_version)
);



CREATE TABLE BBB_PRODUCT_TABS (
	product_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	tab_name 		varchar2(254)	NULL,
	tab_content 		varchar2(254)	NULL,
	sequence_num 		varchar2(254)	NULL,
	PRIMARY KEY(product_id, asset_version)
);



CREATE TABLE BBB_REBATE_INFO (
	rebate_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	descrip 		varchar2(254)	NULL,
	rebate_url 		varchar2(254)	NULL,
	end_dt 			DATE	NULL,
	version_deleted 	number(1)	NULL,
	version_editable 	number(1)	NULL,
	pred_version 		INTEGER	NULL,
	workspace_id 		varchar2(254)	NULL,
	branch_id 		varchar2(254)	NULL,
	is_head 		number(1)	NULL,
	checkin_date 		DATE	NULL,
	CHECK (version_deleted IN (0, 1)),
	CHECK (version_editable IN (0, 1)),
	CHECK (is_head IN (0, 1)),
	PRIMARY KEY(rebate_id, asset_version)
);

CREATE TABLE BBB_STATES (
	state_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	descrip 		varchar2(254)	NULL,
	country_cd 		varchar2(254)	NULL,
	bopus_flag 		number(1)	NULL,
	version_deleted 	number(1)	NULL,
	version_editable 	number(1)	NULL,
	pred_version 		INTEGER	NULL,
	workspace_id 		varchar2(254)	NULL,
	branch_id 		varchar2(254)	NULL,
	is_head 		number(1)	NULL,
	checkin_date 		DATE	NULL,
	CHECK (bopus_flag IN (0, 1)),
	CHECK (version_deleted IN (0, 1)),
	CHECK (version_editable IN (0, 1)),
	CHECK (is_head IN (0, 1)),
	PRIMARY KEY(state_id, asset_version)
);

CREATE TABLE BBB_APLCBLE_SHIP_METHOD_STATES (
	state_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	ship_method_cd 		varchar2(254)	NOT NULL,
	PRIMARY KEY(state_id, asset_version, ship_method_cd)
);



CREATE TABLE BBB_DEPT (
	jda_dept_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	descrip 		varchar2(254)	NULL,
	version_deleted 	number(1)	NULL,
	version_editable 	number(1)	NULL,
	pred_version 		INTEGER	NULL,
	workspace_id 		varchar2(254)	NULL,
	branch_id 		varchar2(254)	NULL,
	is_head 		number(1)	NULL,
	checkin_date 		DATE	NULL,
	CHECK (version_deleted IN (0, 1)),
	CHECK (version_editable IN (0, 1)),
	CHECK (is_head IN (0, 1)),
	PRIMARY KEY(jda_dept_id, asset_version)
);

CREATE TABLE BBB_SUB_DEPT (
	jda_sub_dept_id 	varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	jda_dept_id 		varchar2(254)	NULL,
	descrip 		varchar2(254)	NULL,
	version_deleted 	number(1)	NULL,
	version_editable 	number(1)	NULL,
	pred_version 		INTEGER	NULL,
	workspace_id 		varchar2(254)	NULL,
	branch_id 		varchar2(254)	NULL,
	is_head 		number(1)	NULL,
	checkin_date 		DATE	NULL,
	CHECK (version_deleted IN (0, 1)),
	CHECK (version_editable IN (0, 1)),
	CHECK (is_head IN (0, 1)),
	PRIMARY KEY(jda_sub_dept_id, asset_version)
);


CREATE TABLE BBB_OTHER_MEDIA (
	media_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	media_type 		varchar2(254)	NULL,
	provider_id 		varchar2(254)	NULL,
	media_source 		varchar2(254)	NULL,
	media_description 	varchar2(254)	NULL,
	comments 		varchar2(254)	NULL,
	media_transcript 	varchar2(254)	NULL,
	version_deleted 	number(1)	NULL,
	version_editable 	number(1)	NULL,
	pred_version 		INTEGER	NULL,
	workspace_id 		varchar2(254)	NULL,
	branch_id 		varchar2(254)	NULL,
	is_head 		number(1)	NULL,
	checkin_date 		DATE	NULL,
	CHECK (version_deleted IN (0, 1)),
	CHECK (version_editable IN (0, 1)),
	CHECK (is_head IN (0, 1)),
	PRIMARY KEY(media_id, asset_version)
);

CREATE TABLE BBB_OTHER_MEDIA_SITES (
	media_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	site_id 		varchar2(254)	NOT NULL,
	PRIMARY KEY(media_id, asset_version, site_id)
);



CREATE TABLE BBB_SKU_MEDIA_RELN (
	media_reln_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	media_id 		varchar2(254)	NULL,
	start_date 		DATE	NULL,
	end_date 		DATE	NULL,
	comments 		varchar2(254)	NULL,
	widget_id 		varchar2(254)	NULL,
	version_deleted 	number(1)	NULL,
	version_editable 	number(1)	NULL,
	pred_version 		INTEGER	NULL,
	workspace_id 		varchar2(254)	NULL,
	branch_id 		varchar2(254)	NULL,
	is_head 		number(1)	NULL,
	checkin_date 		DATE	NULL,
	CHECK (version_deleted IN (0, 1)),
	CHECK (version_editable IN (0, 1)),
	CHECK (is_head IN (0, 1)),
	PRIMARY KEY(media_reln_id, asset_version)
);

CREATE TABLE BBB_SKU (
	sku_id 			varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	web_offered_flag 	number(1)	NULL,
	disable_flag 		number(1)	NULL,
	jda_dept_id 		varchar2(254)	NULL,
	jda_sub_dept_id 	varchar2(254)	NULL,
	gift_cert_flag 		number(1)	NULL,
	college_sku_flag 	number(1)	NULL,
	sku_type 		varchar2(254)	NULL,
	email_out_of_stock 	number(1)	NULL,
	color 			varchar2(254)	NULL,
	color_group 		varchar2(254)	NULL,
	sku_size 		varchar2(254)	NULL,
	anywhere_zoom 		number(1)	NULL,
	gift_wrap_eligible 	number(1)	NULL,
	vdc_sku_type 		varchar2(254)	NULL,
	upc 			varchar2(254)	NULL,
	shipping_surcharge 	number(28, 20)	NULL,
	bopus_exclusion_flag 	number(1)	NULL,
	CHECK (web_offered_flag IN (0, 1)),
	CHECK (disable_flag IN (0, 1)),
	CHECK (gift_cert_flag IN (0, 1)),
	CHECK (college_sku_flag IN (0, 1)),
	CHECK (email_out_of_stock IN (0, 1)),
	CHECK (anywhere_zoom IN (0, 1)),
	CHECK (gift_wrap_eligible IN (0, 1)),
	CHECK (bopus_exclusion_flag IN (0, 1)),
	PRIMARY KEY(sku_id, asset_version)
);



CREATE TABLE BBB_SKU_OTHER_MEDIA (
	sku_id 			varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	sequence_num 		INTEGER	NOT NULL,
	media_reln_id 		varchar2(254)	NULL,
	PRIMARY KEY(sku_id, asset_version, sequence_num)
);



CREATE TABLE BBB_SKU_FREE_SHIPPING (
	sku_id 			varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	ship_method_cd 		varchar2(254)	NOT NULL,
	PRIMARY KEY(sku_id, asset_version, ship_method_cd)
);



CREATE TABLE BBB_SKU_ELIGIBLE_SHIPMETHODS (
	sku_id 			varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	ship_method_cd 		varchar2(254)	NOT NULL,
	PRIMARY KEY(sku_id, asset_version, ship_method_cd)
);



CREATE TABLE BBB_SKU_MEDIA (
	sku_id 			varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	zoom_image_id 		varchar2(254)	NULL,
	zoom_index 		INTEGER	NULL,
	PRIMARY KEY(sku_id, asset_version)
);



CREATE TABLE BBB_SKU_REBATES (
	sku_id 			varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	rebate_id 		varchar2(254)	NOT NULL,
	PRIMARY KEY(sku_id, asset_version, rebate_id)
);



CREATE TABLE BBB_SKU_NONSHIPPABLE (
	sku_id 			varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	state_id 		varchar2(254)	NOT NULL,
	PRIMARY KEY(sku_id, asset_version, state_id)
);




CREATE TABLE BBB_SKU_ATTRIBUTES_INFO (
	sku_attribute_id 	varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	display_descrip 	varchar2(254)	NULL,
	image_url 		varchar2(254)	NULL,
	image_action_url 	varchar2(254)	NULL,
	action_text 		varchar2(254)	NULL,
	action_url 		varchar2(254)	NULL,
	place_holder 		varchar2(254)	NULL,
	start_date 		DATE	NULL,
	end_date 		DATE	NULL,
	version_deleted 	number(1)	NULL,
	version_editable 	number(1)	NULL,
	pred_version 		INTEGER	NULL,
	workspace_id 		varchar2(254)	NULL,
	branch_id 		varchar2(254)	NULL,
	is_head 		number(1)	NULL,
	checkin_date 		DATE	NULL,
	CHECK (version_deleted IN (0, 1)),
	CHECK (version_editable IN (0, 1)),
	CHECK (is_head IN (0, 1)),
	PRIMARY KEY(sku_attribute_id, asset_version)
);

CREATE TABLE BBB_SKU_ATTRIBUTE_SITES (
	sku_attribute_id 	varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	site_id 		varchar2(254)	NOT NULL,
	PRIMARY KEY(sku_attribute_id, asset_version, site_id)
);



CREATE TABLE BBB_SKU_ATTRIBUTES (
	sku_id 			varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	priority 		INTEGER	NOT NULL,
	sku_attribute_id 	varchar2(254)	NULL,
	PRIMARY KEY(sku_id, asset_version, priority)
);







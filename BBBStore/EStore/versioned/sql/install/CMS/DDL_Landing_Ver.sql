-- drop table BBB_PROMOBOX;

-- drop table BBB_PROMOIMAGE;

-- drop table BBB_PBOX_CONTAINERS;

-- drop table BBB_LANDING_BRANDS;

-- drop table BBB_LANDING_COLLEGE_DEALS;

-- drop table BBB_LANDING_KEEPSAKE_SHOP;

-- drop table BBB_LANDING_TOPREGISTRY;

-- drop table BBB_LAND_PBOX_TMP_SECOND;

-- drop table BBB_LAND_PBOX_TMP_FIRST;

-- drop table BBB_LANDING_PIMAGE_RCAT;

-- drop table BBB_LANDING_PIMAGE_BRIDAL;

-- drop table BBB_LANDING_PROMOBOX;

-- drop table BBB_LANDING_CATEGORY;

-- drop table BBB_LANDING_TEMPLATE;

-- drop table BBB_LANDING_SITE;



CREATE INDEX BBB_LANDING_SITE_landing_template_idx ON BBB_LANDING_SITE(landing_template_id, site_id);
CREATE INDEX BBB_LANDING_CATEGORY_landing_template_idx ON BBB_LANDING_CATEGORY(landing_template_id);
CREATE INDEX BBB_LANDING_PROMOBOX_landing_template_idx ON BBB_LANDING_PROMOBOX(landing_template_id, sequence_num);
CREATE INDEX BBB_LANDING_PIMAGE_BRIDAL_landing_template_idx ON BBB_LANDING_PIMAGE_BRIDAL(landing_template_id, sequence_num);
CREATE INDEX BBB_LANDING_PIMAGE_RCAT_landing_template_idx ON BBB_LANDING_PIMAGE_RCAT(landing_template_id, sequence_num);
CREATE INDEX BBB_LAND_PBOX_TMP_FIRST_landing_template_idx ON BBB_LAND_PBOX_TMP_FIRST(landing_template_id, sequence_num);
CREATE INDEX BBB_LAND_PBOX_TMP_SECOND_landing_template_idx ON BBB_LAND_PBOX_TMP_SECOND(landing_template_id, sequence_num);
CREATE INDEX BBB_LANDING_TOPREGISTRY_landing_template_idx ON BBB_LANDING_TOPREGISTRY(landing_template_id, sequence_num);
CREATE INDEX BBB_LANDING_KEEPSAKE_SHOP_landing_template_idx ON BBB_LANDING_KEEPSAKE_SHOP(landing_template_id, sequence_num);
CREATE INDEX BBB_LANDING_COLLEGE_DEALS_landing_template_idx ON BBB_LANDING_COLLEGE_DEALS(landing_template_id, sequence_num);
CREATE INDEX BBB_LANDING_BRANDS_landing_template_idx ON BBB_LANDING_BRANDS(landing_template_id, sequence_num);


 

CREATE TABLE BBB_LANDING_SITE (
	landing_template_id 	varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	site_id 		varchar2(40)	NOT NULL,
	PRIMARY KEY(landing_template_id, asset_version, site_id)
);



CREATE TABLE BBB_LANDING_TEMPLATE (
	landing_template_id 	varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	PAGE_NAME 		INTEGER	NULL,
	PAGE_TITLE 		varchar2(254)	NULL,
	MARKETING_BANNER_URL 	varchar2(254)	NULL,
	PROMO_SMALL_CONTENT 	CLOB	NULL,
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
	PRIMARY KEY(landing_template_id, asset_version)
);

CREATE TABLE BBB_LANDING_CATEGORY (
	landing_template_id 	varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	category_id 		varchar2(40)	NULL,
	PRIMARY KEY(landing_template_id, asset_version)
);

CREATE TABLE BBB_LANDING_PROMOBOX (
	landing_template_id 	varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	sequence_num 		INTEGER	NOT NULL,
	promobox_id 		varchar2(40)	NULL,
	PRIMARY KEY(landing_template_id, asset_version, sequence_num)
);


CREATE TABLE BBB_LANDING_PIMAGE_BRIDAL (
	landing_template_id 	varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	sequence_num 		INTEGER	NOT NULL,
	promo_image_id 		varchar2(40)	NULL,
	PRIMARY KEY(landing_template_id, asset_version, sequence_num)
);


CREATE TABLE BBB_LANDING_PIMAGE_RCAT (
	landing_template_id 	varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	sequence_num 		INTEGER	NOT NULL,
	promo_image_id 		varchar2(40)	NULL,
	PRIMARY KEY(landing_template_id, asset_version, sequence_num)
);


CREATE TABLE BBB_LAND_PBOX_TMP_FIRST (
	landing_template_id 	varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	sequence_num 		INTEGER	NOT NULL,
	promoboxtemp_id 	varchar2(40)	NULL,
	PRIMARY KEY(landing_template_id, asset_version, sequence_num)
);


CREATE TABLE BBB_LAND_PBOX_TMP_SECOND (
	landing_template_id 	varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	sequence_num 		INTEGER	NOT NULL,
	promoboxtemp_id 	varchar2(40)	NULL,
	PRIMARY KEY(landing_template_id, asset_version, sequence_num)
);


CREATE TABLE BBB_PBOX_CONTAINERS (
	promoboxtemp_id 	varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	Promo_Containers1 	varchar2(40)	NULL,
	Promo_Containers2 	varchar2(40)	NULL,
	Promo_Containers3 	varchar2(40)	NULL,
	image_url 		varchar2(254)	NULL,
	image_alt_text 		varchar2(254)	NULL,
	link_label 		varchar2(254)	NULL,
	link_url 		varchar2(254)	NULL,
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
	PRIMARY KEY(promoboxtemp_id,asset_version)
);

CREATE TABLE BBB_LANDING_TOPREGISTRY (
	landing_template_id 	varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	sequence_num 		INTEGER	NOT NULL,
	product_id 		varchar2(40)	NULL,
	PRIMARY KEY(landing_template_id, asset_version, sequence_num)
);


CREATE TABLE BBB_LANDING_KEEPSAKE_SHOP (
	landing_template_id 	varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	sequence_num 		INTEGER	NOT NULL,
	product_id 		varchar2(40)	NULL,
	PRIMARY KEY(landing_template_id, asset_version, sequence_num)
);


CREATE TABLE BBB_LANDING_COLLEGE_DEALS (
	landing_template_id 	varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	sequence_num 		INTEGER	NOT NULL,
	product_id 		varchar2(40)	NULL,
	PRIMARY KEY(landing_template_id, asset_version, sequence_num)
);


CREATE TABLE BBB_LANDING_BRANDS (
	landing_template_id 	varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	sequence_num 		INTEGER	NOT NULL,
	brand_id 		varchar2(40)	NULL,
	PRIMARY KEY(landing_template_id, asset_version, sequence_num)
);

CREATE TABLE BBB_PROMOIMAGE (
	promo_image_id 		varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	image_url 		varchar2(254)	NULL,
	image_alt_text 		varchar2(254)	NULL,
	link_label 		varchar2(254)	NULL,
	link_url 		varchar2(254)	NULL,
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
	PRIMARY KEY(promo_image_id, asset_version)
);

CREATE TABLE BBB_PROMOBOX (
	promobox_id 		varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	image_url 		varchar2(254)	NULL,
	image_alt_text 		varchar2(254)	NULL,
	image_map_name 		varchar2(254)	NULL,
	image_map_content 	varchar2(254)	NULL,
	promo_box__content 	varchar2(254)	NULL,
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
	PRIMARY KEY(promobox_id, asset_version)
);



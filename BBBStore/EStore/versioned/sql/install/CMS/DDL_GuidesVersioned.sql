 

CREATE TABLE BBB_GUIDES (
	guides_id 			varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	content_type 		INTEGER	NULL,
	guides_category 	INTEGER	NULL,
	title 				varchar2(254)	NULL,
	image_url 			varchar2(254)	NULL,
	image_alt_text 		varchar2(254)	NULL,
	short_description 	varchar2(2000)	NULL,
	version_deleted 	number(1)	NULL,
	version_editable 	number(1)	NULL,
	pred_version 		INTEGER	NULL,
	workspace_id 		varchar2(254)	NULL,
	branch_id 			varchar2(254)	NULL,
	is_head 			number(1)	NULL,
	checkin_date 		DATE	NULL,
	CHECK (version_deleted IN (0, 1)),
	CHECK (version_editable IN (0, 1)),
	CHECK (is_head IN (0, 1)),
	PRIMARY KEY(guides_id,asset_version)
);

CREATE TABLE GUIDES_LONG_DESC (
	guides_id 			varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	long_description 	CLOB	NULL,
	PRIMARY KEY(guides_id,asset_version)
);

CREATE TABLE BBB_GUIDES_SITE (
	guides_id 		varchar2(40)	NOT NULL,
	asset_version 	INTEGER	NOT NULL,
	site_id 		varchar2(40)	NOT NULL,
	PRIMARY KEY(guides_id, site_id,asset_version)
);



CREATE INDEX GUIDES_LONG_DESC_guides_idx ON GUIDES_LONG_DESC(guides_id);
CREATE INDEX BBB_GUIDES_SITE_guides_idx ON BBB_GUIDES_SITE(guides_id, site_id);

 

CREATE TABLE BBB_LBL_TXT (
	LBL_TXT_ID 		varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	KEY 			varchar2(254)	NULL,
	LANGUAGE 		INTEGER	NULL,
	TEMPLATE_TYPE 		INTEGER	NULL,
	LABEL_VALUE 		varchar2(1000)	NULL,
	TEXTAREA_VALUE 		varchar2(4000)	NULL,
	ERROR_MSG 		varchar2(1000)	NULL,
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
	PRIMARY KEY(LBL_TXT_ID, asset_version)
);

CREATE TABLE BBB_LBL_TXT_SITE_TRANSLATIONS (
	LBL_TXT_ID 		varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	site_id 		varchar2(40)	NOT NULL,
	translation_id 		varchar2(40)	NULL,
	PRIMARY KEY(LBL_TXT_ID, asset_version, site_id)
);

 

CREATE TABLE BBB_LBL_TXT_TRANSLATIONS (
	translation_id 		varchar2(40)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	LABEL_VALUE 		varchar2(1000)	NULL,
	TEXTAREA_VALUE 		varchar2(4000)	NULL,
	ERROR_MSG 		varchar2(1000)	NULL,
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
	PRIMARY KEY(translation_id, asset_version)
);


CREATE TABLE BBB_SITE_TIMEZONE (
	time_zone_id 		varchar2(254)	NOT NULL,
	asset_version 		INTEGER	NOT NULL,
	time_zone 		varchar2(254)	NULL,
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
	PRIMARY KEY(time_zone_id,asset_version)
);


CREATE TABLE BBB_TIME_ZONE_LIST (
    asset_version INTEGER NOT NULL,
	id 			varchar2(254)	NOT NULL,
	time_zone_id 		varchar2(254)	NOT NULL,
	PRIMARY KEY(id, time_zone_id,asset_version)
);
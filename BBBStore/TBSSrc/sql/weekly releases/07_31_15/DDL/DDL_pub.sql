SET ECHO ON;
SET DEFINE OFF;

CREATE TABLE BBB_PUB.TBS_HOLIDAY_MESSAGING (
	asset_version INTEGER NOT NULL,
	workspace_id varchar2(254) NOT NULL,
	branch_id varchar2(254) NOT NULL,
	is_head number(1) NOT NULL,
	version_deleted number(1) NOT NULL,
	version_editable number(1) NOT NULL,
	pred_version INTEGER NULL,
	checkin_date TIMESTAMP NULL,
	holiday_message_id 	varchar2(254)	NOT NULL,
	start_time 		DATE	NULL,
	end_time 		DATE	NULL,
	item_availability 	varchar2(254)	NOT NULL,
	standard_label 		varchar2(254)	NULL,
	expedited_label 	varchar2(254)	NULL,
	name 			varchar2(254)	NULL,
	PRIMARY KEY(holiday_message_id, asset_version)
);

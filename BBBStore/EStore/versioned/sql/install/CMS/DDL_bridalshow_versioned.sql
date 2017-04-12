
drop table bbb_bridalshow_site;

drop table bbb_bridalshow_state;

drop table bbb_bridalshow_template;

drop table state;


CREATE TABLE bbb_bridalshow_template (
	bridalshow_template_id 	varchar2(254)	NOT NULL,
	show_date 		TIMESTAMP(6),
	show_name 		varchar2(254)	NULL,
	show_address 		varchar2(254)	NULL,
	show_phone 		varchar2(254)	NULL,
	show_view_directions 	varchar2(254)	NULL,
	asset_version INT NOT NULL,
	workspace_id VARCHAR(40) NOT NULL,
	branch_id VARCHAR(40) NOT NULL,
	is_head NUMERIC(1) NOT NULL,
	version_deleted NUMERIC(1) NOT NULL,
	version_editable NUMERIC(1) NOT NULL,
	pred_version INT NULL,
	checkin_date TIMESTAMP NULL,
	PRIMARY KEY(bridalshow_template_id, asset_version)
);

CREATE TABLE BBB_BridalShow_State (
	bridalshow_template_id 	varchar2(254)	NOT NULL,
	state_id 		varchar2(254)	NOT NULL,
	asset_version INT NOT NULL,
	PRIMARY KEY(bridalshow_template_id, state_id, asset_version)
);

 

CREATE TABLE BBB_BridalShow_Site (
	bridalshow_template_id 	varchar2(254)	NOT NULL,
	site_id 		varchar2(254)	NOT NULL,
	asset_version INT NOT NULL,
	PRIMARY KEY(bridalshow_template_id, site_id, asset_version)
);

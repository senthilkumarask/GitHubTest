drop table BBB_Static_Template;
drop table BBB_Static_Site_Page;
drop table BBB_Page_Template;

CREATE TABLE BBB_Static_Template (
	static_template_id 	varchar2(40)	NOT NULL,
	page_name 			INTEGER	NULL,
	page_title 			varchar2(254)	NULL,
	page_header_copy 	VARCHAR2(1000)	NULL,
	page_copy 			CLOB	NULL,
	asset_version       INT NOT NULL,
	workspace_id        VARCHAR(40) NOT NULL,
	branch_id           VARCHAR(40) NOT NULL,
	is_head				NUMERIC(1) NOT NULL,
	version_deleted		NUMERIC(1) NOT NULL,
	version_editable	NUMERIC(1) NOT NULL,
	pred_version		INT NULL,
	checkin_date		TIMESTAMP NULL,
	PRIMARY KEY(static_template_id,asset_version)
);

CREATE TABLE BBB_Static_Site (
	static_template_id 	varchar2(40)	NOT NULL,
	site_id 			varchar2(40)	NOT NULL,
	asset_version       INT NOT NULL,
	PRIMARY KEY(static_template_id, site_id,asset_version)
);


DROP Index BBB_Static_Template_idx;

CREATE INDEX BBB_Static_Template_idx ON BBB_Static_Template (workspace_id, checkin_date);



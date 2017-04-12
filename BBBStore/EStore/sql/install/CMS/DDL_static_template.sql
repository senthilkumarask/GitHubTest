drop table BBB_Static_Site_Page;
drop table BBB_Page_Template;
drop table BBB_Static_Template;

CREATE TABLE BBB_Static_Template (
	static_template_id 	varchar2(40)	NOT NULL,
	page_name 			INTEGER	NULL,
	page_title 			varchar2(254)	NULL,
	page_header_copy 	varchar2(1000)	NULL,
	page_copy 			CLOB	NULL,
	PRIMARY KEY(static_template_id)
);

CREATE TABLE BBB_Static_Site (
	static_template_id 	varchar2(40)	NOT NULL REFERENCES BBB_Static_Template(static_template_id),
	site_id 			varchar2(40)	NOT NULL REFERENCES site_configuration(id),
	PRIMARY KEY(static_template_id, site_id)
);


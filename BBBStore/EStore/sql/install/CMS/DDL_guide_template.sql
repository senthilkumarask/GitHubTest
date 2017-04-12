 

CREATE TABLE BBB_GUIDES (
	guides_id 			varchar2(40)	NOT NULL,
	content_type 		INTEGER	NULL,
	guides_category 	INTEGER	NULL,
	title 				varchar2(254)	NULL,
	image_url 			varchar2(254)	NULL,
	image_alt_text 		varchar2(254)	NULL,
	short_description 	varchar2(2000)	NULL,
	PRIMARY KEY(guides_id)
);

CREATE TABLE GUIDES_LONG_DESC (
	guides_id 			varchar2(40)	NOT NULL REFERENCES BBB_GUIDES(guides_id),
	long_description 	CLOB	NULL,
	PRIMARY KEY(guides_id)
);

CREATE TABLE BBB_GUIDES_SITE (
	guides_id 		varchar2(40)	NOT NULL REFERENCES BBB_GUIDES(guides_id),
	site_id 		varchar2(40)	NOT NULL REFERENCES site_configuration(id),
	PRIMARY KEY(guides_id, site_id)
);


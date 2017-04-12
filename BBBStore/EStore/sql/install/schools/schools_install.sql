-- drop table BBB_SCHOOLS;

CREATE TABLE BBB_SCHOOLS (
	school_id 		varchar2(40)	NOT NULL,
	SCHOOL_NAME 		varchar2(254)	NULL,
	SMALL_LOGO_URL 		varchar2(254)	NULL,
	LARGE_LOGO_URL 		varchar2(254)	NULL,
	SMALL_WELCOME_MSG 	varchar2(254)	NULL,
	LARGE_WELCOME_MSG 	varchar2(1000)	NULL,
	PROMOTION_ID 		varchar2(254)	NULL,
	ADDR_LINE1 		varchar2(254)	NULL,
	ADDR_LINE2 		varchar2(254)	NULL,
	CITY 			varchar2(254)	NULL,
	STATE 			varchar2(40)	NULL REFERENCES BBB_STATES(STATE_ID),
	ZIP 			varchar2(40)	NULL,
	CREATE_DATE 		date	NULL,
	LAST_MODIFIED_DATE 	date	NULL,
	IMAGE_URL 		varchar2(254)	NULL,
	PREF_STORE_ID 		varchar2(40)	NULL,
	PDF_URL 		varchar2(254)	NULL,
	COLLEGE_TAG 		varchar2(254)	NULL,
	COLLEGE_LOGO 		varchar2(254)	NULL,
	PRIMARY KEY(school_id)
);


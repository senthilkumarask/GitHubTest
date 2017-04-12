CREATE TABLE BBB_CHAT_ATTRIBUTES (
	asset_version INTEGER NOT NULL,
	id 			varchar2(254)	NOT NULL,
	chat_URL 		varchar2(254)	NULL,
	ONOFF_FLAG 		number(1)	NULL,
	WEEKDAY_OPENTIME 	DATE	NULL,
	WEEKDAY_CLOSETIME 	DATE	NULL,
	WEEKEND_OPENTIME 	DATE	NULL,
	WEEKEND_CLOSETIME 	DATE	NULL,
	PRIMARY KEY(id,asset_version)
);
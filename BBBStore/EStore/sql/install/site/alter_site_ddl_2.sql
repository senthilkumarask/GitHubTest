-- drop table BBB_SITE_TIMEZONE;
-- drop table BBB_TIME_ZONE_LIST;

CREATE TABLE BBB_SITE_TIMEZONE (
	time_zone_id 		varchar2(254)	NOT NULL,
	time_zone 		varchar2(254)	NULL,
	PRIMARY KEY(time_zone_id)
);


CREATE TABLE BBB_TIME_ZONE_LIST (
	id 			varchar2(254)	NOT NULL REFERENCES site_configuration(id),
	time_zone_id 		varchar2(254)	NOT NULL REFERENCES BBB_SITE_TIMEZONE(time_zone_id),
	PRIMARY KEY(id, time_zone_id)
);
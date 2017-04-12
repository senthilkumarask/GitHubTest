CREATE TABLE BBB_DPS_USER (
	user_id 		varchar2(254)	NOT NULL REFERENCES dps_user(id),
	phone_number 		varchar2(254)	NULL,
	mobile_number 		varchar2(254)	NULL,
	PRIMARY KEY(user_id)
);


CREATE TABLE BBB_USER_SITE_ASSOC (
	user_site_id 		varchar2(254)	NOT NULL,
	site_id 		varchar2(254)	NULL,
	store_id 		varchar2(254)	NULL,
	PRIMARY KEY(user_site_id)
);

CREATE TABLE BBB_USER_SITE_ITEMS (
	user_id 		varchar2(254)	NOT NULL REFERENCES dps_user(id),
	site_id 		varchar2(254)	NOT NULL,
	user_site_id 		varchar2(254)	NULL REFERENCES BBB_USER_SITE_ASSOC(user_site_id),
	PRIMARY KEY(user_id, site_id)
);

CREATE INDEX BBB_USER_SITE_ITEMS_user_idx ON BBB_USER_SITE_ITEMS(user_id, site_id);

CREATE TABLE BBB_PROFILE_REGISTRY_MAPPING (
	user_id 		varchar2(254)	NOT NULL REFERENCES dps_user(id),
	registry_id 		varchar2(254)	NOT NULL REFERENCES BBB_REGISTRY(registry_id),
	PRIMARY KEY(user_id, registry_id)
);

CREATE INDEX BBB_PROFILE_REGISTRY_MAPPING_user_idx ON BBB_PROFILE_REGISTRY_MAPPING(user_id, registry_id);
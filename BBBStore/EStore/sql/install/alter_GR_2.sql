drop table bbb_profile_registry_mapping;
drop table bbb_registry;
CREATE TABLE bbb_registry (
	registry_id 		varchar2(254)	NOT NULL,
	site_id 		varchar2(254)	NULL,
	event_type 		varchar2(254)	NULL,
	event_date 		DATE	NULL,
	registry_owner 		varchar2(254)	NOT NULL REFERENCES dps_user(id),
	PRIMARY KEY(registry_id)
);
CREATE TABLE bbb_profile_registry_mapping (
	user_id 		varchar2(254)	NOT NULL REFERENCES dps_user(id),
	registry_id 		varchar2(254)	NOT NULL REFERENCES BBB_REGISTRY(registry_id),
	PRIMARY KEY(user_id, registry_id)
);

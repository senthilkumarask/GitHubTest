-- drop table BBB_REGISTRY_TYPE;

-- drop table BBB_REGISTRY;

CREATE TABLE BBB_REGISTRY (
	registry_id 		varchar2(254)	NOT NULL,
	site_id 		varchar2(254)	NULL,
	event_date 		DATE	NULL,
	registry_owner 		varchar2(254)	NOT NULL REFERENCES dps_user(id),
	PRIMARY KEY(registry_id)
);

CREATE TABLE BBB_REGISTRY_TYPE (
	registry_type_id 	varchar2(254)	NOT NULL,
	registry_type_name 	varchar2(254)	NOT NULL,
	registry_type_desc 	varchar2(254)	NOT NULL,
	registry_type_index 	varchar2(254)	NOT NULL,
	PRIMARY KEY(registry_type_id)
);






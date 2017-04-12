SET ECHO ON;
SET DEFINE OFF;

alter table BBB_CORE.tbs_ship_info add (
	shipping_info_id varchar2(40) not null,
	constraint shipping_info_id_p primary key (shipping_info_id)
);

alter table BBB_CORE.bbb_hrd_ship_group add (
	shipping_info_id varchar2(40) null
);

CREATE TABLE BBB_CORE.TBS_DPS_USER (
	user_id 		varchar2(254)	NOT NULL REFERENCES BBB_CORE.dps_user(id),
	is_terms_condition 	number(1)	NULL,
	CHECK (is_terms_condition IN (0, 1)),
	PRIMARY KEY(user_id)
);
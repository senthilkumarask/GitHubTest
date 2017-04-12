SET ECHO ON;
SET DEFINE OFF;

alter table BBB_CORE_PRV.tbs_ship_info add (
	shipping_info_id varchar2(40) not null,
	constraint shipping_info_id_p primary key (shipping_info_id)
);

alter table BBB_CORE_PRV.bbb_hrd_ship_group add (
	shipping_info_id varchar2(40) null
);

CREATE TABLE BBB_CORE_PRV.TBS_DPS_USER (
	user_id 		varchar2(254)	NOT NULL REFERENCES BBB_CORE_PRV.dps_user(id),
	is_terms_condition 	number(1)	NULL,
	CHECK (is_terms_condition IN (0, 1)),
	PRIMARY KEY(user_id)
);

ALTER TABLE BBB_CORE_PRV.BBB_BCC_CATEGORIES ADD ZOOM_VALUE varchar2(2);
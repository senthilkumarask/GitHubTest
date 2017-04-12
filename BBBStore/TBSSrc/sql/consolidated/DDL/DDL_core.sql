SET ECHO ON;
SET DEFINE OFF;

create table BBB_CORE.tbs_item_info_details (
	tbs_item_id varchar2(50) not null,
	TBS_CONFIG_ID varchar2(15) null,
	tbs_product_desc varchar2(2000) null,
	tbs_product_imag varchar2(100) null,
	tbs_error_code varchar2(50) null,
	tbs_error_name varchar2(50) null,
	tbs_retail_price number(12,2) null,
	tbs_cost number(12,2) null,
	tbs_override_price number(12,2) null,
	tbs_priceoveride_ind number(1) null,
	tbs_override_reason varchar2(5) null,
	tbs_override_competitor varchar2(50) null,
	tbs_override_quantity number(10) null,
	constraint tbs_item_info_p primary key (tbs_item_id)
);

create table BBB_CORE.tbs_item (
   commerce_item_id varchar2(40) not null,
   tbs_item_info_id varchar2(50) null,
   constraint tbs_item_f foreign key (commerce_item_id) references BBB_CORE.dcspp_item(commerce_item_id),
);

alter table BBB_CORE.bbb_order add (
	is_tbs_order number(1) null,
	tbs_associate_id varchar2(100) null,
	tbs_approver_id varchar2(100) null
);

create table BBB_CORE.tbs_ship_info (
	shipping_info_id varchar2(40) not null,
	is_shipprice_override number(1) null,
	shipprice_value number(12,2) null,
	shipprice_reason varchar2(5) null,
	is_surcharge_override number(1) null,
	surcharge_value number(12,2) null,
	surcharge_reason varchar2(5) null,
	is_tax_override number(1) null,
	tax_override_type number(1) null,
	tax_value number(12,2) null,
	tax_reason varchar2(5) null,
	tax_exempt_id varchar2(40) null,
	constraint shipping_info_id_p primary key (shipping_info_id)
);



--Added for PayAtRegister payment group
create table BBB_CORE.tbs_payatregister (
	payment_group_id varchar2(50) not null,
	payer_status varchar2(50) null,
	constraint tbs_payatregister_f foreign key (payment_group_id) references BBB_CORE.dcspp_pay_group(payment_group_id)
);

--Added for PayAtRegister payment group status
create table BBB_CORE.tbs_payatregister_status (
	status_id varchar2(50) not null,
	status_code varchar2(50) null,
	status_message varchar2(100) null,
	constraint tbs_payatregister_status_f foreign key (status_id) references BBB_CORE.dcspp_pay_status(status_id)
);

CREATE TABLE BBB_CORE.item_excluded (
	sku_id 			varchar2(254)	NOT NULL,
	reason_cd 		INTEGER	NOT NULL,
	site_flag 		varchar2(254)	NOT NULL,
	start_dt 		date	NOT NULL,
	end_dt 			date	NOT NULL,
	last_mod_dt 		date	NOT NULL,
	item_comment 		varchar2(254) NULL,
	constraint item_excld_p primary key (sku_id,reason_cd)
);

create table BBB_CORE.bbb_coupon_info (
  coupon_id varchar2(40) not null,
  store_only number(1) null,
  eligible_stores varchar2(1024) null
);

alter table BBB_CORE.bbb_item add (
	shipTime varchar2(50) null
);

CREATE TABLE BBB_CORE.TBS_GS_ORDER_INFO (
	GSORDER_ID 		VARCHAR2(254)	NOT NULL,
	USER_TOKEN 		VARCHAR2(254)	NULL,
	STORE_ID 		INTEGER			NULL,
	TERMINAL_ID 	INTEGER			NULL,
	RE_LAST_NM 		VARCHAR2(254)	NULL,
	RE_FIRST_NM 	VARCHAR2(254)	NULL,
	RE_EMAIL 		VARCHAR2(254)	NULL,
	RE_PHONE 		VARCHAR2(254)	NULL,
	SKUS 			VARCHAR2(254)	NULL,
	QUANTITIES 		VARCHAR2(254)	NULL,
	REGISTRY_ID 	VARCHAR2(254)	NULL,
	atg_order_id 	VARCHAR2(254) NULL,
	constraint tbs_gs_order_p PRIMARY KEY(GSORDER_ID)
);

alter table BBB_CORE.bbb_order add tbs_store_no varchar2(20) null;

CREATE TABLE BBB_CORE.TBS_STORE_IP_RANGE (
	STORE_ID	varchar2(254)	NOT NULL,
	IP_RANGE	varchar2(254) NULL,
	CREATE_DATE		DATE NULL,
	LAST_UPDATED_DATE		DATE NULL,
	constraint STORE_ID_f1 foreign key (STORE_ID) references BBB_CORE.BBB_STORE(STORE_ID)
);

GRANT SELECT ON  BBB_CORE.ITEM_EXCLUDED TO BBB_CORE_PRV;   
GRANT SELECT ON  BBB_CORE.ITEM_EXCLUDED TO BBB_SWITCH_A;
GRANT SELECT ON  BBB_CORE.ITEM_EXCLUDED TO BBB_SWITCH_B;
GRANT INSERT, SELECT, UPDATE, DELETE on bbb_core.item_excluded to bbb_pim_stg; 
GRANT INSERT, SELECT, UPDATE ON BBB_CORE.ITEM_EXCLUDED TO ECOMWEB;

alter table BBB_CORE.bbb_tracking_info add (
	tracking_url varchar2(500) null,
	shipment_qty number(10) null
);

create table BBB_CORE.bbb_item_tracking (
	commerce_item_id varchar2(50) not null,
	tracking_numbers varchar2(200) null,
	sequence_num NUMBER NOT NULL,
	constraint bbb_item_tracking_f foreign key (commerce_item_id) references BBB_CORE.dcspp_item(commerce_item_id)
);

alter table BBB_CORE.bbb_hrd_ship_group add (
	shipping_info_id varchar2(40) null
);

CREATE TABLE BBB_CORE.TBS_DPS_USER (
	user_id 		varchar2(254)	NOT NULL REFERENCES BBB_CORE.dps_user(id),
	is_terms_condition 	number(1)	NULL,
	CHECK (is_terms_condition IN (0, 1)),
	constraint tbs_duser_p PRIMARY KEY(user_id)
);

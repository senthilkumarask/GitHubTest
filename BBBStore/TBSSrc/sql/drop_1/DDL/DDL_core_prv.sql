SET ECHO ON;
SET DEFINE OFF;

create table BBB_CORE_PRV.tbs_item_info_details (
	tbs_item_id varchar2(50) not null,
	TBS_CONFIG_ID varchar2(15) not null,
	tbs_product_desc varchar2(2000) not null,
	tbs_product_imag varchar2(100) null,
	tbs_error_code varchar2(50) null,
	tbs_error_name varchar2(50) null,
	tbs_retail_price number(12,2) null,
	tbs_cost number(12,2) not null,
	tbs_override_price number(12,2) null,
	tbs_priceoveride_ind number(1) null,
	tbs_override_reason varchar2(5) null,
	tbs_override_competitor varchar2(50) null,
	constraint tbs_item_info_p primary key (tbs_item_id)
);

create table BBB_CORE_PRV.tbs_item (
   commerce_item_id varchar2(40) not null,
   tbs_item_info_id varchar2(50) null,
   constraint tbs_item_f foreign key (commerce_item_id) references BBB_CORE_PRV.dcspp_item(commerce_item_id),
   constraint tbs_item_f2 foreign key (tbs_item_info_id) references BBB_CORE_PRV.tbs_item_info_details(tbs_item_id)
);

alter table BBB_CORE_PRV.bbb_order add (
	is_tbs_order number(1) null,
	tbs_associate_id varchar2(20) null,
	tbs_approver_id varchar2(20) null
);

create table BBB_CORE_PRV.tbs_ship_info (
	shipping_group_id varchar2(40) not null,
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
    constraint tbs_shipitem_f foreign key (shipping_group_id) references BBB_CORE_PRV.dcspp_ship_group(shipping_group_id)
);

create table BBB_CORE_PRV.tbs_payatregister (
	payment_group_id varchar2(50) not null,
	payer_status varchar2(50) null,
	constraint tbs_payatregister_f foreign key (payment_group_id) references BBB_CORE_PRV.dcspp_pay_group(payment_group_id)
);

create table BBB_CORE_PRV.tbs_payatregister_status (
	status_id varchar2(50) not null,
	status_code varchar2(50) null,
	status_message varchar2(100) null,
	constraint tbs_payatregister_status_f foreign key (status_id) references BBB_CORE_PRV.dcspp_pay_status(status_id)
);

CREATE TABLE BBB_CORE_PRV.item_excluded (
	sku_id 			varchar2(254)	NOT NULL,
	reason_cd 		INTEGER	NOT NULL,
	site_flag 		varchar2(254)	NOT NULL,
	start_dt 		date	NOT NULL,
	end_dt 			date	NOT NULL,
	last_mod_dt 		date	NOT NULL,
	item_comment 		varchar2(254)	NOT NULL,
	constraint item_excld_p primary key (sku_id,reason_cd)
);

create table BBB_CORE_PRV.bbb_coupon_info (
  coupon_id varchar2(40) not null,
  store_only number(1) null,
  eligible_stores varchar2(1024) null
);

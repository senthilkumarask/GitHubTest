create table bbb_address (
	address_id varchar2(40) not null,
	prefix varchar2(40),
	first_name varchar2(40),
	middle_name varchar2(40),
	last_name varchar2(40),
	suffix varchar2(40),
	job_title varchar2(40),
	company_name varchar2(40),
	address_1 varchar2(50),
	address_2 varchar2(40),
	address_3 varchar2(40),
	city varchar2(40),
	county varchar2(40),
	state varchar2(40),
	postal_code varchar2(40),
	country varchar2(40),
    mobile_number varchar2(15),
    phone_number varchar2(15),
    email varchar2(100),
    constraint bbb_address_p primary key (address_id)
);
create table bbb_order (
   order_id varchar2(40) not null,
   user_ip varchar2(15) null,
   billing_address_id varchar2(40) null,
   school_id varchar2(40) null,
   substatus varchar2(40) null,
   substatus_detail varchar2(254) null,
   online_order_number varchar2(40) null,
   bopus_order_number varchar2(40) null,
   constraint bbb_order_p primary key (order_id),
   constraint bbb_order_f foreign key (order_id) references dcspp_order(order_id)
);

create table bbb_store_ship_grp (
   shipping_group_id varchar2(40),
   store_id varchar2(40),
   constraint bbb_store_ship_grp_p primary key (shipping_group_id),
   constraint bbb_store_ship_grp_f foreign key (shipping_group_id) references dcspp_ship_group(shipping_group_id)
);

create table bbb_hrd_ship_group (
   shipping_group_id varchar2(40),
   registry_id varchar2(40),
   registry_info varchar2(255),
   estd_delivery_date timestamp(6),
   address_type varchar2(255),
   gift_wrap_ind number(1),
   validation_code varchar2(255),
   constraint bbb_hrd_ship_group_p primary key (shipping_group_id),
   constraint bbb_hrd_ship_group_f foreign key (shipping_group_id) references dcspp_ship_group(shipping_group_id)
);
  
create table bbb_ship_price (
   amount_info_id varchar2(40),
   final_shipping number(19,7),
   surcharge_amount number(19,7),
   final_surcharge_amount number(19,7),
   gift_wrap_amount number(19,7),
   constraint bbb_ship_price_p primary key (amount_info_id),
   constraint bbb_ship_price_f foreign key (amount_info_id) references dcspp_amount_info(amount_info_id)
); 
  
create table bbb_item (
   commerce_item_id varchar2(40),
   vdc_ind number(1),
   free_shipping_method varchar2(100),
   registry_id varchar2(40),
   registry_info varchar2(100),
   last_modified_date timestamp(6),
   store_id varchar2(40),
   bts_ind number(1),
   sku_surcharge number(7,2),
   constraint bbb_item_p primary key (commerce_item_id),
   constraint bbb_item_f foreign key (commerce_item_id) references dcspp_item(commerce_item_id)
);
  
create table bbb_levolor_item (
   commerce_item_id varchar2(40),
   levolor_attr1 varchar2(255),
   levolor_attr2 varchar2(100),
   levolor_attr3 varchar2(50),
   levolor_attr4 varchar2(50),
   constraint bbb_levolor_item_p primary key (commerce_item_id),
   constraint bbb_levolor_item_f foreign key (commerce_item_id) references dcspp_item(commerce_item_id)
);

create table bbb_item_sg_tax (
   item_price_info_id varchar2(40),
   sg_id varchar2(40),
   tax_price_id varchar2(40),
   constraint bbb_item_sg_tax_p primary key (item_price_info_id,sg_id),
   constraint bbb_item_sg_tax_f2 foreign key (sg_id) references dcspp_ship_group(shipping_group_id),
   constraint bbb_item_sg_tax_f1 foreign key (tax_price_id) references dcspp_tax_price(amount_info_id)
);

create table bbb_order_price (
   amount_info_id varchar2(40),
   store_subtotal number(19,7),
   order_subtotal number(19,7),
   constraint bbb_order_price_p primary key (amount_info_id),
   constraint bbb_order_price_f foreign key (amount_info_id) references dcspp_amount_info(amount_info_id)
);

create table bbb_cc_status (
   status_id varchar2(40) not null,
   auth_code varchar2(100),
   auth_response_record varchar2(100),
   constraint bbb_cc_status_p primary key (status_id),
   constraint bbb_cc_status_f foreign key (status_id) references dcspp_pay_status(status_id)
);
  
create table bbb_gift_card_pay_grp (
   payment_group_id varchar2(40),
   card_number varchar2(100),
   pin varchar2(40),
   balance number(19,7),
   constraint gift_card_p primary key (payment_group_id),
   constraint gift_card_f foreign key (payment_group_id) references dcspp_pay_group(payment_group_id)
);

create table bbb_credit_card (
   payment_group_id varchar2(40),
   constraint bbb_credit_card_p primary key (payment_group_id)
);
 
create table bbb_gift_card_status (
   status_id varchar2(40) not null,
   auth_code varchar2(40),
   trace_number varchar2(40),
   auth_response_code varchar2(40),
   constraint gift_card_status_p primary key (status_id),
   constraint gift_card_status_f foreign key (status_id) references dcspp_pay_status(status_id)
);

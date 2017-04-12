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
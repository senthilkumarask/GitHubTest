Create table bbb_tracking_info(
tracking_info_id varchar2(40),
carrier_code varchar2(40),
tracking_id varchar2(40),
constraint bbb_tracking_info_p primary key (tracking_info_id)
)

Create table bbb_hrd_ship_tracking(
shipping_group_id varchar2(40),
tracking_info_id varchar2(40),
constraint bbb_hrd_ship_tracking_f1 foreign key (shipping_group_id) references dcspp_ship_group(shipping_group_id),
constraint bbb_hrd_ship_tracking_f2 foreign key (tracking_info_id) references bbb_tracking_info(tracking_info_id)
)

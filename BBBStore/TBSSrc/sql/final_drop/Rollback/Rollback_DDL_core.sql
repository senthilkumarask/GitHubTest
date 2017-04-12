SET ECHO ON;
SET DEFINE OFF;

drop table BBB_CORE.tbs_item_info_details;

drop table BBB_CORE.tbs_item;

alter table BBB_CORE.bbb_order drop column is_tbs_order;
alter table BBB_CORE.bbb_order drop column tbs_associate_id;
alter table BBB_CORE.bbb_order drop column tbs_approver_id;

drop table BBB_CORE.tbs_ship_info;

drop table BBB_CORE.tbs_payatregister;

drop table BBB_CORE.tbs_payatregister_status;

drop TABLE BBB_CORE.item_excluded;

drop table BBB_CORE.bbb_coupon_info;

alter table BBB_CORE.bbb_item drop column shipTime;

drop TABLE BBB_CORE.TBS_GS_ORDER_INFO;

alter table BBB_CORE.bbb_order drop column tbs_store_no;

drop TABLE BBB_CORE.TBS_STORE_IP_RANGE;

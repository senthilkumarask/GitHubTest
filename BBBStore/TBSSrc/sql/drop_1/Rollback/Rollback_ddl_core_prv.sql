SET ECHO ON;
SET DEFINE OFF;

drop table BBB_CORE_PRV.tbs_item;
drop table BBB_CORE_PRV.tbs_item_info_details;
drop table BBB_CORE_PRV.tbs_ship_info;
drop table BBB_CORE_PRV.tbs_payatregister;
drop table BBB_CORE_PRV.tbs_payatregister_status;
drop table BBB_CORE_PRV.item_excluded;
drop table BBB_CORE_PRV.bbb_coupon_info;
alter table BBB_CORE_PRV.bbb_order drop column is_tbs_order;
alter table BBB_CORE_PRV.bbb_order drop column tbs_associate_id;
alter table BBB_CORE_PRV.bbb_order drop column tbs_approver_id;
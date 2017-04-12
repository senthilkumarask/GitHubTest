SET ECHO ON;
SET DEFINE OFF;

DROP table BBB_CORE.tbs_item_info_details;

DROP table BBB_CORE.tbs_item;

alter table BBB_CORE.bbb_order DROP COLUMN is_tbs_order;
alter table BBB_CORE.bbb_order DROP COLUMN tbs_associate_id;
alter table BBB_CORE.bbb_order DROP COLUMN tbs_approver_id;

DROP table BBB_CORE.tbs_ship_info;

DROP table BBB_CORE.tbs_payatregister;

DROP table BBB_CORE.tbs_payatregister_status;

DROP TABLE BBB_CORE.item_excluded;

DROP table BBB_CORE.bbb_coupon_info;

alter table BBB_CORE.bbb_item DROP COLUMN shipTime;

DROP TABLE BBB_CORE.TBS_GS_ORDER_INFO;

alter table BBB_CORE.bbb_order DROP COLUMN tbs_store_no;

DROP TABLE BBB_CORE.TBS_STORE_IP_RANGE;

alter table BBB_CORE.bbb_tracking_info DROP COLUMN tracking_url;
alter table BBB_CORE.bbb_tracking_info DROP COLUMN shipment_qty;

DROP table BBB_CORE.bbb_item_tracking;

alter table BBB_CORE.bbb_hrd_ship_group DROP COLUMN shipping_info_id;

DROP TABLE BBB_CORE.TBS_DPS_USER;

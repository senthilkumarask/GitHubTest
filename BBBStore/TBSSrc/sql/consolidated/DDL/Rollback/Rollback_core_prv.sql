SET ECHO ON;
SET DEFINE OFF;

DROP table BBB_CORE_PRV.tbs_item_info_details;

DROP table BBB_CORE_PRV.tbs_item;

alter table BBB_CORE_PRV.bbb_order DROP COLUMN is_tbs_order;
alter table BBB_CORE_PRV.bbb_order DROP COLUMN tbs_associate_id;
alter table BBB_CORE_PRV.bbb_order DROP COLUMN tbs_approver_id;

DROP table BBB_CORE_PRV.tbs_ship_info;

DROP table BBB_CORE_PRV.tbs_payatregister;

DROP table BBB_CORE_PRV.tbs_payatregister_status;

DROP table BBB_CORE_PRV.item_excluded;

DROP table BBB_CORE_PRV.bbb_coupon_info;

alter table BBB_CORE_PRV.bbb_item DROP COLUMN shipTime;

alter table BBB_CORE_PRV.BBB_SITE_ATTRIBUTES DROP COLUMN item_override_threshold;
alter table BBB_CORE_PRV.BBB_SITE_ATTRIBUTES DROP COLUMN shipping_override_threshold;
alter table BBB_CORE_PRV.BBB_SITE_ATTRIBUTES DROP COLUMN surcharge_override_threshold;
alter table BBB_CORE_PRV.BBB_SITE_ATTRIBUTES DROP COLUMN tax_override_threshold;

alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE DROP COLUMN logo_image;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_announcement_fromweb;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE DROP COLUMN announcement;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_popular_cat_fromweb;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_featured_cat_fromweb;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_promoregpanel_fromweb;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_promotierlayout1_fromweb;

DROP table BBB_CORE_PRV.BBB_HOMEPAGE_FEAT_CATEGORY;

DROP table BBB_CORE_PRV.TBS_GS_ORDER_INFO;

alter table BBB_CORE_PRV.bbb_order DROP COLUMN tbs_store_no;

alter table BBB_CORE_PRV.BBB_PROMOBOX DROP COLUMN promo_box_title;

DROP table BBB_CORE_PRV.TBS_STORE_IP_RANGE;

alter table BBB_CORE_PRV.bbb_tracking_info DROP COLUMN tracking_url;
alter table BBB_CORE_PRV.bbb_tracking_info DROP COLUMN shipment_qty;

DROP table BBB_CORE_PRV.bbb_item_tracking;

alter table BBB_CORE_PRV.bbb_hrd_ship_group DROP COLUMN shipping_info_id;

DROP table BBB_CORE_PRV.TBS_DPS_USER;

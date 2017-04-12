SET ECHO ON;
SET DEFINE OFF;

drop table BBB_CORE_PRV.tbs_item_info_details;

drop table BBB_CORE_PRV.tbs_item;

alter table BBB_CORE_PRV.bbb_order drop column is_tbs_order;
alter table BBB_CORE_PRV.bbb_order drop column tbs_associate_id;
alter table BBB_CORE_PRV.bbb_order drop column tbs_approver_id;

drop table BBB_CORE_PRV.tbs_ship_info;

drop table BBB_CORE_PRV.tbs_payatregister;

drop table BBB_CORE_PRV.tbs_payatregister_status;

DROP TABLE BBB_CORE_PRV.item_excluded;

drop table BBB_CORE_PRV.bbb_coupon_info;

alter table BBB_CORE_PRV.bbb_item drop column shipTime;

alter table BBB_CORE_PRV.BBB_SITE_ATTRIBUTES drop column item_override_threshold; 
alter table BBB_CORE_PRV.BBB_SITE_ATTRIBUTES drop column surcharge_override_threshold;

alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE drop column logo_image; 
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE drop column use_announcement_fromweb;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE drop column announcement;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE drop column use_popular_cat_fromweb;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE drop column use_featured_cat_fromweb;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE drop column use_promoregpanel_fromweb;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE drop column use_promotierlayout1_fromweb;

drop table BBB_CORE_PRV.BBB_HOMEPAGE_FEAT_CATEGORY;

drop TABLE BBB_CORE_PRV.TBS_GS_ORDER_INFO;

alter table BBB_CORE_PRV.bbb_order drop column tbs_store_no;
alter table BBB_CORE_PRV.BBB_PROMOBOX drop column promo_box_title;

drop TABLE BBB_CORE_PRV.TBS_STORE_IP_RANGE;

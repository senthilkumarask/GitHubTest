SET ECHO ON;
SET DEFINE OFF;

alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE drop column logo_image; 
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE drop column use_announcement_fromweb;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE drop column announcement;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE drop column use_popular_cat_fromweb;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE drop column use_featured_cat_fromweb;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE drop column use_promoregpanel_fromweb;
alter table BBB_CORE_PRV.BBB_HOMEPAGE_TEMPLATE drop column use_promotierlayout1_fromweb;

drop table BBB_CORE_PRV.BBB_HOMEPAGE_FEAT_CATEGORY;

drop table BBB_CORE_PRV.TBS_GS_ORDER_INFO;

alter table BBB_CORE_PRV.bbb_order drop column tbs_store_no;

commit;
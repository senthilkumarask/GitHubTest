SET ECHO ON;
SET DEFINE OFF;

alter table BBB_SWITCH_B.BBB_SITE_ATTRIBUTES DROP COLUMN item_override_threshold;
alter table BBB_SWITCH_B.BBB_SITE_ATTRIBUTES DROP COLUMN surcharge_override_threshold;
alter table BBB_SWITCH_B.BBB_SITE_ATTRIBUTES DROP COLUMN shipping_override_threshold;
alter table BBB_SWITCH_B.BBB_SITE_ATTRIBUTES DROP COLUMN tax_override_threshold;

alter table BBB_SWITCH_B.BBB_HOMEPAGE_TEMPLATE DROP COLUMN logo_image;
alter table BBB_SWITCH_B.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_announcement_fromweb;
alter table BBB_SWITCH_B.BBB_HOMEPAGE_TEMPLATE DROP COLUMN announcement;
alter table BBB_SWITCH_B.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_popular_cat_fromweb;
alter table BBB_SWITCH_B.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_featured_cat_fromweb;
alter table BBB_SWITCH_B.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_promoregpanel_fromweb;
alter table BBB_SWITCH_B.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_promotierlayout1_fromweb;

DROP table BBB_SWITCH_B.BBB_HOMEPAGE_FEAT_CATEGORY;

alter table BBB_SWITCH_B.BBB_PROMOBOX DROP COLUMN promo_box_title;

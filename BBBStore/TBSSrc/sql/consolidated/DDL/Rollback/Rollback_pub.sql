SET ECHO ON;
SET DEFINE OFF;

DROP table BBB_PUB.bbb_coupon_info;

alter table BBB_PUB.BBB_SITE_ATTRIBUTES DROP COLUMN item_override_threshold;
alter table BBB_PUB.BBB_SITE_ATTRIBUTES DROP COLUMN surcharge_override_threshold;
alter table BBB_PUB.BBB_SITE_ATTRIBUTES DROP COLUMN shipping_override_threshold;
alter table BBB_PUB.BBB_SITE_ATTRIBUTES DROP COLUMN tax_override_threshold;

alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE DROP COLUMN logo_image;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_announcement_fromweb;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE DROP COLUMN announcement;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_popular_cat_fromweb;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_featured_cat_fromweb;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_promoregpanel_fromweb;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE DROP COLUMN use_promotierlayout1_fromweb;

DROP table BBB_PUB.BBB_HOMEPAGE_FEAT_CATEGORY;

alter table BBB_PUB.BBB_PROMOBOX DROP COLUMN promo_box_title;

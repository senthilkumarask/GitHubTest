SET ECHO ON;
SET DEFINE OFF;

--Added for TBS Store coupon data
drop table BBB_PUB.bbb_coupon_info;

alter table BBB_PUB.BBB_SITE_ATTRIBUTES drop column item_override_threshold;
alter table BBB_PUB.BBB_SITE_ATTRIBUTES drop column surcharge_override_threshold;

alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE drop column logo_image; 
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE drop column use_announcement_fromweb;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE drop column announcement;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE drop column use_popular_cat_fromweb;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE drop column use_featured_cat_fromweb;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE drop column use_promoregpanel_fromweb;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE drop column use_promotierlayout1_fromweb;

drop table BBB_PUB.BBB_HOMEPAGE_FEAT_CATEGORY;

alter table BBB_PUB.BBB_PROMOBOX drop column promo_box_title;

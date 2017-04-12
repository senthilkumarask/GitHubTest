SET ECHO ON;
SET DEFINE OFF;

alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE drop column logo_image; 
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE drop column use_announcement_fromweb;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE drop column announcement;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE drop column use_popular_cat_fromweb;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE drop column use_featured_cat_fromweb;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE drop column use_promoregpanel_fromweb;
alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE drop column use_promotierlayout1_fromweb;

drop table BBB_PUB.BBB_HOMEPAGE_FEAT_CATEGORY;

commit;
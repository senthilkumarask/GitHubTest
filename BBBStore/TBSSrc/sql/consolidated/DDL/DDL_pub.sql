SET ECHO ON;
SET DEFINE OFF;

--Added for TBS Store coupon data
create table BBB_PUB.bbb_coupon_info (
  asset_version integer not null,
  coupon_id varchar2(40) not null,
  store_only number(1) null,
  eligible_stores varchar2(1024) null
);

alter table BBB_PUB.BBB_SITE_ATTRIBUTES add (
  item_override_threshold number(10,2) null, 
  surcharge_override_threshold number(10,2) null,
  shipping_override_threshold number(10,2) null,
  tax_override_threshold number(10,2) null
);

alter table BBB_PUB.BBB_HOMEPAGE_TEMPLATE add (
  logo_image VARCHAR2(40) NULL, 
  use_announcement_fromweb NUMBER(1) default 1,
  announcement VARCHAR2(40) NULL,
  use_popular_cat_fromweb NUMBER(1) default 1,
  use_featured_cat_fromweb NUMBER(1) default 1,
  use_promoregpanel_fromweb NUMBER(1) default 1,
  use_promotierlayout1_fromweb NUMBER(1) default 1
);

create table BBB_PUB.BBB_HOMEPAGE_FEAT_CATEGORY(
   homepage_template_id VARCHAR2(40) NOT NULL, 
   sequence_num NUMBER NOT NULL,
   cat_container_id VARCHAR2(40) NOT NULL,
   asset_version NUMBER NOT NULL,
   CONSTRAINT BBB_HP_FEAT_CAT_PK PRIMARY KEY (homepage_template_id, sequence_num, asset_version),
   CONSTRAINT BBB_HP_FEAT_CAT_FK1 FOREIGN KEY (homepage_template_id, asset_version) REFERENCES BBB_PUB.BBB_HOMEPAGE_TEMPLATE (homepage_template_id, asset_version), 
   CONSTRAINT BBB_HP_FEAT_CAT_FK2 FOREIGN KEY (cat_container_id, asset_version) REFERENCES BBB_PUB.BBB_CATEGORY_CONTAINER (cat_container_id, asset_version)
);

alter table BBB_PUB.BBB_PROMOBOX add promo_box_title VARCHAR2(150);

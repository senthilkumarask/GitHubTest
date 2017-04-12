SET ECHO ON;
SET DEFINE OFF;

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
   asset_version NUMBER NOT NULL
);

alter table BBB_PUB.BBB_PROMOBOX add promo_box_title VARCHAR2(150);


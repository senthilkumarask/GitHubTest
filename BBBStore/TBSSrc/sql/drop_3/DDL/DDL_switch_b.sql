SET ECHO ON;
SET DEFINE OFF;

alter table BBB_SWITCH_B.BBB_HOMEPAGE_TEMPLATE add (
  logo_image VARCHAR2(40) NULL, 
  use_announcement_fromweb NUMBER(1) default 1,
  announcement VARCHAR2(40) NULL,
  use_popular_cat_fromweb NUMBER(1) default 1,
  use_featured_cat_fromweb NUMBER(1) default 1,
  use_promoregpanel_fromweb NUMBER(1) default 1,
  use_promotierlayout1_fromweb NUMBER(1) default 1
);

create table BBB_SWITCH_B.BBB_HOMEPAGE_FEAT_CATEGORY(
   homepage_template_id VARCHAR2(40) NOT NULL, 
   sequence_num NUMBER NOT NULL,
   cat_container_id VARCHAR2(40) NOT NULL,
   CONSTRAINT BBB_HP_FEAT_CAT_PK PRIMARY KEY (homepage_template_id, sequence_num),
   CONSTRAINT BBB_HP_FEAT_CAT_FK1 FOREIGN KEY (homepage_template_id) REFERENCES BBB_SWITCH_B.BBB_HOMEPAGE_TEMPLATE (homepage_template_id), 
   CONSTRAINT BBB_HP_FEAT_CAT_FK2 FOREIGN KEY (cat_container_id) REFERENCES BBB_SWITCH_B.BBB_CATEGORY_CONTAINER (cat_container_id)
);

alter table BBB_SWITCH_B.BBB_PROMOBOX add promo_box_title VARCHAR2(150);


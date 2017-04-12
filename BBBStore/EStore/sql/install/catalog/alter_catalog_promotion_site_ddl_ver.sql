

alter table BBB_SITE_ATTRIBUTES
add (pack_hold_start_date DATE NULL, 
pack_hold_end_date DATE NULL, 
site_tag varchar2(20) NULL);



alter table bbb_sku drop column eComFulfillment_flag;

alter table bbb_sku add (eComFulfillment varchar2(20) NULL);
alter table bbb_sku
add (jda_class varchar2(40) NULL);

CREATE TABLE BBB_PROMO_MAPPING(PROMOTION_ID VARCHAR2(40) NOT NULL, BBB_COUPON_ID  VARCHAR2(40) NOT NULL, ASSET_VERSION  NUMBER(19) NOT NULL, PRIMARY KEY(PROMOTION_ID, BBB_COUPON_ID, ASSET_VERSION));


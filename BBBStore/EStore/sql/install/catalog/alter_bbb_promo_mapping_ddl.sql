alter table BBB_PROMO_MAPPING add (ACTIVATION_TEXT VARCHAR2(4000) NULL, ACTIVATED_TEXT VARCHAR2(4000) NULL, TANDC_TEXT VARCHAR2(4000) NULL);

alter table bbb_promo_mapping modify BBB_COUPON_ID VARCHAR(40) null;

alter table bbb_promo_mapping drop constraint BBB_PROMO_MAPPING_PK ;

alter table bbb_promo_mapping add constraint BBB_PROMO_MAPPING_PKK primary key(PROMOTION_ID);
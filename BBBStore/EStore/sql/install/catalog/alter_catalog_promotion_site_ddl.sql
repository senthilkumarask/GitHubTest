

alter table BBB_SITE_ATTRIBUTES
add (pack_hold_start_date DATE NULL, 
pack_hold_end_date DATE NULL, 
site_tag varchar2(20) NULL);



alter table bbb_sku drop column eComFulfillment_flag;

alter table bbb_sku add (eComFulfillment varchar2(20) NULL);




alter table bbb_sku
add (jda_class varchar2(40) NULL) REFERENCES BBB_CLASS(JDA_CLASS);


CREATE TABLE BBB_PROMO_MAPPING(
    PROMOTION_ID   VARCHAR2(40) NOT NULL REFERENCES DCS_PROMOTION(PROMOTION_ID),
    BBB_COUPON_ID  VARCHAR2(40) NOT NULL,
    PRIMARY KEY(PROMOTION_ID, BBB_COUPON_ID)
);

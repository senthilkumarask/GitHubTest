create table bbb_core_prv.endeca_prod_site_relation(PRODUCT_ID VARCHAR2(40), site_id varchar2(40));

create index bbb_core_prv.GBL_TMP_IDX23 on BBB_CORE_PRV.endeca_prod_site_relation (PRODUCT_ID);

-- Scope Item # 157
CREATE TABLE bbb_core_prv.endeca_export_cat_prods (CAT_ID varchar(32), prod_id varchar(32), seq_num varchar(32));

-- BBBSL-1740
CREATE TABLE bbb_core_prv.endeca_export_prices_updates (product_id varchar(40), price_range_descrip VARCHAR2(254),sku_low_price VARCHAR2(40), sku_high_price VARCHAR2(40),site_id varchar2(40));

create index bbb_core_prv.GBL_TMP_IDX5 on BBB_CORE_PRV.endeca_export_prices_updates(site_id);

create index bbb_core_prv.DCS_price_price_list_IDX on BBB_CORE_PRV.dcs_price(price_list);
-- BBBSL-1740

--BBBSL-2340

CREATE TABLE BBB_CORE_PRV.ENDECA_EXPORT_COLLECTION_SKUS(PRODUCT_ID VARCHAR2(40),CHILDPRD VARCHAR2(40),CHILDPRD_WEBOFFERED_FLAG NUMBER(1),PRODUCT_SITE_ID VARCHAR2(40),SKU_ID VARCHAR2(40),SEQUENCE_NUM VARCHAR2(20),SKU_SITE_ID VARCHAR2(20),SKU_WEBOFFERED_FLAG NUMBER(1));

CREATE INDEX BBB_CORE_PRV.GBL_TMP_IDX6 ON BBB_CORE_PRV.ENDECA_EXPORT_COLLECTION_SKUS (CHILDPRD);
CREATE INDEX BBB_CORE_PRV.GBL_TMP_IDX7 ON BBB_CORE_PRV.ENDECA_EXPORT_COLLECTION_SKUS (SKU_ID);


CREATE TABLE BBB_CORE_PRV.ENDECA_EXPORT_PROD_SKUS (PRODUCT_ID VARCHAR2(40),SKU_ID VARCHAR2(40), SITE_ID VARCHAR2(40),SEQUENCE_NUM VARCHAR2 (20), TRANSLATION_ID VARCHAR2(80), WEB_OFFERED_FLAG NUMBER(1), DISABLE_FLAG NUMBER(1));
CREATE INDEX BBB_CORE_PRV.GBL_TMP_IDX6 ON BBB_CORE_PRV.ENDECA_EXPORT_PROD_SKUS (TRANSLATION_ID);	

--BBBSL-2340

--BAND643
CREATE TABLE BBB_CORE_PRV.ENDECA_PRODUCT_ITEM_ATTR
(		
		PRODUCT_ID VARCHAR2(40 BYTE) NOT NULL,
		SKU_ATTRIBUTE_ID VARCHAR2(40 BYTE),
		SITE_ID VARCHAR2(40 BYTE) NOT NULL,
		START_DATE DATE,
		END_DATE DATE,
		MISC_INFO VARCHAR2(254 BYTE),
		ATTR_LAST_MODIFIED_DATE TIMESTAMP(6)
);

CREATE TABLE BBB_CORE_PRV.ENDECA_SKU_ITEM_ATTR
(		
		SKU_ID VARCHAR2(40 BYTE) NOT NULL,
		SKU_ATTRIBUTE_ID VARCHAR2(40 BYTE),
		SITE_ID VARCHAR2(40 BYTE) NOT NULL,
		START_DATE DATE,
		END_DATE DATE,
		MISC_INFO VARCHAR2(254 BYTE)
);
CREATE INDEX BBB_CORE_PRV.PRD_ITEM_ATTR_INDEX ON BBB_CORE_PRV.ENDECA_PRODUCT_ITEM_ATTR (PRODUCT_ID);
CREATE INDEX BBB_CORE_PRV.SKU_ITEM_ATTR_INDEX ON BBB_CORE_PRV.ENDECA_SKU_ITEM_ATTR (SKU_ID);
--BAND643


COMMIT;
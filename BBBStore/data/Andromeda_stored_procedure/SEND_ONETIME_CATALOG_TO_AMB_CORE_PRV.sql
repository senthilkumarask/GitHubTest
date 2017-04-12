-- build version: 
SET ECHO ON;
SET SERVEROUTPUT ON;
SET DEFINE OFF;

CREATE OR REPLACE PROCEDURE BBB_CORE_PRV.SEND_ONETIME_CATALOG_TO_AMB (bbuMerchId IN VARCHAR2, bbbyMerchId IN VARCHAR2, dataCenter IN VARCHAR2)
IS
-- Cursor for storing all eligible dept/sub-dept/class to be sent to Borderfree
CURSOR category_cursor IS
SELECT 
  bbuMerchId MERCHANT_ID,
  dept.jda_dept_id ID,
  dept.descrip NAME,
  '' PARENT_ID,
  dept.descrip DESCRIPTION
FROM 
  BBB_CORE_PRV.BBB_DEPT dept
WHERE
  dept.descrip is not NULL
UNION  
SELECT 
  bbbyMerchId MERCHANT_ID,
  dept.jda_dept_id ID,
  dept.descrip NAME,
  '' PARENT_ID,
  dept.descrip DESCRIPTION
FROM 
  BBB_CORE_PRV.BBB_DEPT dept
WHERE
  dept.descrip is not NULL  
UNION
SELECT 
  bbuMerchId MERCHANT_ID,
  sdept.jda_sub_dept_id ID,
  sdept.descrip NAME,
  sdept.jda_dept_id PARENT_ID,
  sdept.descrip DESCRIPTION
FROM 
  BBB_CORE_PRV.BBB_SUB_DEPT sdept
WHERE
  sdept.descrip is not NULL  
UNION  
SELECT 
  bbbyMerchId MERCHANT_ID,
  sdept.jda_sub_dept_id ID,
  sdept.descrip NAME,
  sdept.jda_dept_id PARENT_ID,
  sdept.descrip DESCRIPTION
FROM 
  BBB_CORE_PRV.BBB_SUB_DEPT sdept 
WHERE
  sdept.descrip is not NULL  
UNION
SELECT 
  bbuMerchId MERCHANT_ID,
  cls.jda_dept_id || '_' || cls.jda_sub_dept_id || '_' || cls.jda_class ID,
  cls.descrip NAME,
  cls.jda_dept_id || '_' || cls.jda_sub_dept_id PARENT_ID,
  cls.descrip DESCRIPTION
FROM 
  BBB_CORE_PRV.BBB_CLASS cls
WHERE
  cls.descrip is not NULL  
UNION  
SELECT 
  bbbyMerchId MERCHANT_ID,
  cls.jda_dept_id || '_' || cls.jda_sub_dept_id || '_' || cls.jda_class ID,
  cls.descrip NAME,
  cls.jda_dept_id || '_' || cls.jda_sub_dept_id PARENT_ID,
  cls.descrip DESCRIPTION
FROM 
  BBB_CORE_PRV.BBB_CLASS cls
WHERE
  cls.descrip is not NULL; 
  
-- Cursor for storing all eligible skus to be sent to Borderfree
CURSOR sku_cursor IS
SELECT 
  DECODE(site.id, 'BedBathUS', bbuMerchId, 'BuyBuyBaby', bbbyMerchId) MERCHANT_ID,
  sku.sku_id SKU,
  SUBSTR(REGEXP_REPLACE(UTL_I18N.UNESCAPE_REFERENCE(sku.display_name), '<[^<>]*>', ''), 1, 255) NAME,
  REGEXP_REPLACE(UTL_I18N.UNESCAPE_REFERENCE(sku.description), '<[^<>]*>', '') DESCRIPTION,
  bsku.jda_sub_dept_id || '_' || bsku.jda_class PARENT_ID,
  price.list_price DUTIABLE_PRICE,
  DECODE(site.id, 'BedBathUS', 'https://www.bedbathandbeyond.com/store', 'BuyBuyBaby', 'https://www.buybuybaby.com/store') || prd.SEO_URL URL,
  'https://s7d9.scene7.com/is/image/BedBathandBeyond/' || bsku.scene7_url IMAGE_URL,
  bsku.UPC,
  'US' COS,
  'New' PRODUCT_CONDITION,
  bsku.vendor_id MANUFACTURER,
  'en-us' DEFAULT_LOCALE,
  decode(instr(misc.coo, ','), 0, coo, null, null, 'ZZ') COO,
  decode(misc.length, 0, 0.1, misc.length) BOX_LENGTH,
  decode(misc.width, 0, 0.1, misc.width) BOX_WIDTH,
  decode(misc.height, 0, 0.1, misc.height) BOX_HEIGHT,
  NVL2(misc.length, 'inch', '') SIZE_UNITS,
  decode(misc.weight, 0, 0.1, misc.weight) NET_WEIGHT,
  NVL2(misc.weight, 'lb', '') WEIGHT_UNITS
FROM 
  BBB_CORE_PRV.DCS_SKU sku,
  BBB_CORE_PRV.BBB_SKU bsku,
  BBB_CORE_PRV.DCS_PRICE price,
  BBB_CORE_PRV.DCS_SITE site,
  BBB_CORE_PRV.DCS_PRD_CHLDSKU chld,
  BBB_CORE_PRV.BBB_PRODUCT prd,
  BBB_CORE_PRV.BBB_ITEM_MISC misc
WHERE 
  sku.sku_id = bsku.sku_id 
  AND site.list_pricelist_id = price.price_list
  AND bsku.sku_id = price.sku_id
  AND prd.product_id = chld.product_id
  AND sku.sku_id = chld.sku_id
  AND sku.sku_id = misc.sku_id (+)
  AND sku.display_name is not null
  AND price.list_price is not null
  AND (bsku.jda_sub_dept_id is not NULL OR bsku.jda_class is not NULL)
  AND bsku.web_offered_flag = 1
  AND (bsku.gift_cert_flag <> 1 AND prd.gift_cert <> 1)
  AND bsku.sku_id NOT IN ('GiftWrapUS', 'GiftWrapBaby', 'DeliveryCharge', 'AssemblyFee')
  AND site.id = 'BedBathUS'
UNION
SELECT 
  DECODE(site.id, 'BedBathUS', bbuMerchId, 'BuyBuyBaby', bbbyMerchId) MERCHANT_ID,
  sku.sku_id SKU,
  SUBSTR(REGEXP_REPLACE(UTL_I18N.UNESCAPE_REFERENCE(sku.display_name), '<[^<>]*>', ''), 1, 255) NAME,
  REGEXP_REPLACE(UTL_I18N.UNESCAPE_REFERENCE(sku.description), '<[^<>]*>', '') DESCRIPTION,
  bsku.jda_sub_dept_id || '_' || bsku.jda_class PARENT_ID,
  price.list_price DUTIABLE_PRICE,
  DECODE(site.id, 'BedBathUS', 'https://www.bedbathandbeyond.com/store', 'BuyBuyBaby', 'https://www.buybuybaby.com/store') || prd.SEO_URL URL,
  'https://s7d9.scene7.com/is/image/BedBathandBeyond/' || bsku.scene7_url IMAGE_URL,
  bsku.UPC,
  'US' COS,
  'New' PRODUCT_CONDITION,
  bsku.vendor_id MANUFACTURER,
  'en-us' DEFAULT_LOCALE,
  decode(instr(misc.coo, ','), 0, coo, null, null, 'ZZ') COO,
  decode(misc.length, 0, 0.1, misc.length) BOX_LENGTH,
  decode(misc.width, 0, 0.1, misc.width) BOX_WIDTH,
  decode(misc.height, 0, 0.1, misc.height) BOX_HEIGHT,
  NVL2(misc.length, 'inch', '') SIZE_UNITS,
  decode(misc.weight, 0, 0.1, misc.weight) NET_WEIGHT,
  NVL2(misc.weight, 'lb', '') WEIGHT_UNITS
FROM 
  BBB_CORE_PRV.DCS_SKU sku,
  BBB_CORE_PRV.BBB_SKU bsku,
  BBB_CORE_PRV.DCS_PRICE price,
  BBB_CORE_PRV.DCS_SITE site,
  BBB_CORE_PRV.DCS_PRD_CHLDSKU chld,
  BBB_CORE_PRV.BBB_PRODUCT prd,
  BBB_CORE_PRV.BBB_SKU_SITE_TRANSLATIONS skuSite,
  BBB_CORE_PRV.BBB_ITEM_MISC misc
WHERE 
  sku.sku_id = bsku.sku_id 
  AND site.list_pricelist_id = price.price_list
  AND bsku.sku_id = price.sku_id
  AND prd.product_id = chld.product_id
  AND sku.sku_id = chld.sku_id
  AND skuSite.sku_id = bsku.sku_id
  AND sku.sku_id = misc.sku_id (+)
  AND sku.display_name is not null
  AND price.list_price is not null
  AND (bsku.jda_sub_dept_id is not NULL OR bsku.jda_class is not NULL)
  AND (bsku.gift_cert_flag <> 1 AND prd.gift_cert <> 1)
  AND bsku.sku_id NOT IN ('GiftWrapUS', 'GiftWrapBaby', 'DeliveryCharge', 'AssemblyFee')
  AND site.id = 'BuyBuyBaby'
  AND skuSite.translation_id = '2_en_US_webOffered_Y'
  ;   
BEGIN
    -- Insert categories to be send to BorderFree
	INSERT into 
		BBB_PIM_STG.process_support (ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS) 
	VALUES 
		(BBB_PIM_STG.process_support_seq.nextval, 'AMB_ONETIME', 'BF_CatFeed', 'Inserting categories into ambassador schema.', sysdate, 'AMB', 'A');
	
    FOR nextCat IN category_cursor  
     LOOP
        BEGIN
            Insert into bbb_ambassador_usr_prv.E4X_CATEGORIES (MERCHANT_ID,ID,NAME,PARENT_ID,DESCRIPTION) 
            values (nextCat.MERCHANT_ID,nextCat.ID,nextCat.NAME,nextCat.PARENT_ID,nextCat.DESCRIPTION); 
        EXCEPTION
          WHEN DUP_VAL_ON_INDEX THEN 
		  INSERT into 
			BBB_PIM_STG.process_support (ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS) 
		  VALUES 
			(BBB_PIM_STG.process_support_seq.nextval, 'AMB_ONETIME', 'BF_CatFeed', 'Duplicate Category found. Skipping Insert.', sysdate, 'AMB', 'I');
		  CONTINUE;
        END;
     END LOOP;
	
	INSERT into 
		BBB_PIM_STG.process_support (ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS) 
	VALUES 
		(BBB_PIM_STG.process_support_seq.nextval, 'AMB_ONETIME', 'BF_CatFeed', 'Finished inserting categories into ambassador schema.', sysdate, 'AMB', 'A');
     
    -- Insert SKUs to be send to BorderFree
	INSERT into 
		BBB_PIM_STG.process_support (ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS) 
	VALUES 
		(BBB_PIM_STG.process_support_seq.nextval, 'AMB_ONETIME', 'BF_CatFeed', 'Inserting SKUs into ambassador schema.', sysdate, 'AMB', 'A');
		
    FOR nextSku IN sku_cursor  
     LOOP
        BEGIN
            Insert into bbb_ambassador_usr_prv.E4X_PRODUCTS (MERCHANT_ID,SKU,NAME,DESCRIPTION,PARENT_ID,DUTIABLE_PRICE,URL,IMAGE_URL,UPC,COS,PRODUCT_CONDITION,MANUFACTURER,DEFAULT_LOCALE,COO,BOX_LENGTH,BOX_WIDTH,BOX_HEIGHT,SIZE_UNITS,NET_WEIGHT,WEIGHT_UNITS) 
            values (nextSku.MERCHANT_ID,nextSku.SKU,nextSku.NAME,nextSku.DESCRIPTION,nextSku.PARENT_ID,nextSku.DUTIABLE_PRICE,nextSku.URL,nextSku.IMAGE_URL,nextSku.UPC,nextSku.COS,nextSku.PRODUCT_CONDITION,nextSku.MANUFACTURER,nextSku.DEFAULT_LOCALE,nextSku.COO,nextSku.BOX_LENGTH,nextSku.BOX_WIDTH,nextSku.BOX_HEIGHT,nextSku.SIZE_UNITS,nextSku.NET_WEIGHT,nextSku.WEIGHT_UNITS); 
        EXCEPTION
          WHEN DUP_VAL_ON_INDEX THEN 
		  INSERT into 
			BBB_PIM_STG.process_support (ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS) 
		  VALUES 
			(BBB_PIM_STG.process_support_seq.nextval, 'AMB_ONETIME', 'BF_CatFeed', 'Duplicate SKU found. Skipping Insert.', sysdate, 'AMB', 'I');		  
		  CONTINUE;
        END;
     END LOOP;
	 
	INSERT into 
		BBB_PIM_STG.process_support (ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS) 
	VALUES 
		(BBB_PIM_STG.process_support_seq.nextval, 'AMB_ONETIME', 'BF_CatFeed', 'Finished inserting SKUs into ambassador schema.', sysdate, 'AMB', 'A');	 
    
	INSERT into 
		BBB_PIM_STG.process_support (ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS) 
	VALUES 
		(BBB_PIM_STG.process_support_seq.nextval, 'AMB_ONETIME', 'BF_CatFeed', 'Deleting orphan categories from ambassador schema.', sysdate, 'AMB', 'A');	
	
     -- Delete all orphan categories - bbuMerchId
     DELETE FROM bbb_ambassador_usr_prv.e4x_categories
     WHERE merchant_id = bbuMerchId
     AND parent_id NOT IN (SELECT id FROM bbb_ambassador_usr_prv.e4x_categories WHERE merchant_id = bbuMerchId);
     -- Delete all orphan categories - bbbyMerchId
     DELETE FROM bbb_ambassador_usr_prv.e4x_categories
     WHERE merchant_id = bbbyMerchId
     AND parent_id NOT IN (SELECT id FROM bbb_ambassador_usr_prv.e4x_categories WHERE merchant_id = bbbyMerchId);

	INSERT into 
		BBB_PIM_STG.process_support (ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS) 
	VALUES 
		(BBB_PIM_STG.process_support_seq.nextval, 'AMB_ONETIME', 'BF_CatFeed', 'Finished deleting orphan categories from ambassador schema.', sysdate, 'AMB', 'A');
		
	INSERT into 
		BBB_PIM_STG.process_support (ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS) 
	VALUES 
		(BBB_PIM_STG.process_support_seq.nextval, 'AMB_ONETIME', 'BF_CatFeed', 'Deleting orphan SKUs from ambassador schema.', sysdate, 'AMB', 'A');		
     
     -- Delete all orphan SKUs - bbuMerchId
     DELETE FROM bbb_ambassador_usr_prv.e4x_products
     WHERE merchant_id = bbuMerchId
     AND parent_id NOT IN (SELECT id FROM bbb_ambassador_usr_prv.e4x_categories WHERE merchant_id = bbuMerchId);
     -- Delete all orphan SKUs - bbbyMerchId
     DELETE FROM bbb_ambassador_usr_prv.e4x_products
     WHERE merchant_id = bbbyMerchId
     AND parent_id NOT IN (SELECT id FROM bbb_ambassador_usr_prv.e4x_categories WHERE merchant_id = bbbyMerchId);  

	INSERT into 
		BBB_PIM_STG.process_support (ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS) 
	VALUES 
		(BBB_PIM_STG.process_support_seq.nextval, 'AMB_ONETIME', 'BF_CatFeed', 'Finsihed deleting orphan SKUs from ambassador schema.', sysdate, 'AMB', 'A');

	INSERT into 
		BBB_PIM_STG.process_support (ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS) 
	VALUES 
		(BBB_PIM_STG.process_support_seq.nextval, 'AMB_ONETIME', 'BF_CatFeed', 'Inserting flush string into ambassador schema.', sysdate, 'AMB', 'A');		
     
     -- Insert trigger string to send catalog to BorderFree (TODO: Enable when stored proc code is finalized, keep it commented for now)
     INSERT INTO bbb_ambassador_usr_prv.E4X_TRIGGERS(TASK) VALUES('FlushCatalog');     
     
     -- Commit in the end when all inserts are successful
     COMMIT;
     
     -- Update last run date for the catalog feed
     UPDATE bbb_ambassador_usr_prv.BBB_SCH_PROCESS set status = 'SUCCESS', data_center= dataCenter, LAST_RUN = sysdate WHERE PROCESS_NAME='CatalogFeed';
     COMMIT;
	 
	INSERT into 
		BBB_PIM_STG.process_support (ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS) 
	VALUES 
		(BBB_PIM_STG.process_support_seq.nextval, 'AMB_ONETIME', 'BF_CatFeed', 'One time catalog feed is successful. Commit Done.', sysdate, 'AMB', 'A');	 
	COMMIT;
		
     -- Exception Block
	  EXCEPTION
      WHEN others THEN
        DBMS_OUTPUT.PUT_LINE('Exception while sending catalog data to BorderFree Ambassador.');
        ROLLBACK;
        UPDATE bbb_ambassador_usr_prv.BBB_SCH_PROCESS set status = 'FAIL', data_center= dataCenter WHERE PROCESS_NAME='CatalogFeed';
		INSERT into 
			BBB_PIM_STG.process_support (ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS) 
		VALUES 
			(BBB_PIM_STG.process_support_seq.nextval, 'AMB_ONETIME', 'BF_CatFeed', 'One time catalog feed failed. Rolled-back everything.', sysdate, 'AMB', 'I');
		COMMIT;
END;
/

SHOW ERROR;
COMMIT;
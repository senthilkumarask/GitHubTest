-- build version: 2.04.01.001
SET ECHO ON;
SET DEFINE OFF;
SET SERVEROUTPUT ON;

--------------------------------------------------------
--  File created - Monday-January-27-2014   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package ATG_ENDECAEXPORT_PRICING_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BBB_SWITCH_B.ATG_ENDECAEXPORT_PRICING_PKG IS

  -- file for prices

    PROCEDURE Export_Prices ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
	
	PROCEDURE Export_Prices_Updates ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
	
-- All these files will be generated Empty	
-- files from core tables

    PROCEDURE Export_ColorColorGroups ( p_filename varchar2, p_lastModifiedDate varchar2,  p_feedType varchar2);

    PROCEDURE Export_Features ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
    
    PROCEDURE Export_SKU_Features ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Reviews( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
	
-- For College

	PROCEDURE Export_Colleges_States( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);
	
	PROCEDURE Export_Colleges_Taxonomy( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);	
-- files for SKUs
-- no file for this just populating the endeca_export_products and endeca_export_skus tables

    PROCEDURE Export_SKUs( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Collection_SKUs( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Product_SKUs( p_filename varchar2, p_lastModifiedDate varchar2,  p_feedType varchar2);

-- files for Products

    PROCEDURE Export_UnlikeProducts( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Accessories( p_filename varchar2, p_lastModifiedDate varchar2,  p_feedType varchar2);

    PROCEDURE Export_Lead_Products( p_filename varchar2, p_lastModifiedDate varchar2,  p_feedType varchar2);

    PROCEDURE Export_Simple_Products( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Collection_Products( p_filename varchar2, p_lastModifiedDate varchar2,  p_feedType varchar2);

-- files for Brands

    PROCEDURE Export_Brands( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

-- files for media

    PROCEDURE Export_Prod_Media_Sites ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Media ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

-- files for Attributes

    PROCEDURE Export_Item_Attributes ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Product_Item_Attributes ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_SKU_Item_Attributes ( p_filename varchar2, p_lastModifiedDate varchar2,  p_feedType varchar2);

-- files for Taxonomy

    PROCEDURE Export_BBB_Taxonomy ( p_filename varchar2, p_lastModifiedDate varchar2,  p_feedType varchar2);

    PROCEDURE Export_BAB_Taxonomy ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_CA_Taxonomy ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_Category_Products ( p_filename varchar2, p_lastModifiedDate varchar2,  p_feedType varchar2);

-- files for static pages

    PROCEDURE Export_StaticPages_Site ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_StaticPages ( p_filename varchar2, p_lastModifiedDate varchar2,  p_feedType varchar2);

-- files for guides

    PROCEDURE Export_Guides_Sites ( p_filename varchar2, p_lastModifiedDate varchar2,  p_feedType varchar2);

    PROCEDURE Export_Guides ( p_filename varchar2, p_lastModifiedDate varchar2,  p_feedType varchar2);

-- files for product and sku attributes

    PROCEDURE Export_Product_Site_Properties(p_filename varchar2,  p_lastModifiedDate varchar2, p_feedType varchar2);

    PROCEDURE Export_SKU_Site_Properties(p_filename varchar2, p_lastModifiedDate varchar2,  p_feedType varchar2);
	
	PROCEDURE Export_Prod_site_sales_data(p_filename varchar2,  p_lastModifiedDate varchar2, p_feedType varchar2);
	
    PROCEDURE Export_GoFile(p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2);

    FUNCTION dump_csv ( p_query varchar2, -- query to fired
                        p_separator varchar2 default '|', -- delimeter defaulted to pipe
                        p_dir varchar2, -- database machine folder, should be DirectoryName, as in all_directories table
                        p_filename varchar2, -- name of the output file
                        p_static varchar2, -- static or one time content to be written just below header and before query-content
                        p_header varchar2 ) -- header to be put in the output file
                        return number; -- returns the number of rows written, excluding header
    FUNCTION dump_csv_empty_header (p_dir varchar2, -- database machine folder, should be DirectoryName, as in all_directories table
                        p_filename varchar2, -- name of the output file
                        p_static varchar2, -- static or one time content to be written just below header and before query-content
                        p_header varchar2 ) -- header to be put in the output file
                        return number; -- returns the number of rows written, excluding header

END ATG_ENDECAEXPORT_PRICING_PKG;

/

CREATE OR REPLACE PACKAGE BODY BBB_SWITCH_B.ATG_ENDECAEXPORT_PRICING_PKG
AS 

PROCEDURE Export_ColorColorGroups ( 
  p_filename varchar2, 
  p_lastModifiedDate VARCHAR2, 
  p_feedType varchar2)   
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function  
 
  BEGIN 
      
  SELECT dump_csv_empty_header( p_feedType, p_filename, NULL, 'COLOR_GRP_ID|COLOR_CD' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_ColorColorGroups: ' || l_rows);
END Export_ColorColorGroups;


--Export_Colleges_States 
  
PROCEDURE Export_Colleges_States(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,  
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function  
  
  BEGIN 
      
  SELECT dump_csv_empty_header( p_feedType, p_filename, NULL, 'SCHOOL_VER_ID|SCHOOL_NAME|SMALL_LOGO_URL|STATE_ID|DESCRIP' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Colleges_States: ' || l_rows);
END Export_Colleges_States;


---College Taxanomy 
 
PROCEDURE Export_Colleges_Taxonomy(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,  
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function  
  
  BEGIN     
  
  SELECT dump_csv_empty_header( p_feedType, p_filename, NULL, 'CHILD_CAT_ID|CATEGORY_ID|DISPLAY_NAME|SMALL_LOGO_URL' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Colleges_Taxonomy: '||l_rows);
END Export_Colleges_Taxonomy;
--College Taxanomy End
-- End:: Added as part of CR 8


PROCEDURE Export_Features(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,  
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN     
  
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'ID|FEATURE_NAME' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Features: '||l_rows);
END Export_Features;

PROCEDURE Export_SKU_Features(
  p_filename         VARCHAR2, 
  p_lastModifiedDate VARCHAR2,  
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function  
  
  BEGIN    
  
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'SKU_ID|FEATURE_ID|FEATURE_VALUE|FEATURE_VALUE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_SKU_Features: '||l_rows);
END Export_SKU_Features;


PROCEDURE Export_Product_Item_Attributes(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,    
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function  
  
  BEGIN 
      
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'PRODUCT_ID|SKU_ATTRIBUTE_ID|SITE_ID|START_DATE|END_DATE|MISC_INFO' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Product_Item_Attributes: '||l_rows);
END Export_Product_Item_Attributes;


PROCEDURE Export_Reviews(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,    
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function  
  
  BEGIN 
    
  
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'PRODUCT_ID|AVERAGE_OVERALL_RATING|TOTAL_REVIEW_COUNT|SITE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Reviews: '||l_rows);
END Export_Reviews;


PROCEDURE Export_SKUs(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,    
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN 
      
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'NEW_RECORD|SKU_ID|THUMBNAIL_IMAGE_ID|SMALL_IMAGE_ID|LARGE_IMAGE_ID|MEDIUM_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|ANYWHERE_ZOOM|JDA_DEPT_ID|JDA_SUB_DEPT_ID|JDA_CLASS|GIFT_CERT_FLAG|COLLEGE_ID|SKU_TYPE|EMAIL_OUT_OF_STOCK|COLOR|COLOR_GROUP|SKU_SIZE|GIFT_WRAP_ELIGIBLE|VDC_SKU_TYPE|VDC_SKU_MESSAGE|UPC|VERT_IMAGE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_SKUs: '||l_rows);
END Export_SKUs;


PROCEDURE Export_Collection_SKUs(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,    
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function  
  
  BEGIN 
      
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'PRODUCT_ID|SKU_ID|SEQUENCE_NUM|SITE_ID' ) 
  INTO l_rows 
  FROM dual; 
  dbms_output.put_line('EXPORT_COLLECTION_SKUS: '||l_rows);      
END EXPORT_COLLECTION_SKUS;


PROCEDURE Export_Product_SKUs( 
  p_filename VARCHAR2,
  p_lastModifiedDate VARCHAR2,  
  p_feedType VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN     
  
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'PRODUCT_ID|SKU_ID|SEQUENCE_NUM|SITE_ID' ) 
  INTO l_rows 
  FROM dual;
  dbms_output.put_line('Export_Product_Site_Skus: '||l_rows); 
END Export_Product_SKUs;


PROCEDURE Export_UnlikeProducts(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,    
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN 
    
  
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|VERT_IMAGE_ID|SHOP_GUIDE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_UnlikeProducts: '||l_rows);
END Export_UnlikeProducts;


PROCEDURE Export_Accessories(
  p_filename         VARCHAR2, 
  p_lastModifiedDate VARCHAR2,  
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function  
  
  BEGIN 
      
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|VERT_IMAGE_ID|SHOP_GUIDE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Accessories: '||l_rows);
END Export_Accessories;


PROCEDURE Export_Lead_Products(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN 
  
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|VERT_IMAGE_ID|SHOP_GUIDE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Lead_Products: '||l_rows);
END Export_Lead_Products;


PROCEDURE Export_Simple_Products(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,  
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN 
    
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|VERT_IMAGE_ID|SHOP_GUIDE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Simple_Products: '||l_rows);
END Export_Simple_Products;


PROCEDURE Export_Collection_Products(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,  
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN 
    
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|VERT_IMAGE_ID|SHOP_GUIDE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Collection_Products: '||l_rows);
END Export_Collection_Products;


PROCEDURE Export_Brands(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN 

  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'NEW_RECORD|BRAND_ID|BRAND_DESCRIP|BRAND_IMAGE|SITE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Brands: '||l_rows);
END Export_Brands;


PROCEDURE Export_SKU_Item_Attributes(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN 
      
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'SKU_ID|SKU_ATTRIBUTE_ID|SITE_ID|START_DATE|END_DATE|MISC_INFO' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_SKU_Item_Attributes: '||l_rows);
END Export_SKU_Item_Attributes;


PROCEDURE Export_Item_Attributes(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN 
      
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'NEW_RECORD|SKU_ATTRIBUTE_ID|DISPLAY_DESCRIP|IMAGE_URL|ACTION_URL|PLACE_HOLDER|PRIORITY|SITE_ID|START_DATE|END_DATE|ATTRIBUTE_DISPLAY_NAME' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Item_Attributes: '||l_rows);
END Export_Item_Attributes;


PROCEDURE Export_Prod_Media_Sites(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,  
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function 
  
  BEGIN 
      
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'PRODUCT_ID|MEDIA_ID|SITE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Prod_Media_Sites: '||l_rows);
END Export_Prod_Media_Sites;


PROCEDURE Export_Media(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN 
   
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'NEW_RECORD|MEDIA_ID|MEDIA_TYPE|PROVIDER_ID|MEDIA_SOURCE|MEDIA_DESCRIPTION|COMMENTS|MEDIA_TRANSCRIPT|WIDGET_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Media: '||l_rows);
END Export_Media;


PROCEDURE Export_Category_Products( 
  p_filename varchar2,
  p_lastModifiedDate VARCHAR2,
  p_feedType varchar2)
  IS
  l_rows number; -- pkg var to capture the number of rows affected from the function
  
  BEGIN 
    
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'CATEGORY_ID|CHILD_PRD_ID|SEQUENCE_NUM' ) into l_rows from dual;
  dbms_output.put_line('Export_Category_Products: '||l_rows);
END Export_Category_Products;


PROCEDURE Export_BBB_Taxonomy(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,  
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function  
  
  BEGIN 
    
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'CHILD_CAT_ID|CATEGORY_ID|DISPLAY_NAME|IS_COLLEGE|NODE_TYPE|PHANTOM_CATEGORY|GUIDE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_BBB_Taxonomy: '||l_rows);
END Export_BBB_Taxonomy;
  
  
PROCEDURE Export_BAB_Taxonomy(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN 
    
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'CHILD_CAT_ID|CATEGORY_ID|DISPLAY_NAME|IS_COLLEGE|NODE_TYPE|PHANTOM_CATEGORY|GUIDE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_BAB_Taxonomy: '||l_rows);
END Export_BAB_Taxonomy;
  

PROCEDURE Export_CA_Taxonomy(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN 
    
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'CHILD_CAT_ID|CATEGORY_ID|DISPLAY_NAME|IS_COLLEGE|NODE_TYPE|PHANTOM_CATEGORY|GUIDE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_CA_Taxonomy: '||l_rows);
END Export_CA_Taxonomy;
  
  
PROCEDURE Export_StaticPages_Site(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN    
    
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'STATIC_TEMPLATE_ID|SITE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_StaticPages_Site: '||l_rows);
END Export_StaticPages_Site;


PROCEDURE Export_StaticPages(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,  
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function  
  
  BEGIN 
      
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'NEW_RECORD|STATIC_TEMPLATE_ID|PAGE_NAME|PAGE_TITLE|PAGE_HEADER_COPY|PAGE_COPY|BBB_PAGE_NAME|PAGE_TYPE|SEO_URL' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_StaticPages: '||l_rows);
END Export_StaticPages;


PROCEDURE Export_Guides_sites(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function  
  
  BEGIN   
  
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'GUIDES_ID|SITE_ID' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Guides_sites: '||l_rows);
END Export_Guides_sites;
  
PROCEDURE Export_Guides(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,  
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  
  BEGIN      
  
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'NEW_RECORD|GUIDES_ID|CONTENT_TYPE|GUIDES_CATEGORY|TITLE|IMAGE_URL|IMAGE_ALT_TEXT|SHORT_DESCRIPTION|LONG_DESCRIPTION' )
  INTO l_rows
  FROM dual;
  dbms_output.put_line('Export_Guides: '||l_rows);
END Export_Guides;
  
PROCEDURE Export_Prices(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,
  p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
  nCount NUMBER;
  BEGIN
      dbms_output.put_line('Export_Prices: '||endeca_query);
  
  SELECT COUNT(*) INTO nCount FROM all_tables WHERE table_name ='ENDECA_EXPORT_PRICES';
  IF(nCount > 0)THEN
   EXECUTE immediate 'truncate table endeca_export_prices ';   
  END IF;

  INSERT INTO endeca_export_prices (sku_id, was_price,last_mod_date) SELECT t1.sku_id, t1.list_price,t2.last_mod_date from dcs_price t1,bbb_price t2 where t1.price_id = t2.price_id(+) and t1.price_list = 'plist100005' order by t1.sku_id;
  
  MERGE INTO endeca_export_prices t USING (SELECT distinct sku_id,list_price from dcs_price  where price_list='plist100004') t1 on (t.sku_id = t1.sku_id) WHEN MATCHED THEN UPDATE SET t.is_price =t1.list_price;
  MERGE INTO endeca_export_prices t USING (SELECT distinct sku_id,list_price from dcs_price  where price_list='plist100003') t1 on (t.sku_id = t1.sku_id) WHEN MATCHED THEN UPDATE SET t.ca_is_price =t1.list_price;
  MERGE INTO endeca_export_prices t USING (Select distinct sku_id,list_price from dcs_price  where price_list='plist100002') t1 on (t.sku_id = t1.sku_id) WHEN MATCHED THEN UPDATE Set t.ca_was_price =t1.list_price;
      endeca_query:='SELECT sku_id,LTRIM(to_char(was_price,999999999999.99)),LTRIM(to_char(case when is_price is null or is_price = '''' then was_price else is_price end,999999999999.99)),LTRIM(to_char(ca_was_price,999999999999.99)),LTRIM(to_char(case when ca_is_price is null or ca_is_price = '''' then ca_was_price else ca_is_price end,999999999999.99)) from endeca_export_prices';
      dbms_output.put_line('Export_Prices: '||endeca_query);
  commit;
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'SKU_ID|WAS_PRICE|IS_PRICE|CA_WAS_PRICE|CA_IS_PRICE' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_Prices: '||l_rows);
   EXCEPTION
   WHEN OTHERS THEN  -- handles all other errors
    dbms_output.put_line('Export_Prices: Error!');
  commit;
END Export_Prices;


PROCEDURE Export_Product_Site_Properties(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,	
    p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function 
  BEGIN      
 
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|WEB_OFFERED_FLAG|DISABLE_FLAG|PRODUCT_TITLE|SHORT_DESCRIPTION|LONG_DESCRIPTION|PRICE_RANGE_DESCRIP|SKU_LOW_PRICE|SKU_HIGH_PRICE|ENABLE_DATE|SITE_ID' )
  INTO l_rows
  FROM dual;
 
  dbms_output.put_line('Export_Product_Site_Properties: '||l_rows);
   EXCEPTION
   WHEN OTHERS THEN -- handles all other errors
   dbms_output.put_line('Export_Product_Site_Properties: Error!');
   DBMS_OUTPUT.PUT_LINE (SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
   commit;
END Export_Product_Site_Properties;

PROCEDURE Export_SKU_Site_Properties(
  p_filename         VARCHAR2,
  p_lastModifiedDate VARCHAR2,
  p_feedType         VARCHAR2)
  IS 
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function  
  
  BEGIN             
  
  SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'NEW_RECORD|SKU_ID|WEB_OFFERED_FLAG|DISABLE_FLAG|SKU_TITLE|SHORT_DESCRIPTION|SITE_ID' )
  INTO l_rows
  FROM dual;
  
  dbms_output.put_line('Export_SKU_Site_Properties: '||l_rows);
      EXCEPTION
  WHEN OTHERS THEN -- handles all other errors
  dbms_output.put_line('Export_SKU_Site_Properties: Error!');
  commit;
END Export_SKU_Site_Properties; 
  
PROCEDURE Export_Prod_site_sales_data(
  p_filename varchar2,
  p_lastModifiedDate VARCHAR2,  
  p_feedType varchar2)
  IS
  l_rows number; -- pkg var to capture the number of rows affected from the function   
  
  BEGIN 		
  
	SELECT dump_csv_empty_header(  p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|UNIT_SALES|ORDER_SALES|TOTAL_SALES|SITE_ID' ) into l_rows from dual; 
	dbms_output.put_line('Export_Prod_site_sales_data: '||l_rows);
	EXCEPTION
    WHEN OTHERS THEN  -- handles all other errors
        dbms_output.put_line('Export_Prod_site_sales_data: Error!'); 
		DBMS_OUTPUT.PUT_LINE (SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
		commit;
  END Export_Prod_site_sales_data;
  
  --BBBSL-1740
  
  PROCEDURE Export_Prices_Updates( p_filename VARCHAR2, p_lastModifiedDate VARCHAR2, p_feedType VARCHAR2)
IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
  nCount NUMBER;
  ROWDATA VARCHAR2(12);
BEGIN
  dbms_output.put_line('Export_Prices_Updates: Started');
  
  SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_EXPORT_PRICES_UPDATES';
   dbms_output.put_line('nCount' || nCount );
  IF(nCount > 0)THEN
  
   EXECUTE immediate 'truncate table endeca_export_prices_updates'; 
    
  END IF;    
  IF p_feedType = 'PARTIAL' THEN
   dbms_output.put_line('Partial' );
     insert into endeca_export_prices_updates (product_id, price_range_descrip,sku_low_price,sku_high_price,site_id) SELECT distinct(p.product_id),p.price_range_descrip, p.sku_low_price,p.sku_high_price,
                                case
                                when t1.price_list = 'plist100002' then  'BedBathCanada'
                                when t1.price_list = 'plist100003' then  'BedBathCanada'
                                when t1.price_list = 'plist100004' then  'BedBathUS'
                                when t1.price_list = 'plist100005' then  'BedBathUS'
                                end as site_id
                        from dcs_price t1, bbb_price t2, 
                                dcs_prd_chldsku s,bbb_product p
                        where t1.price_id = t2.price_id(+) 
                                and p.product_id = s.product_id 
                                and s.sku_id = t1.sku_id  
                                and t2.last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') order by p.product_id;
  dbms_output.put_line('Insert Done' );
  
      FOR ROWDATA IN (SELECT * from ENDECA_EXPORT_PRICES_UPDATES where site_id='BedBathUS')
		LOOP
    
    insert into ENDECA_EXPORT_PRICES_UPDATES( PRODUCT_ID,PRICE_RANGE_DESCRIP,SKU_LOW_PRICE,SKU_HIGH_PRICE,SITE_ID)
    VALUES (ROWDATA.product_id,ROWDATA.PRICE_RANGE_DESCRIP,ROWDATA.SKU_LOW_PRICE,ROWDATA.SKU_HIGH_PRICE,'BuyBuyBaby'); 
      
    END LOOP;
    dbms_output.put_line('Buy Buy Baby Updated' );
  END IF;
                                
  endeca_query:='SELECT product_id,price_range_descrip,sku_low_price,sku_high_price,site_id from endeca_export_prices_updates order by product_id'; 

  dbms_output.put_line('Export_Prices_Updates: '||endeca_query); 


  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'PRODUCT_ID|PRICE_RANGE_STRING|SKU_LOW_PRICE|SKU_HIGH_PRICE|SITE_ID' )
  INTO l_rows
  FROM dual;
  
  dbms_output.put_line('Export_Prices_Updates: '||l_rows);

EXCEPTION
WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_Prices_Updates: Error!');
  
END Export_Prices_Updates;

--BBBSL-1740
  
   PROCEDURE Export_GoFile(p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2)
  IS
    l_rows number;
    endeca_query varchar(2000);
  BEGIN
        endeca_query:='SELECT sysdate from dual';
  IF p_feedType = 'PARTIAL' THEN
    SELECT dump_csv( endeca_query, '|', p_feedType, 'partial.go', NULL, 'SYSDATE' )
    INTO l_rows
    FROM dual;
  ELSE
    SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'SYSDATE' )
    INTO l_rows
    FROM dual;
  END IF;
        dbms_output.put_line(p_filename || l_rows);
  END Export_GoFile;
  
FUNCTION dump_csv(
    p_query     IN VARCHAR2,
    p_separator IN VARCHAR2 DEFAULT '|',
    p_dir       IN VARCHAR2 ,
    p_filename  IN VARCHAR2,
    p_static    IN VARCHAR2,
    p_header    IN VARCHAR2)
  RETURN NUMBER
IS
    l_output        utl_file.file_type;
  l_theCursor   INTEGER DEFAULT dbms_sql.open_cursor;
  l_columnValue VARCHAR2(2000);
  l_status      INTEGER;
  l_colCnt      NUMBER DEFAULT 0;
  l_separator   VARCHAR2(10) DEFAULT '';
  l_cnt         NUMBER DEFAULT 0;
  l_dir         VARCHAR2(20);
BEGIN
  IF p_dir = 'PARTIAL' THEN
    l_dir := 'ATG_APP_PARTIAL';
    ELSE 
    l_dir:= 'ATG_APP_FULL';
    END IF;
    dbms_sql.parse(  l_theCursor,  p_query, dbms_sql.native );
    l_output := utl_file.fopen( l_dir, p_filename, 'w', 32767 );
    utl_file.put( l_output, p_header );
    utl_file.new_line( l_output );
  IF ( p_static IS NOT NULL ) THEN
        utl_file.put( l_output, p_static );
        utl_file.new_line( l_output );
  END IF;
  FOR i IN 1 .. 255
  LOOP
    BEGIN
            dbms_sql.define_column( l_theCursor, i, l_columnValue, 2000 );
            l_colCnt := i;
    EXCEPTION
    WHEN OTHERS THEN
      IF ( SQLCODE = -1007 ) THEN
        EXIT;
      ELSE
        raise;
      END IF;
    END;
  END LOOP;
    dbms_sql.define_column( l_theCursor, 1, l_columnValue, 2000 );
    l_status := dbms_sql.execute(l_theCursor);
  IF (dbms_sql.fetch_rows(l_theCursor) > 0 ) THEN
    LOOP
            l_separator := '';
      FOR i IN 1 .. l_colCnt
      LOOP
                dbms_sql.column_value( l_theCursor, i, l_columnValue );
                utl_file.put( l_output, l_separator || l_columnValue );
                l_separator := p_separator;
      END LOOP;
            utl_file.new_line( l_output );
            l_cnt := l_cnt+1;
      EXIT
    WHEN ( dbms_sql.fetch_rows(l_theCursor) <= 0 );
    END LOOP;
        dbms_sql.close_cursor(l_theCursor);
  END IF;
    utl_file.fclose( l_output );
  RETURN l_cnt;
END dump_csv;

FUNCTION dump_csv_empty_header(    
    p_dir       IN VARCHAR2 ,
    p_filename  IN VARCHAR2,
    p_static    IN VARCHAR2,
    p_header    IN VARCHAR2)
  RETURN NUMBER
IS
    l_output        utl_file.file_type;
  l_theCursor   INTEGER DEFAULT dbms_sql.open_cursor;
  l_columnValue VARCHAR2(2000);
  l_status      INTEGER;
  l_colCnt      NUMBER DEFAULT 0;
  l_separator   VARCHAR2(10) DEFAULT '';
  l_cnt         NUMBER DEFAULT 0;
  l_dir         VARCHAR2(20);
BEGIN
  IF p_dir = 'PARTIAL' THEN
    l_dir := 'ATG_APP_PARTIAL';
    ELSE 
    l_dir:= 'ATG_APP_FULL';
    END IF;    
    l_output := utl_file.fopen( l_dir, p_filename, 'w', 32767 );
    utl_file.put( l_output, p_header );
    utl_file.new_line( l_output );
  IF ( p_static IS NOT NULL ) THEN
        utl_file.put( l_output, p_static );
        utl_file.new_line( l_output );
  END IF;
    utl_file.fclose( l_output );
  RETURN l_cnt;
END dump_csv_empty_header;

END ATG_ENDECAEXPORT_PRICING_PKG;
/
SHOW ERROR;
COMMIT;
set define off
create or replace
PACKAGE ATG_GS_EndecaExport_Pkg IS

-- files from core tables

    PROCEDURE Export_GS_ColorColorGroups ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

    PROCEDURE Export_GS_Features ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
    
    PROCEDURE Export_GS_SKU_Features ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

    PROCEDURE Export_GS_Reviews( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
	

-- files for SKUs

    PROCEDURE Export_GS_SKUs( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

    PROCEDURE Export_GS_Product_SKUs( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

-- files for Products

    PROCEDURE Export_GS_Accessories( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

    PROCEDURE Export_GS_Lead_Products( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

    PROCEDURE Export_GS_Simple_Products( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

-- files for Brands

    PROCEDURE Export_GS_Brands( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

-- files for Attributes

    PROCEDURE Export_GS_Item_Attributes ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

    PROCEDURE Export_GS_Product_Item_Attr ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

    PROCEDURE Export_GS_SKU_Item_Attributes ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

-- files for Taxonomy

    PROCEDURE Export_GS_Category_Products ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

-- file for prices

    PROCEDURE Export_GS_Prices ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
--Done
-- files for product and sku attributes

    PROCEDURE Export_GS_Product_Site_Prop(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

    PROCEDURE Export_GS_SKU_Site_Properties(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

-- <Begin>files for STOFU

	PROCEDURE Export_GS_BBB_Channel_Category(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
	
	PROCEDURE Export_GS_BAB_Channel_Category(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
	
	PROCEDURE Export_GS_CA_Channel_Category(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
	
	PROCEDURE Export_GS_BBB_Taxonomy(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
	
	PROCEDURE Export_GS_BAB_Taxonomy(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);
	
	PROCEDURE Export_GS_CA_Taxonomy(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

-- <End>files for STOFU	
	
    PROCEDURE Export_GS_GoFile(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2);

    FUNCTION dump_csv ( p_query varchar2, -- query to fired
                        p_separator varchar2 default '|', -- delimeter defaulted to pipe
                        p_dir varchar2, -- database machine folder, should be DirectoryName, as in all_directories table
                        p_filename varchar2, -- name of the output file
                        p_static varchar2, -- static or one time content to be written just below header and before query-content
                        p_header varchar2 ) -- header to be put in the output file
                        return number; -- returns the number of rows written, excluding header

END ATG_GS_EndecaExport_Pkg;
/
set define off
CREATE OR REPLACE
PACKAGE BODY              ATG_GS_EndecaExport_Pkg 
AS

  PROCEDURE Export_GS_ColorColorGroups ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='SELECT COLOR_GRP_ID, COLOR_CD FROM stofu_12sep_qa_core.BBB_COLOR_COLOR_GROUP';
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'COLOR_GRP_ID|COLOR_CD' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_ColorColorGroups: '||l_rows);
  END Export_GS_ColorColorGroups;

  PROCEDURE Export_GS_Features ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='SELECT FACET_ID,DESCRIPTION from stofu_12sep_qa_core.BBB_FACET_TYPES';
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'ID|FEATURE_NAME' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_Features: '||l_rows);
  END Export_GS_Features;
  
  PROCEDURE Export_GS_SKU_Features ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='SELECT ITEM_ID, FACET_ID, FACET_VALUE, FACET_VALUE_ID from stofu_12sep_qa_core.BBB_FACET_SKU';
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'SKU_ID|FEATURE_ID|FEATURE_VALUE|FEATURE_VALUE_ID' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_SKU_Features: '||l_rows);
  END Export_GS_SKU_Features;

  PROCEDURE Export_GS_Product_Item_Attr ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='SELECT A.PRODUCT_ID,B.SKU_ATTRIBUTE_ID,C.SITE_ID,B.START_DATE,B.END_DATE,B.MISC_INFO 
            FROM BBB_PRD_ATTR_RELN A, BBB_ATTR_RELN B, BBB_ATTR_RELN_SITES C 
            WHERE A.PRD_ATTR_RELN_ID=B.SKU_ATTR_RELN_ID AND C.SITE_ID like ''GS_%''
            AND A.PRD_ATTR_RELN_ID = C.SKU_ATTR_RELN_ID
            AND NVL(b.start_date,sysdate) <= sysdate and NVL(b.end_date, sysdate) >= sysdate';
      if p_feedType = 'PARTIAL' then
          endeca_query := endeca_query || ' and b.last_mod_date > '''|| p_lastModifiedDate ||'''' ;
      end if;
      
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'PRODUCT_ID|SKU_ATTRIBUTE_ID|SITE_ID|START_DATE|END_DATE|MISC_INFO' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_Product_Item_Attr: '||l_rows);
  END Export_GS_Product_Item_Attr;

  PROCEDURE Export_GS_Reviews ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='SELECT PRODUCT_ID,AVERAGE_OVERALL_RATING,TOTAL_REVIEW_COUNT FROM stofu_12sep_qa_core.BBB_BAZAAR_VOICE';
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'PRODUCT_ID|AVERAGE_OVERALL_RATING|TOTAL_REVIEW_COUNT' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_Reviews: '||l_rows);
  END Export_GS_Reviews;

  PROCEDURE Export_GS_SKUs ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
	execute immediate 'create global temporary table endeca_expt_GS_SKUs (sku_id varchar2(40),gsPLPImage varchar2(254)) ON COMMIT DELETE ROWS'; 
      execute immediate 'create index GBL_TMP_GS_SKUs on endeca_expt_GS_SKUs (sku_id)';
	  execute immediate 'insert into endeca_expt_GS_SKUs (sku_id) select dcs_sku.sku_id from bbb_sku,dcs_sku where bbb_sku.sku_id=dcs_sku.sku_id';
	   execute immediate 'update endeca_expt_GS_SKUs t set t.gsPLPImage = (select t1.scene7_url from BBB_GS_SKU_IMAGES t1 where t.sku_id=t1.sku_id and t1.SHOT_TYPE = ''GS_PLP'')';
	   
	  endeca_query:='select ''*#*'',a.sku_id, replace(a.display_name,''"'',''&quot;''), replace(a.description,''"'',''&quot;''), 
                case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
                case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,
                case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, 
                case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id,
                d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id,
                d.zoom_index, d.anywhere_zoom, b.jda_dept_id, b.jda_sub_dept_id, b.jda_class, b.gift_cert_flag, b.college_id, b.sku_type,b.email_out_of_stock,b.color,b.color_group,replace(b.sku_size,''"'',''&quot;''),b.gift_wrap_eligible,b.vdc_sku_type,b.vdc_sku_message,b.upc,
                case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, case when e.gsPLPImage is not null then e.gsPLPImage else b.scene7_url end as gsPLPImage
            from dcs_sku a, bbb_sku b, dcs_sku_media c, bbb_sku_media d, endeca_expt_GS_SKUs e
            where a.sku_id=b.sku_id and a.sku_id=e.sku_id and a.sku_id=c.sku_id(+) and a.sku_id=d.sku_id(+) ';

      if p_feedType = 'PARTIAL' then
          endeca_query := 'select distinct ''*#*'',a.sku_id, replace(a.display_name,''"'',''&quot;''), replace(a.description,''"'',''&quot;''), 
                            case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
                            case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,
                            case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, 
                            case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id,
                            d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id,
                            d.zoom_index, d.anywhere_zoom, b.jda_dept_id, b.jda_sub_dept_id, b.jda_class, b.gift_cert_flag, b.college_id, b.sku_type,b.email_out_of_stock,b.color,b.color_group,replace(b.sku_size,''"'',''&quot;''),b.gift_wrap_eligible,b.vdc_sku_type,b.vdc_sku_message,b.upc,
                            case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, case when e.gsPLPImage is not null then e.gsPLPImage else b.scene7_url end as gsPLPImage
                        from dcs_sku a, bbb_sku b, dcs_sku_media c, bbb_sku_media d, dcs_price i, bbb_price j, endeca_expt_GS_SKUs e
                        where a.sku_id=b.sku_id and a.sku_id=e.sku_id and a.sku_id=c.sku_id(+) and a.sku_id=d.sku_id(+) and a.sku_id = i.sku_id and i.price_id = j.price_id
                        and ( b.last_mod_date > '''|| p_lastModifiedDate ||''' 
                          or j.last_mod_date > '''|| p_lastModifiedDate ||''' )' ;
      end if;
	  dbms_output.put_line('Export_GS_SKUs: '||endeca_query);
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'NEW_RECORD|SKU_ID|DISPLAY_NAME|DESCRIPTION|THUMBNAIL_IMAGE_ID|SMALL_IMAGE_ID|LARGE_IMAGE_ID|MEDIUM_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|ANYWHERE_ZOOM|JDA_DEPT_ID|JDA_SUB_DEPT_ID|JDA_CLASS|GIFT_CERT_FLAG|COLLEGE_ID|SKU_TYPE|EMAIL_OUT_OF_STOCK|COLOR|COLOR_GROUP|SKU_SIZE|GIFT_WRAP_ELIGIBLE|VDC_SKU_TYPE|VDC_SKU_MESSAGE|UPC|VERT_IMAGE_ID|GSPLPIMAGE' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_SKUs: '||l_rows);
	  execute immediate 'drop index GBL_TMP_GS_SKUs';
      execute immediate 'drop table endeca_expt_GS_SKUs';
	  EXCEPTION
    WHEN OTHERS THEN  -- handles all other errors
        dbms_output.put_line('Export_GS_SKUs: Error!'); 
        execute immediate 'drop index GBL_TMP_GS_SKUs';
        execute immediate 'drop table endeca_expt_GS_SKUs';
        execute Immediate 'commit';
  END Export_GS_SKUs;

  PROCEDURE Export_GS_Product_SKUs ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='select product_id,sku_id,sequence_num from dcs_prd_chldsku where product_id in (select product_id from bbb_product where product_id not in (select product_id from bbb_prd_reln where like_unlike=1) union select child_prd_id from dcs_cat_chldprd)';
      if p_feedType = 'PARTIAL' then
          -- endeca_query:='select a.product_id,a.sku_id,a.sequence_num from dcs_prd_chldsku a,BBB_PRODUCT b where a.product_id = b.product_id and b.last_mod_date > '''|| p_lastModifiedDate ||''' and a.product_id in (select product_id from bbb_product where product_id not in (select product_id from bbb_prd_reln where like_unlike=1))';
             endeca_query:='select distinct a.product_id,a.sku_id,a.sequence_num from dcs_prd_chldsku a, BBB_PRODUCT b where a.product_id = b.product_id and b.last_mod_date > '''|| p_lastModifiedDate ||''' and a.product_id in ( select product_id from bbb_product where product_id not in ( select product_id from bbb_prd_reln where like_unlike=1 ) )';
      end if;
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'PRODUCT_ID|SKU_ID|SEQUENCE_NUM' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_Product_SKUs: '||l_rows);
  END Export_GS_Product_SKUs;

  PROCEDURE Export_GS_Accessories ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
	execute immediate 'create global temporary table endeca_expt_GS_Accessories (product_id varchar2(40),gsPLPImage varchar2(254)) ON COMMIT DELETE ROWS'; 
      execute immediate 'create index GBL_TMP_GS_Accessories on endeca_expt_GS_Accessories (product_id)';
	  
	   execute immediate 'insert into endeca_expt_GS_Accessories (product_id) select dcs_product.product_id from bbb_product,dcs_product where bbb_product.product_id=dcs_product.product_id';

	   execute immediate 'update endeca_expt_GS_Accessories t set t.gsPLPImage = (select t1.scene7_url from BBB_GS_PRODUCT_IMAGES t1 where t.product_id=t1.product_id and t1.SHOT_TYPE = ''GS_PLP'')';
      endeca_query:='select distinct ''*#*'',a.product_id,display_name,description,dbms_lob.substr( long_description, 8000, 1 ),gift_cert,collection_flag,swatch_flag,brand_id,b.email_out_of_stock,
            show_images_in_collection,f.school_name,rollup_type_code,
            case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id, 
            case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id,
            case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
            case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id,
            d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index, 
            case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail,
            anywhere_zoom,case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end, b.enable_date,
            case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id,case when e.gsPLPImage is not null then e.gsPLPImage else b.scene7_url end as gsPLPImage
            from dcs_product a, bbb_product b, dcs_prd_media c, bbb_prd_media d, stofu_12sep_qa_core.BBB_SCHOOLS f, endeca_expt_GS_Accessories e
            where a.product_id = b.product_id and a.product_id = e.product_id and a.product_id=c.product_id(+)
            and a.product_id=d.product_id(+) and b.college_id=f.school_id(+)
            and a.product_id in ( select distinct c.product_id from bbb_product a, bbb_prd_prd_reln b, bbb_prd_reln c  where a.product_id=b.product_id
            and b.product_relan_id=c.product_relan_id and a.lead_prd_flag=''1'')';

      if p_feedType = 'PARTIAL' then
           endeca_query := endeca_query || ' and ( b.last_mod_date > '''|| p_lastModifiedDate ||''') ' ;
      end if;
      dbms_output.put_line('Export_GS_Accessories: '||endeca_query);
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'NEW_RECORD|PRODUCT_ID|DISPLAY_NAME|DESCRIPTION|LONG_DESCRIPTION|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|ENABLE_DATE|VERT_IMAGE_ID|SHOP_GUIDE_ID|GSPLPIMAGE' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_Accessories: '||l_rows);
	  execute immediate 'drop index GBL_TMP_GS_Accessories';
      execute immediate 'drop table endeca_expt_GS_Accessories';
	  EXCEPTION
    WHEN OTHERS THEN  -- handles all other errors
        dbms_output.put_line('Export_GS_Accessories: Error!'); 
        execute immediate 'drop index GBL_TMP_GS_Accessories';
        execute immediate 'drop table endeca_expt_GS_Accessories';
        execute Immediate 'commit';
  END Export_GS_Accessories;

  PROCEDURE Export_GS_Lead_Products ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
  execute immediate 'create global temporary table endeca_expt_Lead_Products (product_id varchar2(40),gsPLPImage varchar2(254)) ON COMMIT DELETE ROWS'; 
      execute immediate 'create index GBL_TMP_Lead_Products on endeca_expt_Lead_Products (product_id)';
	  if p_feedType = 'PARTIAL' then
	   execute immediate 'insert into endeca_expt_Lead_Products (product_id) select dcs_product.product_id from bbb_product,dcs_product where bbb_product.product_id=dcs_product.product_id and bbb_product.last_mod_date > '''|| p_lastModifiedDate ||'''';
	   else
	   execute immediate 'insert into endeca_expt_Lead_Products (product_id) select dcs_product.product_id from bbb_product,dcs_product where bbb_product.product_id=dcs_product.product_id';
	   end if;
	   execute immediate 'update endeca_expt_Lead_Products t set t.gsPLPImage = (select t1.scene7_url from BBB_GS_PRODUCT_IMAGES t1 where t.product_id=t1.product_id and t1.SHOT_TYPE = ''GS_PLP'')';

    endeca_query:='select distinct ''*#*'',a.product_id,display_name,description,dbms_lob.substr( long_description, 8000, 1 ),gift_cert,collection_flag,swatch_flag,brand_id,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code,
        case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id, 
        case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id,
        case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
        case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id,
        d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index, 
        case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail,
        anywhere_zoom, case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end, b.enable_date,
        case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id, case when e.gsPLPImage is not null then e.gsPLPImage else b.scene7_url end as gsPLPImage
        from dcs_product a, bbb_product b, dcs_prd_media c, bbb_prd_media d,endeca_expt_Lead_Products e, bbb_prd_prd_reln r, stofu_12sep_qa_core.BBB_SCHOOLS f 
        where a.product_id = b.product_id and a.product_id = e.product_id and a.product_id = r.product_id and a.product_id=c.product_id(+) and a.product_id=d.product_id(+) 
        and b.college_id=f.school_id(+) and b.lead_prd_flag=''1'' ';

    if p_feedType = 'PARTIAL' then
        endeca_query:='select distinct ''*#*'',a.product_id,display_name,description,dbms_lob.substr( long_description, 8000, 1 ),gift_cert,collection_flag,swatch_flag,brand_id,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code, case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id, case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index,  case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail, anywhere_zoom, case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end, b.enable_date, case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id, case when e.gsPLPImage is not null then e.gsPLPImage else b.scene7_url end as gsPLPImage from dcs_product a, bbb_product b,endeca_expt_Lead_Products e, dcs_prd_media c, bbb_prd_media d, bbb_prd_prd_reln r, stofu_12sep_qa_core.BBB_SCHOOLS f, dcs_prd_chldsku g, bbb_sku h, dcs_cat_chldprd i, bbb_category j where a.product_id = b.product_id and a.product_id = e.product_id and a.product_id = r.product_id and a.product_id=g.product_id(+) and g.sku_id=h.sku_id(+) and a.product_id=c.product_id(+) and a.product_id=d.product_id(+)  and a.product_id=i.child_prd_id (+) and i.category_id = j.category_id and b.college_id=f.school_id(+) and b.lead_prd_flag=''1''  and ( b.last_mod_date > '''|| p_lastModifiedDate ||''' or h.last_mod_date > '''|| p_lastModifiedDate ||'''  or j.last_mod_date > '''|| p_lastModifiedDate ||''' ) ' ;
    end if;
    dbms_output.put_line('endeca_query: '||endeca_query);
    select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'NEW_RECORD|PRODUCT_ID|DISPLAY_NAME|DESCRIPTION|LONG_DESCRIPTION|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|ENABLE_DATE|VERT_IMAGE_ID|SHOP_GUIDE_ID|GSPLPIMAGE' ) into l_rows from dual;

    dbms_output.put_line('Export_GS_Lead_Products: '||l_rows);
	execute immediate 'drop index GBL_TMP_Lead_Products';
      execute immediate 'drop table endeca_expt_Lead_Products';
	  EXCEPTION
    WHEN OTHERS THEN  -- handles all other errors
        dbms_output.put_line('Export_GS_Lead_Products: Error!'); 
        execute immediate 'drop index GBL_TMP_Lead_Products';
        execute immediate 'drop table endeca_expt_Lead_Products';
        execute Immediate 'commit';
  END Export_GS_Lead_Products;

  PROCEDURE Export_GS_Simple_Products ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
execute immediate 'create global temporary table endeca_expt_Simple_Products (product_id varchar2(40),gsPLPImage varchar2(254)) ON COMMIT DELETE ROWS'; 
      execute immediate 'create index GBL_TMP_Simple_Products on endeca_expt_Simple_Products (product_id)';
	  if p_feedType = 'PARTIAL' then
	   execute immediate 'insert into endeca_expt_Simple_Products (product_id) select dcs_product.product_id from bbb_product,dcs_product where bbb_product.product_id=dcs_product.product_id and bbb_product.last_mod_date > '''|| p_lastModifiedDate ||'''';
	   else
	   execute immediate 'insert into endeca_expt_Simple_Products (product_id) select dcs_product.product_id from bbb_product,dcs_product where bbb_product.product_id=dcs_product.product_id';
	   end if;
	   execute immediate 'update endeca_expt_Simple_Products t set t.gsPLPImage = (select t1.scene7_url from BBB_GS_PRODUCT_IMAGES t1 where t.product_id=t1.product_id and t1.SHOT_TYPE = ''GS_PLP'')';
    endeca_query:='select distinct ''*#*'',a.product_id,a.display_name,a.description as description,dbms_lob.substr( long_description, 8000, 1 ),gift_cert,collection_flag,swatch_flag,brand_id,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code,
        case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id, 
        case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id,
        case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
        case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id,
        d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index, 
        case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail,
        anywhere_zoom,case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end, b.enable_date,
        case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id,case when e.gsPLPImage is not null then e.gsPLPImage else b.scene7_url end as gsPLPImage
        from dcs_product a, bbb_product b, dcs_prd_media c, bbb_prd_media d, stofu_12sep_qa_core.BBB_SCHOOLS f ,endeca_expt_Simple_Products e
        where a.product_id = b.product_id and a.product_id = e.product_id and a.product_id=c.product_id(+) and a.product_id=d.product_id(+) 
        and b.college_id=f.school_id(+) and b.collection_flag=''0'' and b.lead_prd_flag = ''0'' and exists(select 1 from dcs_cat_chldprd a1 where a1.child_prd_id = a.product_id) ';

    if p_feedType = 'PARTIAL' then
        endeca_query:='select distinct ''*#*'',a.product_id,a.display_name,a.description as description,dbms_lob.substr( long_description, 8000, 1 ),gift_cert,collection_flag,swatch_flag,brand_id,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code, case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id, case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index,  case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail, anywhere_zoom,case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end, b.enable_date, case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id,case when e.gsPLPImage is not null then e.gsPLPImage else b.scene7_url end as gsPLPImage from dcs_product a, bbb_product b, dcs_prd_media c, bbb_prd_media d, stofu_12sep_qa_core.BBB_SCHOOLS f,endeca_expt_Simple_Products e, dcs_prd_chldsku g, bbb_sku h, dcs_cat_chldprd i, bbb_category j where a.product_id = b.product_id and a.product_id = e.product_id and a.product_id=c.product_id(+) and a.product_id=d.product_id(+)  and a.product_id=g.product_id(+) and g.sku_id=h.sku_id(+) and a.product_id=i.child_prd_id (+) and i.category_id = j.category_id and b.college_id=f.school_id(+) and b.collection_flag=''0'' and b.lead_prd_flag = ''0'' and exists (select 1 from dcs_cat_chldprd a1 where a1.child_prd_id = a.product_id)  and ( b.last_mod_date > '''|| p_lastModifiedDate ||''' or h.last_mod_date > '''|| p_lastModifiedDate ||'''  or j.last_mod_date > '''|| p_lastModifiedDate ||''' ) ' ;
    end if;
    dbms_output.put_line('Export_GS_Simple_Products: '||endeca_query);
    select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'NEW_RECORD|PRODUCT_ID|DISPLAY_NAME|DESCRIPTION|LONG_DESCRIPTION|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|ENABLE_DATE|VERT_IMAGE_ID|SHOP_GUIDE_ID|GSPLPIMAGE' ) into l_rows from dual;

    dbms_output.put_line('Export_GS_Simple_Products: '||l_rows);
	execute immediate 'drop index GBL_TMP_Simple_Products';
      execute immediate 'drop table endeca_expt_Simple_Products';
	  EXCEPTION
    WHEN OTHERS THEN  -- handles all other errors
        dbms_output.put_line('Export_GS_Simple_Products: Error!'); 
        execute immediate 'drop index GBL_TMP_Simple_Products';
        execute immediate 'drop table endeca_expt_Simple_Products';
        execute Immediate 'commit';
  END Export_GS_Simple_Products;

  PROCEDURE Export_GS_Brands ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='select ''*#*'',a.brand_id,brand_descrip,brand_image,site_id from BBB_BRANDS a, bbb_brand_sites b where a.brand_id=b.brand_id and b.site_id like ''GS_%''';
       dbms_output.put_line('Export_GS_Brands: '||endeca_query);     
     if p_feedType = 'PARTIAL' then
          endeca_query := endeca_query || ' and a.last_mod_date > '''|| p_lastModifiedDate ||'''' ;
         end if;
      dbms_output.put_line('Export_GS_Brands: '||endeca_query);  

      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'NEW_RECORD|BRAND_ID|BRAND_DESCRIP|BRAND_IMAGE|SITE_ID' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_Brands: '||l_rows);
  END Export_GS_Brands;

  PROCEDURE Export_GS_SKU_Item_Attributes ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      if p_feedType = 'PARTIAL' then
          endeca_query := 'select a.sku_id,b.sku_attribute_id,c.site_id,b.start_date,b.end_date,b.misc_info 
            from BBB_SKU_ATTR_RELN a, BBB_ATTR_RELN b, BBB_ATTR_RELN_SITES C  
            where a.sku_attr_reln_id=b.sku_attr_reln_id 
            and a.sku_attr_reln_id=c.sku_attr_reln_id
			and c.site_id like ''GS_%''
            and NVL(b.start_date,sysdate) <= sysdate and NVL(b.end_date, sysdate) >= sysdate and b.last_mod_date > '''|| p_lastModifiedDate ||'''' ;
      else
		endeca_query:='select a.sku_id,b.sku_attribute_id,c.site_id,b.start_date,b.end_date,b.misc_info 
            from BBB_SKU_ATTR_RELN a, BBB_ATTR_RELN b, BBB_ATTR_RELN_SITES C  
            where a.sku_attr_reln_id=b.sku_attr_reln_id 
            and a.sku_attr_reln_id=c.sku_attr_reln_id
			and c.site_id like ''GS_%''
            and NVL(b.start_date,sysdate) <= sysdate and NVL(b.end_date, sysdate) >= sysdate';
	  end if;
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'SKU_ID|SKU_ATTRIBUTE_ID|SITE_ID|START_DATE|END_DATE|MISC_INFO' ) into l_rows from dual;
      dbms_output.put_line('endeca_query: '||endeca_query);
	  dbms_output.put_line('Export_GS_SKU_Item_Attributes: '||l_rows);
  END Export_GS_SKU_Item_Attributes;

  PROCEDURE Export_GS_Item_Attributes ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN

      endeca_query:='select ''*#*'',a.sku_attribute_id, replace (display_descrip, ''"'',''''''''), image_url,action_url, place_holder, priority, site_id, start_date, end_date,
            CASE WHEN INSTR( display_descrip, ''>'') > 1 THEN SUBSTR( display_descrip, INSTR( display_descrip, ''>'') + 1, INSTR( display_descrip, ''</'') - INSTR( display_descrip, ''>'') - 1 ) ELSE display_descrip END ATTRIBUTE_DISPLAY_NAME
            from BBB_SKU_ATTRIBUTES_INFO a, BBB_SKU_ATTRIBUTE_SITES b where a.sku_attribute_id = b.sku_attribute_id and b.site_id like ''GS_%''
            and NVL(a.start_date,sysdate) <= sysdate and NVL(a.end_date, sysdate) >= sysdate and place_holder is not null';

      if p_feedType = 'PARTIAL' then
          endeca_query:='select distinct ''*#*'',c.sku_attribute_id, replace (display_descrip, ''"'',''''''''), a.image_url, a.action_url, a.place_holder, a.priority, b.site_id, a.start_date, a.end_date,
                CASE WHEN INSTR( display_descrip, ''>'') > 1 THEN SUBSTR( display_descrip, INSTR( display_descrip, ''>'') + 1, INSTR( display_descrip, ''</'') - INSTR( display_descrip, ''>'') - 1 ) ELSE display_descrip END ATTRIBUTE_DISPLAY_NAME
                from BBB_SKU_ATTRIBUTES_INFO a, BBB_SKU_ATTRIBUTE_SITES b, BBB_ATTR_RELN c 
                where a.sku_attribute_id = b.sku_attribute_id and b.sku_attribute_id = c.sku_attribute_id  and b.site_id like ''GS_%''
                and NVL(a.start_date,sysdate) <= sysdate and NVL(a.end_date, sysdate) >= sysdate and place_holder is not null
                and c.last_mod_date > '''|| p_lastModifiedDate ||''' ';
      end if;
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'NEW_RECORD|SKU_ATTRIBUTE_ID|DISPLAY_DESCRIP|IMAGE_URL|ACTION_URL|PLACE_HOLDER|PRIORITY|SITE_ID|START_DATE|END_DATE|ATTRIBUTE_DISPLAY_NAME' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_Item_Attributes: '||l_rows);
  END Export_GS_Item_Attributes;

  PROCEDURE Export_GS_Category_Products ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='select category_id,child_prd_id,sequence_num from dcs_cat_chldprd where (category_id like ''4%'' OR category_id like ''5%'' OR category_id like ''6%'')';
      if p_feedType = 'PARTIAL' then
          --endeca_query := 'select a.category_id,a.child_prd_id,a.sequence_num from dcs_cat_chldprd a, BBB_CATEGORY b, BBB_PRODUCT c 
          --      where c.product_id = a.child_prd_id and a.category_id = b.category_id 
          --      and ( b.last_mod_date > '''|| p_lastModifiedDate ||''' or (c.last_mod_date > '''|| p_lastModifiedDate ||'''))' ;
          endeca_query:='select distinct a.category_id, a.child_prd_id, a.sequence_num
                        from dcs_cat_chldprd a, BBB_CATEGORY b, BBB_PRODUCT c, dcs_prd_chldsku g, bbb_sku h, dcs_price h1, dcs_price i, bbb_price h2, bbb_price i1 
                        where a.category_id = b.category_id and c.product_id = a.child_prd_id 
                        and c.product_id = g.product_id (+) and g.sku_id = h.sku_id (+) and c.sku_low_price = h1.sku_id (+) and c.sku_high_price = i.sku_id (+) 
                        and h1.price_id = h2.price_id (+) and i.price_id = i1.price_id (+)
                        and ( b.last_mod_date >'''|| p_lastModifiedDate ||'''
                          or c.last_mod_date >'''|| p_lastModifiedDate ||'''
                          or h.last_mod_date > '''|| p_lastModifiedDate ||'''
                          or h2.last_mod_date >'''|| p_lastModifiedDate ||'''
                          or i1.last_mod_date >'''|| p_lastModifiedDate ||'''
                          ) and (a.category_id like ''4%'' OR a.category_id like ''5%'' OR a.category_id like ''6%'')
                        order by a.category_id, a.sequence_num';

      end if;
      dbms_output.put_line('Export_GS_Category_Products: '||endeca_query);
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'CATEGORY_ID|CHILD_PRD_ID|SEQUENCE_NUM' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_Category_Products: '||l_rows);
  END Export_GS_Category_Products;

  PROCEDURE Export_GS_Prices ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      execute immediate 'CREATE GLOBAL TEMPORARY TABLE endeca_export_prices ( sku_id varchar(20), was_price NUMBER(19,7), is_price NUMBER(19,7), ca_was_price NUMBER(19,7), ca_is_price NUMBER(19,7),last_mod_date TIMESTAMP(6)) ON COMMIT DELETE ROWS';
	  execute immediate 'create index GBL_TMP_IDX1 on endeca_export_prices (sku_id)';
      if p_feedType = 'PARTIAL' then
          execute immediate 'insert into endeca_export_prices (sku_id, was_price,last_mod_date) select t1.sku_id, t1.list_price,t2.last_mod_date from dcs_price t1, bbb_price t2, dcs_prd_chldsku s, bbb_product p where t1.price_id = t2.price_id(+) and p.product_id = s.product_id and s.sku_id = t1.sku_id  and t1.price_list = ''plist100005'' and ( p.last_mod_date > '''|| p_lastModifiedDate ||''' or t2.last_mod_date > '''|| p_lastModifiedDate ||''' ) order by t1.sku_id';
      else
          execute immediate 'insert into endeca_export_prices (sku_id, was_price,last_mod_date) select t1.sku_id, t1.list_price,t2.last_mod_date from dcs_price t1,bbb_price t2 where t1.price_id = t2.price_id(+) and t1.price_list = ''plist100005'' order by t1.sku_id';
      end if;
      execute immediate 'MERGE INTO endeca_export_prices t USING (select distinct sku_id,list_price from dcs_price  where price_list=''plist100004'') t1 on (t.sku_id = t1.sku_id) WHEN MATCHED THEN UPDATE SET t.is_price =t1.list_price';
      execute immediate 'MERGE INTO endeca_export_prices t USING (select distinct sku_id,list_price from dcs_price  where price_list=''plist100003'') t1 on (t.sku_id = t1.sku_id) WHEN MATCHED THEN UPDATE SET t.ca_is_price =t1.list_price';
      execute immediate 'MERGE INTO endeca_export_prices t USING (select distinct sku_id,list_price from dcs_price  where price_list=''plist100002'') t1 on (t.sku_id = t1.sku_id) WHEN MATCHED THEN UPDATE SET t.ca_was_price =t1.list_price';
      endeca_query:='select sku_id,LTRIM(to_char(was_price,999999999999.99)),LTRIM(to_char(case when is_price is null or is_price = '''' then was_price else is_price end,999999999999.99)),LTRIM(to_char(ca_was_price,999999999999.99)),LTRIM(to_char(case when ca_is_price is null or ca_is_price = '''' then ca_was_price else ca_is_price end,999999999999.99)) from endeca_export_prices';
      
      dbms_output.put_line('Export_GS_Prices: '||endeca_query);
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'SKU_ID|WAS_PRICE|IS_PRICE|CA_WAS_PRICE|CA_IS_PRICE' ) 
            into l_rows from dual;
	  execute immediate 'drop index GBL_TMP_IDX1';
      execute immediate 'drop table endeca_export_prices';
	  execute Immediate 'commit';
      dbms_output.put_line('Export_GS_Prices: '||l_rows);
   EXCEPTION
   WHEN OTHERS THEN  -- handles all other errors
	 execute immediate 'drop index GBL_TMP_IDX1';
     execute immediate 'drop table endeca_export_prices';
     execute Immediate 'commit';
  END Export_GS_Prices;

  PROCEDURE Export_GS_Product_Site_Prop(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function 
    endeca_query varchar(2000); -- pkg var to capture the SQL query 
  BEGIN 

    execute immediate 'create global temporary table endeca_export_prod_properties (product_id varchar2(40),site_id varchar2(40), web_offered_flag number(1), disable_flag number(1), product_title varchar2(1000), short_description CLOB, long_description CLOB, price_range_descrip VARCHAR2(254), sku_low_price VARCHAR2(40), sku_high_price VARCHAR2(40),enable_date TIMESTAMP(6) ) ON COMMIT DELETE ROWS';
    execute immediate 'create index GBL_TMP_IDX1 on endeca_export_prod_properties (product_id)';
	execute immediate 'create index GBL_TMP_IDX2 on endeca_export_prod_properties (site_id)';
    if p_feedType = 'PARTIAL' then 
       execute immediate 'insert into endeca_export_prod_properties(product_id,web_offered_flag, disable_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price, enable_date, site_id) select a.product_id, GS_BBB_WEB_OFFERED, GS_BBB_DISABLED, display_name, dbms_lob.substr( long_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price,b.enable_date,''GS_BedBathUS'' from dcs_product a, bbb_product b where a.product_id=b.product_id and a.product_id in (select child_prd_id from dcs_cat_chldprd union all select product_id from bbb_prd_reln where NVL(like_unlike, 0) = 0) and b.last_mod_date > '''|| p_lastModifiedDate ||'''';
	   execute immediate 'insert into endeca_export_prod_properties(product_id, web_offered_flag, disable_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price,enable_date, site_id) select a.product_id, GS_BAB_WEB_OFFERED, GS_BAB_DISABLED, display_name, dbms_lob.substr( long_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price,b.enable_date,''GS_BuyBuyBaby'' from dcs_product a, bbb_product b where a.product_id=b.product_id and a.product_id in (select child_prd_id from dcs_cat_chldprd union all select product_id from bbb_prd_reln where NVL(like_unlike, 0) = 0) and b.last_mod_date > '''|| p_lastModifiedDate ||'''';
	   execute immediate 'insert into endeca_export_prod_properties(product_id,web_offered_flag, disable_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price,enable_date, site_id) select a.product_id, GS_CA_WEB_OFFERED, GS_CA_DISABLED, display_name, dbms_lob.substr( long_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price,b.enable_date,''GS_BedBathCanada'' from dcs_product a, bbb_product b where a.product_id=b.product_id and a.product_id in (select child_prd_id from dcs_cat_chldprd union all select product_id from bbb_prd_reln where NVL(like_unlike, 0) = 0) and b.last_mod_date > '''|| p_lastModifiedDate ||'''';
    else 
        execute immediate 'insert into endeca_export_prod_properties(product_id,web_offered_flag, disable_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price,enable_date, site_id ) select a.product_id, GS_BBB_WEB_OFFERED, GS_BBB_DISABLED, display_name, dbms_lob.substr( long_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price,b.enable_date,''GS_BedBathUS'' from dcs_product a, bbb_product b where a.product_id=b.product_id and a.product_id in (select child_prd_id from dcs_cat_chldprd union all select product_id from bbb_prd_reln where NVL(like_unlike, 0) = 0) ';
		execute immediate 'insert into endeca_export_prod_properties(product_id,web_offered_flag, disable_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price,enable_date, site_id ) select a.product_id, GS_BAB_WEB_OFFERED, GS_BAB_DISABLED, display_name, dbms_lob.substr( long_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price,b.enable_date,''GS_BuyBuyBaby'' from dcs_product a, bbb_product b where a.product_id=b.product_id and a.product_id in (select child_prd_id from dcs_cat_chldprd union all select product_id from bbb_prd_reln where NVL(like_unlike, 0) = 0) ';
		execute immediate 'insert into endeca_export_prod_properties(product_id,web_offered_flag, disable_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price,enable_date, site_id ) select a.product_id, GS_CA_WEB_OFFERED, GS_CA_DISABLED, display_name, dbms_lob.substr( long_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price,b.enable_date,''GS_BedBathCanada'' from dcs_product a, bbb_product b where a.product_id=b.product_id and a.product_id in (select child_prd_id from dcs_cat_chldprd union all select product_id from bbb_prd_reln where NVL(like_unlike, 0) = 0) ';
    end if; 

	--Web CA Site
	execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''displayName'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.product_title =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.product_title)'; 
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''description'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.short_description =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.short_description)';
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t2.product_id,t1.attribute_value_clob from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''longDescription'' and t1.attribute_value_clob is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.long_description =NVL(replace(tt1.attribute_value_clob,''"'',''&quot;''), t.long_description)';
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''skuLowPrice'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.sku_low_price =NVL(tt1.attribute_value_string, t.sku_low_price)';
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''skuHighPrice'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.sku_high_price =NVL(tt1.attribute_value_string, t.sku_high_price)';
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''priceRangeDescrip'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.price_range_descrip =NVL(tt1.attribute_value_string, t.price_range_descrip)';
	execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t2.product_id,t1.attribute_value_date from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''enableDate'' and t1.attribute_value_date is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.enable_date =NVL(tt1.attribute_value_date, t.enable_date)';
	--Web Baby Site
	execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''displayName'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.product_title =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.product_title)'; 
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''description'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.short_description =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.short_description)';
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t2.product_id,t1.attribute_value_clob from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''longDescription'' and t1.attribute_value_clob is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.long_description =NVL(replace(tt1.attribute_value_clob,''"'',''&quot;''), t.long_description)';
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''skuLowPrice'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.sku_low_price =NVL(tt1.attribute_value_string, t.sku_low_price)';
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''skuHighPrice'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.sku_high_price =NVL(tt1.attribute_value_string, t.sku_high_price)';
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''priceRangeDescrip'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.price_range_descrip =NVL(tt1.attribute_value_string, t.price_range_descrip)';
	execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t2.product_id,t1.attribute_value_date from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''enableDate'' and t1.attribute_value_date is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.enable_date =NVL(tt1.attribute_value_date, t.enable_date)';
	
	--GS Sites
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t1.site_id,t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id   and t1.attribute_name = ''displayName'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.product_title =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.product_title)'; 
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t1.site_id,t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id   and t1.attribute_name = ''description'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.short_description =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.short_description)';
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t1.site_id,t2.product_id,t1.attribute_value_clob from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id   and t1.attribute_name = ''longDescription'' and t1.attribute_value_clob is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.long_description =NVL(replace(tt1.attribute_value_clob,''"'',''&quot;''), t.long_description)';
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t1.site_id,t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id   and t1.attribute_name = ''skuLowPrice'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.sku_low_price =NVL(tt1.attribute_value_string, t.sku_low_price)';
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t1.site_id,t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id   and t1.attribute_name = ''skuHighPrice'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.sku_high_price =NVL(tt1.attribute_value_string, t.sku_high_price)';
    execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t1.site_id,t2.product_id,t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t2.translation_id=t1.translation_id   and t1.attribute_name = ''priceRangeDescrip'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.price_range_descrip =NVL(tt1.attribute_value_string, t.price_range_descrip)';
	
	--GS Enable Dates
	execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t1.product_id,t1.gs_bbb_enable_date from bbb_product t1
	where t1.gs_bbb_enable_date is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathUS'') WHEN MATCHED THEN UPDATE SET t.enable_date =NVL(tt1.gs_bbb_enable_date, t.enable_date)';
	execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t1.product_id,t1.gs_bab_enable_date from bbb_product t1
	where t1.gs_bab_enable_date is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.enable_date =NVL(tt1.gs_bab_enable_date, t.enable_date)';
	execute immediate 'MERGE INTO endeca_export_prod_properties t USING (select t1.product_id,t1.gs_ca_enable_date from bbb_product t1
	where t1.gs_ca_enable_date is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.enable_date =NVL(tt1.gs_ca_enable_date, t.enable_date)';

    execute immediate 'delete from endeca_export_prod_properties where product_id in ( select distinct e1.product_id from endeca_export_prod_properties e1, bbb_product p1 where p1.product_id = e1.product_id and ( p1.collection_flag = 1 or p1.lead_prd_flag = 1) and e1.product_id not in (  select distinct e.product_id from endeca_export_prod_properties e, bbb_product p, bbb_prd_prd_reln r where p.product_id = e.product_id and p.product_id = r.product_id and e.product_id = r.product_id and ( p.collection_flag = 1 or p.lead_prd_flag = 1) ) )';

    endeca_query:='select distinct ''*#*'',product_id, web_offered_flag, disable_flag, product_title, dbms_lob.substr( short_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price,TO_CHAR (enable_date, ''DD-MON-YY''), site_id from endeca_export_prod_properties order by product_id'; 
     
    select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'NEW_RECORD|PRODUCT_ID|WEB_OFFERED_FLAG|DISABLE_FLAG|PRODUCT_TITLE|SHORT_DESCRIPTION|LONG_DESCRIPTION|PRICE_RANGE_DESCRIP|SKU_LOW_PRICE|SKU_HIGH_PRICE|ENABLE_DATE|SITE_ID' ) into l_rows from dual; 
    execute immediate 'drop index GBL_TMP_IDX1';
	execute immediate 'drop index GBL_TMP_IDX2';
    execute immediate 'drop table endeca_export_prod_properties'; 
    dbms_output.put_line('Export_GS_Product_Site_Prop: '||l_rows); 
    EXCEPTION
    WHEN OTHERS THEN  -- handles all other errors
        dbms_output.put_line('Export_GS_Product_Site_Prop: Error!'); 
        execute immediate 'drop index GBL_TMP_IDX1';
		execute immediate 'drop index GBL_TMP_IDX2';
        execute immediate 'drop table endeca_export_prod_properties';
        execute Immediate 'commit';
  END Export_GS_Product_Site_Prop;

  PROCEDURE Export_GS_SKU_Site_Properties ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2) 
  IS 
    l_rows number; -- pkg var to capture the number of rows affected from the function 
    endeca_query varchar(2000); -- pkg var to capture the SQL query 
  BEGIN 
      execute immediate 'create global temporary table endeca_expt_sku_site_propts (sku_id varchar2(40),site_id varchar2(40), web_offered_flag number(1), disable_flag number(1), sku_title varchar2(1000), short_description varchar2(1000),last_mod_date TIMESTAMP(6)) ON COMMIT DELETE ROWS'; 
      execute immediate 'create index GBL_TMP_IDX3 on endeca_expt_sku_site_propts (sku_id)'; 
	  execute immediate 'create index GBL_TMP_IDX4 on endeca_expt_sku_site_propts (site_id)'; 
      if p_feedType = 'PARTIAL' then
          execute immediate 'insert into endeca_expt_sku_site_propts (sku_id, web_offered_flag, disable_flag,sku_title, short_description, site_id,last_mod_date) select bbb_sku.sku_id, bbb_sku.GS_BBB_WEB_OFFERED, bbb_sku.GS_BBB_DISABLED, dcs_sku.display_name, dcs_sku.description,''GS_BedBathUS'',bbb_sku.last_mod_date from bbb_sku,dcs_sku where bbb_sku.sku_id=dcs_sku.sku_id and bbb_sku.last_mod_date > '''|| p_lastModifiedDate ||'''';
		  execute immediate 'insert into endeca_expt_sku_site_propts (sku_id, web_offered_flag, disable_flag,sku_title, short_description, site_id,last_mod_date) select bbb_sku.sku_id, bbb_sku.GS_BAB_WEB_OFFERED, bbb_sku.GS_BAB_DISABLED, dcs_sku.display_name, dcs_sku.description,''GS_BuyBuyBaby'',bbb_sku.last_mod_date from bbb_sku,dcs_sku where bbb_sku.sku_id=dcs_sku.sku_id and bbb_sku.last_mod_date > '''|| p_lastModifiedDate ||'''';
		  execute immediate 'insert into endeca_expt_sku_site_propts (sku_id, web_offered_flag, disable_flag,sku_title, short_description, site_id,last_mod_date) select bbb_sku.sku_id, bbb_sku.GS_CA_WEB_OFFERED, bbb_sku.GS_CA_DISABLED, dcs_sku.display_name, dcs_sku.description,''GS_BedBathCanada'',bbb_sku.last_mod_date from bbb_sku,dcs_sku where bbb_sku.sku_id=dcs_sku.sku_id and bbb_sku.last_mod_date > '''|| p_lastModifiedDate ||'''';
      else
          execute immediate 'insert into endeca_expt_sku_site_propts (sku_id, web_offered_flag, disable_flag,sku_title, short_description, site_id,last_mod_date) select bbb_sku.sku_id, bbb_sku.GS_BBB_WEB_OFFERED, bbb_sku.GS_BBB_DISABLED, dcs_sku.display_name, dcs_sku.description,''GS_BedBathUS'',bbb_sku.last_mod_date from bbb_sku,dcs_sku where bbb_sku.sku_id=dcs_sku.sku_id';
			execute immediate 'insert into endeca_expt_sku_site_propts (sku_id, web_offered_flag, disable_flag,sku_title, short_description, site_id,last_mod_date) select bbb_sku.sku_id, bbb_sku.GS_BAB_WEB_OFFERED, bbb_sku.GS_BAB_DISABLED, dcs_sku.display_name, dcs_sku.description,''GS_BuyBuyBaby'',bbb_sku.last_mod_date from bbb_sku,dcs_sku where bbb_sku.sku_id=dcs_sku.sku_id';
			execute immediate 'insert into endeca_expt_sku_site_propts (sku_id, web_offered_flag, disable_flag,sku_title, short_description, site_id,last_mod_date) select bbb_sku.sku_id, bbb_sku.GS_CA_WEB_OFFERED, bbb_sku.GS_CA_DISABLED, dcs_sku.display_name, dcs_sku.description,''GS_BedBathCanada'',bbb_sku.last_mod_date from bbb_sku,dcs_sku where bbb_sku.sku_id=dcs_sku.sku_id';
      end if;
      --Web Baby
	  execute immediate 'MERGE INTO endeca_expt_sku_site_propts t USING (select t2.sku_id,t1.attribute_value_string from bbb_sku_site_translations t2, bbb_sku_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''displayName'' and t1.attribute_value_string is not null) tt1 on (t.sku_id=tt1.sku_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.sku_title =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.sku_title)';
      execute immediate 'MERGE INTO endeca_expt_sku_site_propts t USING (select t2.sku_id,t1.attribute_value_string from bbb_sku_site_translations t2, bbb_sku_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''description'' and t1.attribute_value_string is not null) tt1 on (t.sku_id=tt1.sku_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.short_description =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.short_description)'; 
	  --Web CA
	  execute immediate 'MERGE INTO endeca_expt_sku_site_propts t USING (select t2.sku_id,t1.attribute_value_string from bbb_sku_site_translations t2, bbb_sku_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''displayName'' and t1.attribute_value_string is not null) tt1 on (t.sku_id=tt1.sku_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.sku_title =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.sku_title)';
      execute immediate 'MERGE INTO endeca_expt_sku_site_propts t USING (select t2.sku_id,t1.attribute_value_string from bbb_sku_site_translations t2, bbb_sku_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''description'' and t1.attribute_value_string is not null) tt1 on (t.sku_id=tt1.sku_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.short_description =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.short_description)';
	  
	  --GS properties
      execute immediate 'MERGE INTO endeca_expt_sku_site_propts t USING (select t1.site_id,t2.sku_id,t1.attribute_value_string from bbb_sku_site_translations t2, bbb_sku_translations t1 where t2.translation_id=t1.translation_id  and t1.attribute_name = ''displayName'' and t1.attribute_value_string is not null) tt1 on (t.sku_id=tt1.sku_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.sku_title =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.sku_title)';
      execute immediate 'MERGE INTO endeca_expt_sku_site_propts t USING (select t1.site_id,t2.sku_id,t1.attribute_value_string from bbb_sku_site_translations t2, bbb_sku_translations t1 where t2.translation_id=t1.translation_id and t1.attribute_name = ''description'' and t1.attribute_value_string is not null) tt1 on (t.sku_id=tt1.sku_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.short_description =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.short_description)';
	        
      endeca_query:='select ''*#*'',sku_id, web_offered_flag,disable_flag, sku_title, short_description, site_id from endeca_expt_sku_site_propts order by sku_id'; 

      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'NEW_RECORD|SKU_ID|WEB_OFFERED_FLAG|DISABLE_FLAG|SKU_TITLE|SHORT_DESCRIPTION|SITE_ID' ) into l_rows from dual; 
      execute immediate 'drop index GBL_TMP_IDX3';
	  execute immediate 'drop index GBL_TMP_IDX4';
      execute immediate 'drop table endeca_expt_sku_site_propts'; 
      dbms_output.put_line('Export_GS_SKU_Site_Properties: '||l_rows); 
      EXCEPTION
      WHEN OTHERS THEN  -- handles all other errors
    dbms_output.put_line('Export_GS_SKU_Site_Properties: Error!');
        execute immediate 'drop index GBL_TMP_IDX3';
		execute immediate 'drop index GBL_TMP_IDX4';
        execute immediate 'drop table endeca_expt_sku_site_propts';
        execute immediate 'commit';
  END Export_GS_SKU_Site_Properties;
  
-- <Begin> STOFU PROC

 PROCEDURE Export_GS_BBB_Channel_Category ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='SELECT distinct channel.channel_id, a.child_cat_id,b.display_name FROM dcs_cat_chldcat a, dcs_category b, (select d.category_id as category_id, a.channel_id as channel_id from BBB_CHANNEL_INFO a, BBB_GS_CHAN_CATS b, BBB_GS_CATEGORY_CONTNR c, BBB_GS_CONT_CATS d where a.channel_id=b.channel_id and b.cat_container_id=c.cat_container_id and c.cat_container_id=d.cat_container_id and b.site_id=''GS_BedBathUS'') channel where CONNECT_BY_ISLEAF=1 and a.child_cat_id=b.category_id START WITH a.category_id = channel.category_id CONNECT BY PRIOR a.child_cat_id = a.category_id';
       dbms_output.put_line('Export_GS_BBB_Channel_Category: '||endeca_query);     
     
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'FFNAME|CATEGORYID|CATEGORYNAME' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_BBB_Channel_Category: '||l_rows);
  END Export_GS_BBB_Channel_Category;
  
  PROCEDURE Export_GS_BAB_Channel_Category ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='SELECT distinct channel.channel_id, a.child_cat_id,b.display_name FROM dcs_cat_chldcat a, dcs_category b, (select d.category_id as category_id, a.channel_id as channel_id from BBB_CHANNEL_INFO a, BBB_GS_CHAN_CATS b, BBB_GS_CATEGORY_CONTNR c, BBB_GS_CONT_CATS d where a.channel_id=b.channel_id and b.cat_container_id=c.cat_container_id and c.cat_container_id=d.cat_container_id and b.site_id=''GS_BuyBuyBaby'') channel where CONNECT_BY_ISLEAF=1 and a.child_cat_id=b.category_id START WITH a.category_id = channel.category_id CONNECT BY PRIOR a.child_cat_id = a.category_id';
       dbms_output.put_line('Export_GS_BAB_Channel_Category: '||endeca_query);     
     
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'FFNAME|CATEGORYID|CATEGORYNAME' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_BAB_Channel_Category: '||l_rows);
  END Export_GS_BAB_Channel_Category;
  
  PROCEDURE Export_GS_CA_Channel_Category ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='SELECT distinct channel.channel_id, a.child_cat_id,b.display_name FROM dcs_cat_chldcat a, dcs_category b, (select d.category_id as category_id, a.channel_id as channel_id from BBB_CHANNEL_INFO a, BBB_GS_CHAN_CATS b, BBB_GS_CATEGORY_CONTNR c, BBB_GS_CONT_CATS d where a.channel_id=b.channel_id and b.cat_container_id=c.cat_container_id and c.cat_container_id=d.cat_container_id and b.site_id=''GS_BedBathCanada'') channel where CONNECT_BY_ISLEAF=1 and a.child_cat_id=b.category_id START WITH a.category_id = channel.category_id CONNECT BY PRIOR a.child_cat_id = a.category_id';
       dbms_output.put_line('Export_GS_CA_Channel_Category: '||endeca_query);     
     
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'FFNAME|CATEGORYID|CATEGORYNAME' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_CA_Channel_Category: '||l_rows);
  END Export_GS_CA_Channel_Category;
  
  PROCEDURE Export_GS_BBB_Taxonomy ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
            from dcs_category a, bbb_category b, dcs_cat_chldcat c where a.category_id = b.category_id 
            and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''4%'' and c.category_id like ''4%''
            order by a.category_id';
       dbms_output.put_line('Export_GS_BBB_Taxonomy: '||endeca_query);     
     if p_feedType = 'PARTIAL' then
          endeca_query := 'select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
            from dcs_category a, bbb_category b, dcs_cat_chldcat c where a.category_id = b.category_id 
            and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''4%'' and c.category_id like ''4%''
            and b.last_mod_date > '''|| p_lastModifiedDate ||''' order by a.category_id' ;
         end if;
      dbms_output.put_line('Export_GS_BBB_Taxonomy: '||endeca_query);  

      select dump_csv( endeca_query, '|', p_feedType, p_filename,'40000||GS Bed Bath & Beyond||||', 'CHILD_CAT_ID|CATEGORY_ID|DISPLAY_NAME|IS_COLLEGE|NODE_TYPE|PHANTOM_CATEGORY|GUIDE_ID') into l_rows from dual;
      dbms_output.put_line('Export_GS_BBB_Taxonomy: '||l_rows);
  END Export_GS_BBB_Taxonomy;
  
  PROCEDURE Export_GS_BAB_Taxonomy ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
            from dcs_category a, bbb_category b, dcs_cat_chldcat c where a.category_id = b.category_id 
            and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''6%'' and c.category_id like ''6%''
            order by a.category_id';
       dbms_output.put_line('Export_GS_BAB_Taxonomy: '||endeca_query);     
     if p_feedType = 'PARTIAL' then
          endeca_query := 'select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
            from dcs_category a, bbb_category b, dcs_cat_chldcat c where a.category_id = b.category_id 
            and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''6%'' and c.category_id like ''6%''
            and b.last_mod_date > '''|| p_lastModifiedDate ||''' order by a.category_id' ;
         end if;
      dbms_output.put_line('Export_GS_BAB_Taxonomy: '||endeca_query);  

      select dump_csv( endeca_query, '|', p_feedType, p_filename, '60000||GS Buy Buy Baby||||', 'CHILD_CAT_ID|CATEGORY_ID|DISPLAY_NAME|IS_COLLEGE|NODE_TYPE|PHANTOM_CATEGORY|GUIDE_ID' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_BAB_Taxonomy: '||l_rows);
  END Export_GS_BAB_Taxonomy;
  
  PROCEDURE Export_GS_CA_Taxonomy ( p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
  BEGIN
      endeca_query:='select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
            from dcs_category a, bbb_category b, dcs_cat_chldcat c where a.category_id = b.category_id 
            and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''5%'' and c.category_id like ''5%''
            order by a.category_id';
       dbms_output.put_line('Export_GS_CA_Taxonomy: '||endeca_query);     
     if p_feedType = 'PARTIAL' then
          endeca_query := 'select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
            from dcs_category a, bbb_category b, dcs_cat_chldcat c where a.category_id = b.category_id 
            and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''5%'' and c.category_id like ''5%''
            and b.last_mod_date > '''|| p_lastModifiedDate ||''' order by a.category_id' ;
         end if;
      dbms_output.put_line('Export_GS_CA_Taxonomy: '||endeca_query);  

      select dump_csv( endeca_query, '|', p_feedType, p_filename,'50000||GS Bed Bath Canada||||', 'CHILD_CAT_ID|CATEGORY_ID|DISPLAY_NAME|IS_COLLEGE|NODE_TYPE|PHANTOM_CATEGORY|GUIDE_ID' ) into l_rows from dual;
      dbms_output.put_line('Export_GS_CA_Taxonomy: '||l_rows);
  END Export_GS_CA_Taxonomy;

-- <End> STOFU PROC  

  PROCEDURE Export_GS_GoFile(p_filename varchar2, p_lastModifiedDate timestamp, p_feedType varchar2)
  IS
    l_rows number;
    endeca_query varchar(2000);
  BEGIN
        endeca_query:='select sysdate from dual';
        if p_feedType = 'PARTIAL' then 
            select dump_csv( endeca_query, '|', p_feedType, 'partial.go', null, 'SYSDATE' ) into l_rows from dual;
        else
            select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'SYSDATE' ) into l_rows from dual; 
        end if;
        dbms_output.put_line(p_filename || l_rows);
  END Export_GS_GoFile;

  
  FUNCTION  dump_csv( p_query        in varchar2,
                    p_separator in varchar2 default '|',
                    p_dir       in varchar2 ,
                    p_filename  in varchar2,
                    p_static    in varchar2,
                    p_header in varchar2)
  RETURN number
  is
    l_output        utl_file.file_type;
    l_theCursor     integer default dbms_sql.open_cursor;
    l_columnValue   varchar2(2000);
    l_status        integer;
    l_colCnt        number default 0;
    l_separator     varchar2(10) default '';
    l_cnt           number default 0;
    l_dir           varchar2(20);
BEGIN

    IF p_dir = 'PARTIAL' 
        then l_dir:= 'ATG_APP_PARTIAL_GS';
    ELSE 
        l_dir:= 'ATG_APP_FULL_GS';
    END IF;
    
    dbms_sql.parse(  l_theCursor,  p_query, dbms_sql.native );

    l_output := utl_file.fopen( l_dir, p_filename, 'w', 32767 );

    utl_file.put( l_output, p_header );
    utl_file.new_line( l_output );

    if ( p_static is not null ) then
        utl_file.put( l_output, p_static );
        utl_file.new_line( l_output );
    end if;
    
    for i in 1 .. 255 loop
        begin
            dbms_sql.define_column( l_theCursor, i, l_columnValue, 2000 );
            l_colCnt := i;
            exception
            when others then
                if ( sqlcode = -1007 ) then exit;
                else raise;
                end if;
        end;
    end loop;
  
    dbms_sql.define_column( l_theCursor, 1, l_columnValue, 2000 );

    l_status := dbms_sql.execute(l_theCursor);
    
    if  (dbms_sql.fetch_rows(l_theCursor) > 0 ) then
    
        loop
            l_separator := '';
            for i in 1 .. l_colCnt loop
                dbms_sql.column_value( l_theCursor, i, l_columnValue );
                utl_file.put( l_output, l_separator || l_columnValue );
                l_separator := p_separator;
            end loop;
            utl_file.new_line( l_output );
            l_cnt := l_cnt+1;
            exit when ( dbms_sql.fetch_rows(l_theCursor) <= 0 );
        end loop;
        dbms_sql.close_cursor(l_theCursor);
    end if;

    utl_file.fclose( l_output );
 
    return l_cnt;
end dump_csv;
END ATG_GS_EndecaExport_Pkg;
/

show error


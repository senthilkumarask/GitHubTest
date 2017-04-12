--  DDL for Package Body ATG_GS_ENDECAEXPORT_PKG
-- Build version: 2.05.02.001 PSI5 
SET ECHO ON;
SET DEFINE OFF;
SET SERVEROUTPUT ON;

CREATE OR REPLACE PACKAGE BODY BBB_SWITCH_B.ATG_GS_ENDECAEXPORT_PKG 
	AS
	-- Log Message SP
	PROCEDURE GS_Log_message(p_process_cd VARCHAR2, p_process_sub_cd VARCHAR2, p_message VARCHAR2, p_row_status CHAR)
	IS
		v_schema VARCHAR2(30); --row_xng_usr
		nextVal number(14);
	BEGIN
		SELECT BBB_PIM_STG.PROCESS_SUPPORT_SEQ.NEXTVAL INTO nextVal FROM dual;
		SELECT sys_context('userenv','current_schema') INTO v_schema FROM dual;
		INSERT INTO BBB_PIM_STG.PROCESS_SUPPORT ( ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS )
		(select nextVal, p_process_cd, p_process_sub_cd, RTRIM(SUBSTR( v_schema ||' : ' || p_message,1,512)) , SYSDATE, 'PIM' , p_row_status  from dual);
		commit;
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('GS_Log_message: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
	END GS_Log_message;

	PROCEDURE Export_GS_ColorColorGroups ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_Color';
		endeca_query :='SELECT COLOR_GRP_ID, COLOR_CD FROM BBB_CORE.BBB_COLOR_COLOR_GROUP';
		v_message :='Export_GS_ColorColorGroups STARTS: '||endeca_query;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		dbms_output.put_line('Export_GS_ColorColorGroups: '||endeca_query);
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'COLOR_GRP_ID|COLOR_CD' ) INTO l_rows FROM dual;
		dbms_output.put_line('Export_GS_ColorColorGroups: Total rows:'||l_rows);
		v_message :='Export_GS_ColorColorGroups ENDS: rows returned'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_ColorColorGroups: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_ColorColorGroups: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_ColorColorGroups;

	PROCEDURE Export_GS_Features ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_Featr';
		endeca_query :='SELECT FACET_ID,DESCRIPTION from BBB_CORE.BBB_FACET_TYPES';
		v_message :='Export_GS_Features: STARTS: '||endeca_query;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'ID|FEATURE_NAME' ) INTO l_rows FROM dual;
		dbms_output.put_line('Export_GS_Features: Total Rows: '||l_rows);
		v_message :='Export_GS_Features: ENDS: Total rows returned '||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_Features: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_Features: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_Features;
  
	PROCEDURE Export_GS_SKU_Features ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GSSkuFtr';
		
		endeca_query :='SELECT a.ITEM_ID, a.FACET_ID, a.FACET_VALUE, a.FACET_VALUE_ID from BBB_CORE.BBB_FACET_SKU a, BBB_SWITCH_B.bbb_sku b where a.item_id = b.sku_id and ((b.GS_BBB_WEB_OFFERED = ''1'' and  b.GS_BBB_DISABLED = ''0'') OR (b.GS_CA_WEB_OFFERED = ''1'' and b.GS_CA_DISABLED = ''0'') OR (b.GS_BAB_WEB_OFFERED = ''1'' and b.GS_BAB_DISABLED = ''0''))';
		v_message :='Export_GS_SKU_Features: STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'SKU_ID|FEATURE_ID|FEATURE_VALUE|FEATURE_VALUE_ID' ) INTO l_rows FROM dual;
		dbms_output.put_line('Export_GS_SKU_Features: Total Rows: '||l_rows);
		
		v_message :='Export_GS_SKU_Features: ENDS: Total rows returned:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_SKU_Features: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_SKU_Features: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_SKU_Features;

	PROCEDURE Export_GS_Reviews ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
		v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_Rvws'; 
		v_message :='Export_GS_Reviews: STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		endeca_query:='SELECT a.PRODUCT_ID,a.AVERAGE_OVERALL_RATING,a.TOTAL_REVIEW_COUNT FROM BBB_CORE.BBB_BAZAAR_VOICE a, BBB_SWITCH_B.BBB_PRODUCT b where a.product_id = b.product_id and ((b.GS_BBB_WEB_OFFERED = ''1'' and  b.GS_BBB_DISABLED = ''0'') OR (b.GS_CA_WEB_OFFERED = ''1'' and b.GS_CA_DISABLED = ''0'') OR (b.GS_BAB_WEB_OFFERED = ''1'' and b.GS_BAB_DISABLED = ''0''))';
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'PRODUCT_ID|AVERAGE_OVERALL_RATING|TOTAL_REVIEW_COUNT' ) INTO l_rows FROM dual;
		dbms_output.put_line('Export_GS_Reviews: '||l_rows);
		v_message :='Export_GS_Reviews: ENDS: Total rows returned: '||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_Reviews: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_Reviews: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_Reviews;

  	PROCEDURE Export_GS_SKUs ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
		nCount NUMBER;
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_SKUs';   
		v_message :='Export_GS_SKUs: STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

		SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_EXPT_GS_SKUS';
		IF(nCount > 0)THEN
			EXECUTE immediate 'truncate table BBB_SWITCH_B.ENDECA_EXPT_GS_SKUS ';   
		END IF;

		execute immediate 'insert into BBB_SWITCH_B.endeca_expt_GS_SKUs (sku_id) select b.sku_id from BBB_SWITCH_B.bbb_sku a, BBB_SWITCH_B.dcs_sku b where a.sku_id=b.sku_id and ((a.GS_BBB_WEB_OFFERED = ''1'' and  a.GS_BBB_DISABLED = ''0'') OR (a.GS_CA_WEB_OFFERED = ''1'' and a.GS_CA_DISABLED = ''0'') OR (a.GS_BAB_WEB_OFFERED = ''1'' and a.GS_BAB_DISABLED = ''0''))';
		execute immediate 'update BBB_SWITCH_B.endeca_expt_GS_SKUs t set t.gsPLPImage = (select t1.scene7_url from BBB_SWITCH_B.BBB_GS_SKU_IMAGES t1 where t.sku_id=t1.sku_id and t1.SHOT_TYPE = ''GSC''), t.gsPDPImage = (select t2.scene7_url from BBB_SWITCH_B.BBB_GS_SKU_IMAGES t2 where t.sku_id=t2.sku_id and t2.SHOT_TYPE = ''GSP'')';

		endeca_query:='select ''*#*'',a.sku_id, replace(a.display_name,''"'',''&quot;''), replace(a.description,''"'',''&quot;''), 
			case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
			case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,
			case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, 
			case when NVL(e.gsPLPImage,b.scene7_url) is not null then NVL(e.gsPLPImage,b.scene7_url)||''?$146$'' else '''' end as medium_image_id,
			d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id,
			d.zoom_index, d.anywhere_zoom, b.jda_dept_id, b.jda_sub_dept_id, b.jda_class, b.gift_cert_flag, b.college_id, b.sku_type,b.email_out_of_stock,b.color,b.color_group,replace(b.sku_size,''"'',''&quot;''),b.gift_wrap_eligible,b.vdc_sku_type,b.vdc_sku_message,b.upc,
			case when NVL(e.gsPDPImage,b.scene7_url) is not null then NVL(e.gsPDPImage,b.scene7_url)||''?$146v$'' else '''' end as vert_image_id, case when e.gsPLPImage is not null then e.gsPLPImage else b.scene7_url end as gsPLPImage
			from BBB_SWITCH_B.dcs_sku a, BBB_SWITCH_B.bbb_sku b, BBB_SWITCH_B.dcs_sku_media c, BBB_SWITCH_B.bbb_sku_media d, BBB_SWITCH_B.endeca_expt_GS_SKUs e
			where a.sku_id=e.sku_id and a.sku_id=b.sku_id and a.sku_id=c.sku_id(+) and a.sku_id=d.sku_id(+) ';

		IF p_feedType = 'PARTIAL' THEN
			endeca_query := 'select distinct ''*#*'',a.sku_id, replace(a.display_name,''"'',''&quot;''), replace(a.description,''"'',''&quot;''), 
				case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
				case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,
				case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, 
				case when NVL(e.gsPLPImage,b.scene7_url) is not null then NVL(e.gsPLPImage,b.scene7_url)||''?$146$'' else '''' end as medium_image_id,
				d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id,
				d.zoom_index, d.anywhere_zoom, b.jda_dept_id, b.jda_sub_dept_id, b.jda_class, b.gift_cert_flag, b.college_id, b.sku_type,b.email_out_of_stock,b.color,b.color_group,replace(b.sku_size,''"'',''&quot;''),b.gift_wrap_eligible,b.vdc_sku_type,b.vdc_sku_message,b.upc,
				case when NVL(e.gsPDPImage,b.scene7_url) is not null then NVL(e.gsPDPImage,b.scene7_url)||''?$146v$'' else '''' end as vert_image_id, case when e.gsPLPImage is not null then e.gsPLPImage else b.scene7_url end as gsPLPImage
				from BBB_SWITCH_B.dcs_sku a, BBB_SWITCH_B.bbb_sku b, BBB_SWITCH_B.dcs_sku_media c, BBB_SWITCH_B.bbb_sku_media d, BBB_SWITCH_B.dcs_price i, BBB_SWITCH_B.bbb_price j, BBB_SWITCH_B.endeca_expt_GS_SKUs e
				where a.sku_id=b.sku_id and a.sku_id=e.sku_id and a.sku_id=c.sku_id(+) and a.sku_id=d.sku_id(+) and a.sku_id = i.sku_id and i.price_id = j.price_id
				and ( b.last_mod_date > '''|| p_lastModifiedDate ||''' 
				or j.last_mod_date > '''|| p_lastModifiedDate ||''' )' ;
		END IF;

		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|SKU_ID|DISPLAY_NAME|DESCRIPTION|THUMBNAIL_IMAGE_ID|SMALL_IMAGE_ID|LARGE_IMAGE_ID|MEDIUM_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|ANYWHERE_ZOOM|JDA_DEPT_ID|JDA_SUB_DEPT_ID|JDA_CLASS|GIFT_CERT_FLAG|COLLEGE_ID|SKU_TYPE|EMAIL_OUT_OF_STOCK|COLOR|COLOR_GROUP|SKU_SIZE|GIFT_WRAP_ELIGIBLE|VDC_SKU_TYPE|VDC_SKU_MESSAGE|UPC|VERT_IMAGE_ID|GSPLPIMAGE' ) INTO l_rows FROM dual;
		dbms_output.put_line('Export_GS_SKUs: '||l_rows);
		v_message :='Export_GS_SKUs: ENDS : Total rows returned: '||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_SKUs: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_SKUs: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_SKUs;
	
	PROCEDURE Export_GS_Product_SKUs ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
		nCount number;
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_PRSK'; 
		v_message :='Export_GS_Product_SKUs: STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		
		endeca_query:='select a.product_id,a.sku_id,a.sequence_num from BBB_SWITCH_B.dcs_prd_chldsku a, BBB_SWITCH_B.BBB_SKU s where a.sku_id=s.sku_id and ((s.GS_BBB_WEB_OFFERED = ''1'' and  s.GS_BBB_DISABLED = ''0'') OR (s.GS_CA_WEB_OFFERED = ''1'' and s.GS_CA_DISABLED = ''0'') OR (s.GS_BAB_WEB_OFFERED = ''1'' and s.GS_BAB_DISABLED = ''0'')) and a.product_id in (select product_id from BBB_SWITCH_B.bbb_product where product_id not in (select product_id from BBB_SWITCH_B.bbb_prd_reln where like_unlike=1) union select child_prd_id from BBB_SWITCH_B.dcs_cat_chldprd) order by a.product_id,a.sequence_num';
		if p_feedType = 'PARTIAL' then
		-- endeca_query:='select a.product_id,a.sku_id,a.sequence_num from dcs_prd_chldsku a,BBB_PRODUCT b where a.product_id = b.product_id and b.last_mod_date > '''|| p_lastModifiedDate ||''' and a.product_id in (select product_id from bbb_product where product_id not in (select product_id from bbb_prd_reln where like_unlike=1))';
		endeca_query:='select distinct a.product_id,a.sku_id,a.sequence_num from BBB_SWITCH_B.dcs_prd_chldsku a, BBB_SWITCH_B.BBB_PRODUCT b, BBB_SWITCH_B.BBB_SKU s where a.product_id = b.product_id and a.sku_id=s.sku_id and s.web_offered_flag =''1'' and s.disable_flag =''0'' and b.last_mod_date > '''|| p_lastModifiedDate ||''' and a.product_id in ( select product_id from BBB_SWITCH_B.bbb_product where product_id not in ( select product_id from BBB_SWITCH_B.bbb_prd_reln where like_unlike=1 ) ) order by product_id,sequence_num';
		end if;
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'PRODUCT_ID|SKU_ID|SEQUENCE_NUM' ) INTO l_rows FROM dual;
		dbms_output.put_line('Export_GS_Product_SKUs: '||l_rows); 
		v_message :='Export_GS_Product_SKUs : ENDS: Total rows returned:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		COMMIT;
	EXCEPTION
		WHEN OTHERS THEN  -- handles all other errors
			dbms_output.put_line('Export_GS_Product_SKUs: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_Product_SKUs: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
			COMMIT;
	END Export_GS_Product_SKUs;

	PROCEDURE Export_GS_Accessories ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
		nCount NUMBER;
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;  	  
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_PAcc'; 
		v_message :='Export_GS_Accessories: STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

		SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_EXPT_GS_ACCESSORIES';
		IF(nCount > 0)THEN
			EXECUTE immediate 'truncate table BBB_SWITCH_B.ENDECA_EXPT_GS_ACCESSORIES ';   
		END IF;
		
		execute immediate 'insert into BBB_SWITCH_B.endeca_expt_GS_Accessories (product_id) select a.product_id from BBB_SWITCH_B.bbb_product b,BBB_SWITCH_B.dcs_product a where a.product_id=b.product_id and ((b.GS_BBB_WEB_OFFERED = ''1'' and  b.GS_BBB_DISABLED = ''0'') OR (b.GS_CA_WEB_OFFERED = ''1'' and b.GS_CA_DISABLED = ''0'') OR (b.GS_BAB_WEB_OFFERED = ''1'' and b.GS_BAB_DISABLED = ''0''))';
		execute immediate 'update BBB_SWITCH_B.endeca_expt_GS_Accessories t set t.gsPLPImage = (select t1.scene7_url from BBB_SWITCH_B.BBB_GS_PRODUCT_IMAGES t1 where t.product_id=t1.product_id and t1.SHOT_TYPE = ''GSC''), t.gsPDPImage = (select t2.scene7_url from BBB_SWITCH_B.BBB_GS_PRODUCT_IMAGES t2 where t.product_id=t2.product_id and t2.SHOT_TYPE = ''GSP'')';
        endeca_query:='select distinct ''*#*'',a.product_id,gift_cert,collection_flag,swatch_flag,brand_id,b.email_out_of_stock,
            show_images_in_collection,f.school_name,rollup_type_code,
            case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id, 
            case when NVL(e.gsPLPImage,b.scene7_url) is not null then NVL(e.gsPLPImage,b.scene7_url)||''?$146$'' else '''' end as medium_image_id,
            case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
            case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id,
            d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index, 
            case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail,
            anywhere_zoom,case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end,
            case when NVL(e.gsPDPImage,b.scene7_url) is not null then NVL(e.gsPDPImage,b.scene7_url)||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id,case when e.gsPLPImage is not null then e.gsPLPImage else b.scene7_url end as gsPLPImage
            from BBB_SWITCH_B.dcs_product a, BBB_SWITCH_B.bbb_product b, BBB_SWITCH_B.dcs_prd_media c, BBB_SWITCH_B.bbb_prd_media d, BBB_CORE.BBB_SCHOOLS f, BBB_SWITCH_B.endeca_expt_GS_Accessories e
            where a.product_id = e.product_id and a.product_id = b.product_id and a.product_id=c.product_id(+)
            and a.product_id=d.product_id(+) and b.college_id=f.school_id(+)
            and a.product_id in ( select distinct c.product_id from BBB_SWITCH_B.bbb_product a, BBB_SWITCH_B.bbb_prd_prd_reln b, BBB_SWITCH_B.bbb_prd_reln c  where a.product_id=b.product_id
            and b.product_relan_id=c.product_relan_id and a.lead_prd_flag=''1'')';

		IF p_feedType = 'PARTIAL' THEN
			endeca_query := endeca_query || ' and ( b.last_mod_date > '''|| p_lastModifiedDate ||''') ' ;
		END IF;
		dbms_output.put_line('Export_GS_Accessories: '||endeca_query);
		v_message :='Export_GS_Accessories: Query:'||endeca_query;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|VERT_IMAGE_ID|SHOP_GUIDE_ID|GSPLPIMAGE' ) INTO l_rows FROM dual;
		dbms_output.put_line('Export_GS_Accessories:Total Rows returned: '||l_rows);
		v_message :='Export_GS_Accessories: ENDS :Total rows returned:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_Accessories: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_Accessories: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_Accessories;

	PROCEDURE Export_GS_Lead_Products ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
		nCount NUMBER;
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_PLead'; 
		v_message :='Export_GS_Lead_Products: STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

		SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_EXPT_LEAD_PRODUCTS';
		IF(nCount > 0)THEN
			EXECUTE immediate 'truncate table BBB_SWITCH_B.ENDECA_EXPT_LEAD_PRODUCTS ';   
		END IF;

		IF p_feedType = 'PARTIAL' THEN
			execute immediate 'insert into BBB_SWITCH_B.endeca_expt_Lead_Products (product_id) select a.product_id from BBB_SWITCH_B.bbb_product b,BBB_SWITCH_B.dcs_product a where b.product_id=a.product_id and b.last_mod_date > '''|| p_lastModifiedDate ||'''';
		ELSE
			execute immediate 'insert into BBB_SWITCH_B.endeca_expt_Lead_Products (product_id) select a.product_id from BBB_SWITCH_B.bbb_product b,BBB_SWITCH_B.dcs_product a where b.product_id=a.product_id and ((b.GS_BBB_WEB_OFFERED = ''1'' and  b.GS_BBB_DISABLED = ''0'') OR (b.GS_CA_WEB_OFFERED = ''1'' and b.GS_CA_DISABLED = ''0'') OR (b.GS_BAB_WEB_OFFERED = ''1'' and b.GS_BAB_DISABLED = ''0''))';
		END IF;
		execute immediate 'update BBB_SWITCH_B.endeca_expt_Lead_Products t set t.gsPLPImage = (select t1.scene7_url from BBB_SWITCH_B.BBB_GS_PRODUCT_IMAGES t1 where t.product_id=t1.product_id and t1.SHOT_TYPE = ''GSC''), t.gsPDPImage = (select t2.scene7_url from BBB_SWITCH_B.BBB_GS_PRODUCT_IMAGES t2 where t.product_id=t2.product_id and t2.SHOT_TYPE = ''GSP'')';

        endeca_query:='select distinct ''*#*'',a.product_id,     gift_cert,collection_flag,swatch_flag,brand_id,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code,
			case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id, 
			case when NVL(e.gsPLPImage,b.scene7_url) is not null then NVL(e.gsPLPImage,b.scene7_url)||''?$146$'' else '''' end as medium_image_id,
			case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
			case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id,
			d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index, 
			case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail,
			anywhere_zoom, case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end,
			case when NVL(e.gsPDPImage,b.scene7_url) is not null then NVL(e.gsPDPImage,b.scene7_url)||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id, case when e.gsPLPImage is not null then e.gsPLPImage else b.scene7_url end as gsPLPImage
			from BBB_SWITCH_B.dcs_product a, BBB_SWITCH_B.bbb_product b, BBB_SWITCH_B.dcs_prd_media c, BBB_SWITCH_B.bbb_prd_media d,BBB_SWITCH_B.endeca_expt_Lead_Products e, BBB_SWITCH_B.bbb_prd_prd_reln r, BBB_CORE.BBB_SCHOOLS f 
			where a.product_id = e.product_id and a.product_id = b.product_id and a.product_id = r.product_id and a.product_id=c.product_id(+) and a.product_id=d.product_id(+) 
			and b.college_id=f.school_id(+) and b.lead_prd_flag=''1'' ';

		IF p_feedType = 'PARTIAL' THEN
			endeca_query:='select distinct ''*#*'',a.product_id, gift_cert,collection_flag,swatch_flag,brand_id,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code, case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,  case when NVL(e.gsPLPImage,b.scene7_url) is not null then NVL(e.gsPLPImage,b.scene7_url)||''?$146$'' else '''' end as medium_image_id, case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index,  case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail, anywhere_zoom, case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end, case when NVL(e.gsPDPImage,b.scene7_url) is not null then NVL(e.gsPDPImage,b.scene7_url)||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id, case when e.gsPLPImage is not null then e.gsPLPImage else b.scene7_url end as gsPLPImage from BBB_SWITCH_B.dcs_product a, BBB_SWITCH_B.bbb_product b,BBB_SWITCH_B.endeca_expt_Lead_Products e, BBB_SWITCH_B.dcs_prd_media c, BBB_SWITCH_B.bbb_prd_media d, BBB_SWITCH_B.bbb_prd_prd_reln r, BBB_CORE.BBB_SCHOOLS f, BBB_SWITCH_B.dcs_prd_chldsku g, BBB_SWITCH_B.bbb_sku h, BBB_SWITCH_B.dcs_cat_chldprd i, BBB_SWITCH_B.bbb_category j where a.product_id = b.product_id and a.product_id = e.product_id and a.product_id = r.product_id and a.product_id=g.product_id(+) and g.sku_id=h.sku_id(+) and a.product_id=c.product_id(+) and a.product_id=d.product_id(+)  and a.product_id=i.child_prd_id (+) and i.category_id = j.category_id and b.college_id=f.school_id(+) and b.lead_prd_flag=''1''  and ( b.last_mod_date > '''|| p_lastModifiedDate ||''' or h.last_mod_date > '''|| p_lastModifiedDate ||'''  or j.last_mod_date > '''|| p_lastModifiedDate ||''' ) ' ;
		END IF;
		dbms_output.put_line('endeca_query: '||endeca_query);
		v_message :='Export_GS_Lead_Products: Query:'||endeca_query;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|VERT_IMAGE_ID|SHOP_GUIDE_ID|GSPLPIMAGE' ) INTO l_rows FROM dual;
		dbms_output.put_line('Export_GS_Lead_Products: '||l_rows);
		v_message :='Export_GS_Lead_Products: ENDS: Total rows returned:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_Lead_Products: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_Lead_Products: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_Lead_Products;

	PROCEDURE Export_GS_Simple_Products ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
		nCount NUMBER;
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_PSmpl'; 
		v_message :='Export_GS_Simple_Products: STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_EXPT_SIMPLE_PRODUCTS';
		IF(nCount > 0)THEN
			EXECUTE immediate 'truncate table BBB_SWITCH_B.ENDECA_EXPT_SIMPLE_PRODUCTS ';   
		END IF;

		IF p_feedType = 'PARTIAL' THEN
			execute immediate 'insert into BBB_SWITCH_B.endeca_expt_Simple_Products (product_id) select a.product_id from BBB_SWITCH_B.bbb_product b,BBB_SWITCH_B.dcs_product a where b.product_id=a.product_id and b.last_mod_date > '''|| p_lastModifiedDate ||'''';
		ELSE
			execute immediate 'insert into BBB_SWITCH_B.endeca_expt_Simple_Products (product_id) select a.product_id from BBB_SWITCH_B.bbb_product b,BBB_SWITCH_B.dcs_product a where b.product_id=a.product_id and ((b.GS_BBB_WEB_OFFERED = ''1'' and  b.GS_BBB_DISABLED = ''0'') OR (b.GS_CA_WEB_OFFERED = ''1'' and b.GS_CA_DISABLED = ''0'') OR (b.GS_BAB_WEB_OFFERED = ''1'' and b.GS_BAB_DISABLED = ''0''))';
		END IF;
		execute immediate 'update BBB_SWITCH_B.endeca_expt_Simple_Products t set t.gsPLPImage = (select t1.scene7_url from BBB_SWITCH_B.BBB_GS_PRODUCT_IMAGES t1 where t.product_id=t1.product_id and t1.SHOT_TYPE = ''GSC''), t.gsPDPImage = (select t2.scene7_url from BBB_SWITCH_B.BBB_GS_PRODUCT_IMAGES t2 where t.product_id=t2.product_id and t2.SHOT_TYPE = ''GSP'')';
		endeca_query:='select distinct ''*#*'',a.product_id, gift_cert,collection_flag,swatch_flag,brand_id,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code,
			case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id, 
			case when NVL(e.gsPLPImage,b.scene7_url) is not null then NVL(e.gsPLPImage,b.scene7_url)||''?$146$'' else '''' end as medium_image_id,
			case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
			case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id,
			d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index, 
			case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail,
			anywhere_zoom,case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end,
			case when NVL(e.gsPDPImage,b.scene7_url) is not null then NVL(e.gsPDPImage,b.scene7_url)||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id,case when e.gsPLPImage is not null then e.gsPLPImage else b.scene7_url end as gsPLPImage
			from BBB_SWITCH_B.dcs_product a, BBB_SWITCH_B.bbb_product b, BBB_SWITCH_B.dcs_prd_media c, BBB_SWITCH_B.bbb_prd_media d, BBB_CORE.BBB_SCHOOLS f ,BBB_SWITCH_B.endeca_expt_Simple_Products e
			where a.product_id = e.product_id and a.product_id = b.product_id and a.product_id=c.product_id(+) and a.product_id=d.product_id(+) 
			and b.college_id=f.school_id(+) and b.collection_flag=''0'' and b.lead_prd_flag = ''0'' and exists(select 1 from BBB_SWITCH_B.dcs_cat_chldprd a1 where a1.child_prd_id = a.product_id) ';

		IF p_feedType = 'PARTIAL' THEN
			endeca_query:='select distinct ''*#*'',a.product_id, gift_cert,collection_flag,swatch_flag,brand_id,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code, case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,  case when NVL(e.gsPLPImage,b.scene7_url) is not null then NVL(e.gsPLPImage,b.scene7_url)||''?$146$'' else '''' end as medium_image_id, case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index,  case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail, anywhere_zoom,case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end, case when NVL(e.gsPDPImage,b.scene7_url) is not null then NVL(e.gsPDPImage,b.scene7_url)||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id,case when e.gsPLPImage is not null then e.gsPLPImage else b.scene7_url end as gsPLPImage from BBB_SWITCH_B.dcs_product a, BBB_SWITCH_B.bbb_product b, BBB_SWITCH_B.dcs_prd_media c, BBB_SWITCH_B.bbb_prd_media d, BBB_CORE.BBB_SCHOOLS f,BBB_SWITCH_B.endeca_expt_Simple_Products e, BBB_SWITCH_B.dcs_prd_chldsku g, BBB_SWITCH_B.bbb_sku h, BBB_SWITCH_B.dcs_cat_chldprd i, BBB_SWITCH_B.bbb_category j where a.product_id = b.product_id and a.product_id = e.product_id and a.product_id=c.product_id(+) and a.product_id=d.product_id(+)  and a.product_id=g.product_id(+) and g.sku_id=h.sku_id(+) and a.product_id=i.child_prd_id (+) and i.category_id = j.category_id and b.college_id=f.school_id(+) and b.collection_flag=''0'' and b.lead_prd_flag = ''0'' and exists (select 1 from BBB_SWITCH_B.dcs_cat_chldprd a1 where a1.child_prd_id = a.product_id)  and ( b.last_mod_date > '''|| p_lastModifiedDate ||''' or h.last_mod_date > '''|| p_lastModifiedDate ||'''  or j.last_mod_date > '''|| p_lastModifiedDate ||''' ) ' ;
		END IF;
		dbms_output.put_line('Export_GS_Simple_Products: '||endeca_query);
		v_message :='Export_GS_Simple_Products: Query:'||endeca_query;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|VERT_IMAGE_ID|SHOP_GUIDE_ID|GSPLPIMAGE' ) INTO l_rows FROM dual;
		dbms_output.put_line('Export_GS_Simple_Products: '||l_rows);
		v_message :='Export_GS_Simple_Products: ENDS: Rows returned:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_Simple_Products: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_Simple_Products: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_Simple_Products;
	
	PROCEDURE Export_GS_Brands ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_Brand'; 
		endeca_query:='select ''*#*'',a.brand_id,brand_descrip,brand_image,site_id from BBB_SWITCH_B.BBB_BRANDS a, BBB_SWITCH_B.bbb_brand_sites b where a.brand_id=b.brand_id and b.site_id like ''GS_%''';
		dbms_output.put_line('Export_GS_Brands: '||endeca_query);     
		v_message :='Export_GS_Brands: STARTS: endeca_query:'||endeca_query;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		IF p_feedType = 'PARTIAL' then
			endeca_query := 'select distinct ''*#*'',a.brand_id,brand_descrip,brand_image,site_id from BBB_SWITCH_B.BBB_BRANDS a, BBB_SWITCH_B.bbb_brand_sites b,BBB_SWITCH_B.bbb_product c where a.brand_id=b.brand_id and b.site_id like ''GS_%'' and c.brand_id=a.brand_id and (a.last_mod_date > '''|| p_lastModifiedDate ||''' or c.last_mod_date > '''|| p_lastModifiedDate ||''')' ;
		END IF;
		dbms_output.put_line('Export_GS_Brands: '||endeca_query);  
		v_message :='Export_GS_Brands: endeca_query:'||endeca_query;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'NEW_RECORD|BRAND_ID|BRAND_DESCRIP|BRAND_IMAGE|SITE_ID' ) into l_rows from dual;
		dbms_output.put_line('Export_GS_Brands: '||l_rows);
		v_message :='Export_GS_Brands: ENDS: Total rows:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_Brands: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_Brands: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_Brands;
	
	PROCEDURE Export_GS_Item_Attributes ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_Attr'; 
		v_message :='Export_GS_Item_Attributes: STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);      
		endeca_query:='select ''*#*'',a.sku_attribute_id, replace (display_descrip, ''"'',''''''''), image_url,action_url, place_holder, priority, site_id, start_date, end_date,
			CASE WHEN INSTR( display_descrip, ''>'') > 1 THEN SUBSTR( display_descrip, INSTR( display_descrip, ''>'') + 1, INSTR( display_descrip, ''</'') - INSTR( display_descrip, ''>'') - 1 ) ELSE display_descrip END ATTRIBUTE_DISPLAY_NAME
			from BBB_SWITCH_B.BBB_SKU_ATTRIBUTES_INFO a, BBB_SWITCH_B.BBB_SKU_ATTRIBUTE_SITES b where a.sku_attribute_id = b.sku_attribute_id and b.site_id like ''GS_%''
			and NVL(a.start_date,sysdate) <= sysdate and NVL(a.end_date, sysdate) >= sysdate and place_holder is not null';
		IF p_feedType  = 'PARTIAL' THEN
			endeca_query:='select distinct ''*#*'',c.sku_attribute_id, replace (display_descrip, ''"'',''''''''), a.image_url, a.action_url, a.place_holder, a.priority, b.site_id, a.start_date, a.end_date,
				CASE WHEN INSTR( display_descrip, ''>'') > 1 THEN SUBSTR( display_descrip, INSTR( display_descrip, ''>'') + 1, INSTR( display_descrip, ''</'') - INSTR( display_descrip, ''>'') - 1 ) ELSE display_descrip END ATTRIBUTE_DISPLAY_NAME
				from BBB_SWITCH_B.BBB_SKU_ATTRIBUTES_INFO a, BBB_SWITCH_B.BBB_SKU_ATTRIBUTE_SITES b, BBB_SWITCH_B.BBB_ATTR_RELN c,BBB_SWITCH_B.BBB_PRODUCT d,BBB_SWITCH_B.BBB_PRD_ATTR_RELN f                
				where a.sku_attribute_id = b.sku_attribute_id and b.sku_attribute_id = c.sku_attribute_id and f.prd_attr_reln_id=c.sku_attr_reln_id and f.product_id=d.product_id and b.site_id like ''GS_%'' 
				and NVL(a.start_date,sysdate) <= sysdate and NVL(a.end_date, sysdate) >= sysdate and place_holder is not null
				and c.last_mod_date > '''|| p_lastModifiedDate ||''' or d.last_mod_date > '''|| p_lastModifiedDate ||''' ';
		END IF;
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|SKU_ATTRIBUTE_ID|DISPLAY_DESCRIP|IMAGE_URL|ACTION_URL|PLACE_HOLDER|PRIORITY|SITE_ID|START_DATE|END_DATE|ATTRIBUTE_DISPLAY_NAME' ) INTO l_rows FROM dual;
		dbms_output.put_line('Export_GS_Item_Attributes: '||l_rows);
		v_message :='Export_GS_Item_Attributes: ENDS: Total rows:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_Item_Attributes: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_Item_Attributes: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_Item_Attributes;

	PROCEDURE Export_GS_Product_Item_Attr ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
		nCount NUMBER;
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_PATT'; 
		v_message :='Export_Product_Item_Attributes: STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		
		select count(*) into nCount from all_tables where table_name ='ENDECA_GS_PRODUCT_ITEM_ATTR';
	 IF(nCount > 0)THEN
		execute immediate 'truncate table BBB_SWITCH_B.ENDECA_GS_PRODUCT_ITEM_ATTR'; 
	 END IF;
	 
		--- Insert Data into temp table Start
		insert into BBB_SWITCH_B.ENDECA_GS_PRODUCT_ITEM_ATTR (product_id, SKU_ATTRIBUTE_ID, SITE_ID, start_date, END_DATE, MISC_INFO,attr_last_modified_date, product_last_modified_date)
			(SELECT distinct A.PRODUCT_ID,B.SKU_ATTRIBUTE_ID,'GS_BedBathUS',B.START_DATE,B.END_DATE,B.MISC_INFO,B.last_mod_date, d.last_mod_date             
					FROM BBB_SWITCH_B.BBB_PRD_ATTR_RELN A, BBB_SWITCH_B.BBB_ATTR_RELN B, BBB_SWITCH_B.BBB_PRODUCT D            
					WHERE A.PRD_ATTR_RELN_ID=B.SKU_ATTR_RELN_ID AND b.GS_BAB_FLAG=1
					AND A.PRODUCT_ID=D.PRODUCT_ID AND NVL(b.start_date,sysdate) <= sysdate and NVL(b.end_date, sysdate) >= sysdate);
					
		insert into BBB_SWITCH_B.ENDECA_GS_PRODUCT_ITEM_ATTR (product_id, SKU_ATTRIBUTE_ID, SITE_ID, start_date, END_DATE, MISC_INFO,attr_last_modified_date, product_last_modified_date)
			(SELECT distinct A.PRODUCT_ID,B.SKU_ATTRIBUTE_ID,'GS_BuyBuyBaby',B.START_DATE,B.END_DATE,B.MISC_INFO,B.last_mod_date, d.last_mod_date             
					FROM BBB_SWITCH_B.BBB_PRD_ATTR_RELN A, BBB_SWITCH_B.BBB_ATTR_RELN B, BBB_SWITCH_B.BBB_PRODUCT D            
					WHERE A.PRD_ATTR_RELN_ID=B.SKU_ATTR_RELN_ID AND b.GS_BBB_FLAG=1
					AND A.PRODUCT_ID=D.PRODUCT_ID AND NVL(b.start_date,sysdate) <= sysdate and NVL(b.end_date, sysdate) >= sysdate);
					
		insert into BBB_SWITCH_B.ENDECA_GS_PRODUCT_ITEM_ATTR (product_id, SKU_ATTRIBUTE_ID, SITE_ID, start_date, END_DATE, MISC_INFO,attr_last_modified_date, product_last_modified_date)
			(SELECT distinct A.PRODUCT_ID,B.SKU_ATTRIBUTE_ID,'GS_BedBathCanada',B.START_DATE,B.END_DATE,B.MISC_INFO,B.last_mod_date, d.last_mod_date             
					FROM BBB_SWITCH_B.BBB_PRD_ATTR_RELN A, BBB_SWITCH_B.BBB_ATTR_RELN B, BBB_SWITCH_B.BBB_PRODUCT D            
					WHERE A.PRD_ATTR_RELN_ID=B.SKU_ATTR_RELN_ID AND b.GS_CA_FLAG=1
					AND A.PRODUCT_ID=D.PRODUCT_ID AND NVL(b.start_date,sysdate) <= sysdate and NVL(b.end_date, sysdate) >= sysdate);
		--- Insert Data into temp table End
		
			endeca_query :='SELECT distinct PRODUCT_ID,SKU_ATTRIBUTE_ID,SITE_ID,START_DATE,END_DATE,MISC_INFO from BBB_SWITCH_B.ENDECA_GS_PRODUCT_ITEM_ATTR';
		IF p_feedType   = 'PARTIAL' THEN
			endeca_query := endeca_query || ' a where a.attr_last_modified_date > '''|| p_lastModifiedDate ||''' or a.product_last_modified_date > '''|| p_lastModifiedDate ||'''' ;
		END IF;
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'PRODUCT_ID|SKU_ATTRIBUTE_ID|SITE_ID|START_DATE|END_DATE|MISC_INFO') INTO l_rows FROM dual;      
		dbms_output.put_line('Export_GS_Product_Item_Attr: '||l_rows);
		v_message :='Export_GS_Product_Item_Attr: ENDS: Total rows returned: '||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_Product_Item_Attr: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
		v_message_type :='I';
		v_message := 'Export_GS_Product_Item_Attr: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_Product_Item_Attr;
	
	PROCEDURE Export_GS_SKU_Item_Attributes ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
		nCount NUMBER;
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_SATT'; 
		v_message :='Export_GS_SKU_Item_Attributes: STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		
		select count(*) into nCount from all_tables where table_name ='ENDECA_GS_SKU_ITEM_ATTR';
	 IF(nCount > 0)THEN
		execute immediate 'truncate table BBB_SWITCH_B.ENDECA_GS_SKU_ITEM_ATTR'; 
	 END IF;
	 
		--- Insert Data into temp table Start
		insert into BBB_SWITCH_B.ENDECA_GS_SKU_ITEM_ATTR (sku_id, SKU_ATTRIBUTE_ID, SITE_ID, start_date, END_DATE, MISC_INFO, attr_last_modified_date)
			(select a.sku_id,b.sku_attribute_id,'GS_BedBathUS',b.start_date,b.end_date,b.misc_info, b.last_mod_date 
					from BBB_SWITCH_B.BBB_SKU_ATTR_RELN a, BBB_SWITCH_B.BBB_ATTR_RELN b  
					where a.sku_attr_reln_id=b.sku_attr_reln_id and b.GS_BAB_FLAG=1
					and NVL(b.start_date,sysdate) <= sysdate and NVL(b.end_date, sysdate) >= sysdate);
					
		insert into BBB_SWITCH_B.ENDECA_GS_SKU_ITEM_ATTR (sku_id, SKU_ATTRIBUTE_ID, SITE_ID, start_date, END_DATE, MISC_INFO, attr_last_modified_date)
			(select a.sku_id,b.sku_attribute_id,'GS_BuyBuyBaby',b.start_date,b.end_date,b.misc_info, b.last_mod_date 
					from BBB_SWITCH_B.BBB_SKU_ATTR_RELN a, BBB_SWITCH_B.BBB_ATTR_RELN b  
					where a.sku_attr_reln_id=b.sku_attr_reln_id and b.GS_BBB_FLAG=1
					and NVL(b.start_date,sysdate) <= sysdate and NVL(b.end_date, sysdate) >= sysdate);
					
		insert into BBB_SWITCH_B.ENDECA_GS_SKU_ITEM_ATTR (sku_id, SKU_ATTRIBUTE_ID, SITE_ID, start_date, END_DATE, MISC_INFO, attr_last_modified_date)
			(select a.sku_id,b.sku_attribute_id,'GS_BedBathCanada',b.start_date,b.end_date,b.misc_info, b.last_mod_date 
					from BBB_SWITCH_B.BBB_SKU_ATTR_RELN a, BBB_SWITCH_B.BBB_ATTR_RELN b  
					where a.sku_attr_reln_id=b.sku_attr_reln_id and b.GS_CA_FLAG=1
					and NVL(b.start_date,sysdate) <= sysdate and NVL(b.end_date, sysdate) >= sysdate);
		--- Insert Data into temp table End
		
			endeca_query:='select sku_id, sku_attribute_id, site_id, start_date, end_date, misc_info from BBB_SWITCH_B.ENDECA_GS_SKU_ITEM_ATTR';
		IF p_feedType = 'PARTIAL' THEN
			endeca_query := 'select sku_id, sku_attribute_id, site_id, start_date, end_date, misc_info from BBB_SWITCH_B.ENDECA_GS_SKU_ITEM_ATTR a where a.attr_last_modified_date > '''|| p_lastModifiedDate ||'''' ;
		END IF;
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'SKU_ID|SKU_ATTRIBUTE_ID|SITE_ID|START_DATE|END_DATE|MISC_INFO' ) INTO l_rows FROM dual;
		dbms_output.put_line('Export_GS_SKU_Item_Attributes: '||l_rows);
		v_message :='Export_GS_SKU_Item_Attributes: ENDS: Total rows:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_SKU_Item_Attributes: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_SKU_Item_Attributes: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_SKU_Item_Attributes;
	
	PROCEDURE Export_GS_BBB_Channel_Category ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows number; -- pkg var to capture the number of rows affected from the function
		endeca_query varchar(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_BBB_Chan'; 
		v_message :='Export_GS_BBB_Channel_Category: STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

		endeca_query:='SELECT distinct channel.channel_id, a.child_cat_id,b.display_name FROM BBB_SWITCH_B.dcs_cat_chldcat a, BBB_SWITCH_B.dcs_category b,(select d.category_id as category_id, a.channel_id as channel_id from BBB_SWITCH_B.BBB_CHANNEL_INFO a, BBB_SWITCH_B.BBB_GS_CHAN_CATS b, BBB_SWITCH_B.BBB_GS_CATEGORY_CONTNR c, BBB_SWITCH_B.BBB_GS_CONT_CATS d where a.channel_id=b.channel_id and b.cat_container_id=c.cat_container_id and c.cat_container_id=d.cat_container_id and b.site_id=''GS_BedBathUS'') channel WHERE CONNECT_BY_ISLEAF=1 and a.child_cat_id=b.category_id START WITH a.category_id = channel.category_id CONNECT BY PRIOR a.child_cat_id = a.category_id and PRIOR channel.channel_id=channel.channel_id union SELECT distinct category_channel.channel_id, b.category_id,b.display_name FROM BBB_SWITCH_B.dcs_category b,(select d.category_id as category_id, a.channel_id as channel_id from BBB_SWITCH_B.BBB_CHANNEL_INFO a, BBB_SWITCH_B.BBB_GS_CHAN_CATS b, BBB_SWITCH_B.BBB_GS_CATEGORY_CONTNR c, BBB_SWITCH_B.BBB_GS_CONT_CATS d where a.channel_id=b.channel_id and b.cat_container_id=c.cat_container_id and c.cat_container_id=d.cat_container_id and b.site_id=''GS_BedBathUS'') category_channel where b.category_id=category_channel.category_id and b.category_id not in (select category_id from BBB_SWITCH_B.dcs_cat_chldcat)';
		dbms_output.put_line('Export_GS_BBB_Channel_Category: '||endeca_query);     
		select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'FFNAME|CATEGORYID|CATEGORYNAME' ) into l_rows from dual;
		dbms_output.put_line('Export_GS_BBB_Channel_Category: '||l_rows);
		v_message :='Export_GS_BBB_Channel_Category: ENDS: Total rows:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_BBB_Channel_Category: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_BBB_Channel_Category: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_BBB_Channel_Category;
	  
	PROCEDURE Export_GS_BAB_Channel_Category ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows number; -- pkg var to capture the number of rows affected from the function
		endeca_query varchar(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_BAB_Chan'; 
		v_message :='Export_GS_BAB_Channel_Category: STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		endeca_query:='SELECT distinct channel.channel_id, a.child_cat_id,b.display_name FROM BBB_SWITCH_B.dcs_cat_chldcat a, BBB_SWITCH_B.dcs_category b,(select d.category_id as category_id, a.channel_id as channel_id from BBB_SWITCH_B.BBB_CHANNEL_INFO a, BBB_SWITCH_B.BBB_GS_CHAN_CATS b, BBB_SWITCH_B.BBB_GS_CATEGORY_CONTNR c, BBB_SWITCH_B.BBB_GS_CONT_CATS d where a.channel_id=b.channel_id and b.cat_container_id=c.cat_container_id and c.cat_container_id=d.cat_container_id and b.site_id=''GS_BuyBuyBaby'') channel WHERE CONNECT_BY_ISLEAF=1 and a.child_cat_id=b.category_id START WITH a.category_id = channel.category_id CONNECT BY PRIOR a.child_cat_id = a.category_id and PRIOR channel.channel_id=channel.channel_id union SELECT distinct category_channel.channel_id, b.category_id,b.display_name FROM BBB_SWITCH_B.dcs_category b,(select d.category_id as category_id, a.channel_id as channel_id from BBB_SWITCH_B.BBB_CHANNEL_INFO a, BBB_SWITCH_B.BBB_GS_CHAN_CATS b, BBB_SWITCH_B.BBB_GS_CATEGORY_CONTNR c, BBB_SWITCH_B.BBB_GS_CONT_CATS d where a.channel_id=b.channel_id and b.cat_container_id=c.cat_container_id and c.cat_container_id=d.cat_container_id and b.site_id=''GS_BuyBuyBaby'') category_channel where b.category_id=category_channel.category_id and b.category_id not in (select category_id from BBB_SWITCH_B.dcs_cat_chldcat)';
		dbms_output.put_line('Export_GS_BAB_Channel_Category: '||endeca_query);     
		select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'FFNAME|CATEGORYID|CATEGORYNAME' ) into l_rows from dual;
		dbms_output.put_line('Export_GS_BAB_Channel_Category: '||l_rows);
		v_message :='Export_GS_BAB_Channel_Category: ENDS: Total rows:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_BAB_Channel_Category: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_BAB_Channel_Category: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_BAB_Channel_Category;
	  
	PROCEDURE Export_GS_CA_Channel_Category ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows number; -- pkg var to capture the number of rows affected from the function
		endeca_query varchar(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_CA_Chan'; 
		v_message :='Export_GS_CA_Channel_Category: STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		endeca_query:='SELECT distinct channel.channel_id, a.child_cat_id,b.display_name FROM BBB_SWITCH_B.dcs_cat_chldcat a, BBB_SWITCH_B.dcs_category b,(select d.category_id as category_id, a.channel_id as channel_id from BBB_SWITCH_B.BBB_CHANNEL_INFO a, BBB_SWITCH_B.BBB_GS_CHAN_CATS b, BBB_SWITCH_B.BBB_GS_CATEGORY_CONTNR c, BBB_SWITCH_B.BBB_GS_CONT_CATS d where a.channel_id=b.channel_id and b.cat_container_id=c.cat_container_id and c.cat_container_id=d.cat_container_id and b.site_id=''GS_BedBathCanada'') channel WHERE CONNECT_BY_ISLEAF=1 and a.child_cat_id=b.category_id START WITH a.category_id = channel.category_id CONNECT BY PRIOR a.child_cat_id = a.category_id and PRIOR channel.channel_id=channel.channel_id union SELECT distinct category_channel.channel_id, b.category_id,b.display_name FROM BBB_SWITCH_B.dcs_category b,(select d.category_id as category_id, a.channel_id as channel_id from BBB_SWITCH_B.BBB_CHANNEL_INFO a, BBB_SWITCH_B.BBB_GS_CHAN_CATS b, BBB_SWITCH_B.BBB_GS_CATEGORY_CONTNR c, BBB_SWITCH_B.BBB_GS_CONT_CATS d where a.channel_id=b.channel_id and b.cat_container_id=c.cat_container_id and c.cat_container_id=d.cat_container_id and b.site_id=''GS_BedBathCanada'') category_channel where b.category_id=category_channel.category_id and b.category_id not in (select category_id from BBB_SWITCH_B.dcs_cat_chldcat)';
		dbms_output.put_line('Export_GS_CA_Channel_Category: '||endeca_query);     
		select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'FFNAME|CATEGORYID|CATEGORYNAME' ) into l_rows from dual;
		dbms_output.put_line('Export_GS_CA_Channel_Category: '||l_rows);
			v_message :='Export_GS_CA_Channel_Category: ENDS: Total rows:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_CA_Channel_Category: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_CA_Channel_Category: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_CA_Channel_Category;
	
	PROCEDURE Export_GS_BBB_Taxonomy ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows number; -- pkg var to capture the number of rows affected from the function
		endeca_query varchar(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_BBBTx'; 
		endeca_query:='select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
			from BBB_SWITCH_B.dcs_category a, BBB_SWITCH_B.bbb_category b, BBB_SWITCH_B.dcs_cat_chldcat c where a.category_id = b.category_id 
			and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''4%'' and c.category_id like ''4%''
			order by a.category_id';
		IF p_feedType   = 'PARTIAL' THEN
			endeca_query := 'select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
				from BBB_SWITCH_B.dcs_category a, BBB_SWITCH_B.bbb_category b, BBB_SWITCH_B.dcs_cat_chldcat c where a.category_id = b.category_id 
				and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''4%'' and c.category_id like ''4%''
				and b.last_mod_date > '''|| p_lastModifiedDate ||''' order by a.category_id' ;
		END IF;
		dbms_output.put_line('Export_GS_BBB_Taxonomy: '||endeca_query);
		v_message :='Export_GS_BBB_Taxonomy : STARTS : Query: '||endeca_query;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		select dump_csv( endeca_query, '|', p_feedType, p_filename,'40000||GS Bed Bath & Beyond||||', 'CHILD_CAT_ID|CATEGORY_ID|DISPLAY_NAME|IS_COLLEGE|NODE_TYPE|PHANTOM_CATEGORY|GUIDE_ID') into l_rows from dual;
		dbms_output.put_line('Export_GS_BBB_Taxonomy: '||l_rows);
		v_message :='Export_GS_BBB_Taxonomy : ENDS : Total rows:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_BBB_Taxonomy : Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_BBB_Taxonomy : Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_BBB_Taxonomy;

	PROCEDURE Export_GS_BAB_Taxonomy ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_BABTx'; 
		endeca_query:='select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
			from BBB_SWITCH_B.dcs_category a, BBB_SWITCH_B.bbb_category b, BBB_SWITCH_B.dcs_cat_chldcat c where a.category_id = b.category_id 
			and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''6%'' and c.category_id like ''6%''
			order by a.category_id';
		dbms_output.put_line('Export_GS_BAB_Taxonomy: '||endeca_query);     
		v_message :='Export_GS_BAB_Taxonomy : STARTS : Query: '||endeca_query;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		IF p_feedType = 'PARTIAL' THEN
			endeca_query := 'select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
				from BBB_SWITCH_B.dcs_category a, BBB_SWITCH_B.bbb_category b, BBB_SWITCH_B.dcs_cat_chldcat c where a.category_id = b.category_id 
				and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''6%'' and c.category_id like ''6%''
				and b.last_mod_date > '''|| p_lastModifiedDate ||''' order by a.category_id' ;
		END IF;
		dbms_output.put_line('Export_GS_BAB_Taxonomy: '||endeca_query);  
		v_message :='Export_GS_BAB_Taxonomy:Query:'||endeca_query;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, '60000||GS Buy Buy Baby||||', 'CHILD_CAT_ID|CATEGORY_ID|DISPLAY_NAME|IS_COLLEGE|NODE_TYPE|PHANTOM_CATEGORY|GUIDE_ID' ) INTO l_rows FROM dual;
		dbms_output.put_line('Export_GS_BAB_Taxonomy: '||l_rows);
		v_message :='Export_GS_BAB_Taxonomy : ENDS : Total rows:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_BAB_Taxonomy : Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_BAB_Taxonomy : Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_BAB_Taxonomy;

	PROCEDURE Export_GS_CA_Taxonomy ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_CATx'; 
		endeca_query:='select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
			from BBB_SWITCH_B.dcs_category a, BBB_SWITCH_B.bbb_category b, BBB_SWITCH_B.dcs_cat_chldcat c where a.category_id = b.category_id 
			and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''5%'' and c.category_id like ''5%''
			order by a.category_id';
		dbms_output.put_line('Export_GS_CA_Taxonomy: '||endeca_query);
		v_message :='Export_GS_CA_Taxonomy : STARTS : Query :'||endeca_query;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		IF p_feedType   = 'PARTIAL' THEN
			endeca_query := 'select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
				from BBB_SWITCH_B.dcs_category a, BBB_SWITCH_B.bbb_category b, BBB_SWITCH_B.dcs_cat_chldcat c where a.category_id = b.category_id 
				and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''5%'' and c.category_id like ''5%''
				and b.last_mod_date > '''|| p_lastModifiedDate ||''' order by a.category_id' ;
		END IF;
		dbms_output.put_line('Export_GS_CA_Taxonomy: '||endeca_query);  
		v_message :='Export_GS_CA_Taxonomy : Query :'||endeca_query;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename,'50000||GS Bed Bath Canada||||', 'CHILD_CAT_ID|CATEGORY_ID|DISPLAY_NAME|IS_COLLEGE|NODE_TYPE|PHANTOM_CATEGORY|GUIDE_ID' ) INTO l_rows FROM dual;
		dbms_output.put_line('Export_GS_CA_Taxonomy: '||l_rows);
		v_message :='Export_GS_CA_Taxonomy : ENDS : Total rows:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_CA_Taxonomy: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_CA_Taxonomy: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_CA_Taxonomy;

	PROCEDURE Export_GS_Category_Products ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows NUMBER; -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		nCount NUMBER;
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_PrCat'; 
		v_message :='Export_GS_Category_Products : STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

		SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_EXP_GS_CAT_PRODS';
		IF(nCount > 0)THEN
			EXECUTE immediate 'truncate table ENDECA_EXP_GS_CAT_PRODS';
		END IF;  
  
		IF p_feedType = 'PARTIAL' THEN
			insert into ENDECA_EXP_GS_CAT_PRODS (cat_id, prod_id, seq_num) 
				select distinct a.category_id, a.child_prd_id, a.sequence_num
					from BBB_SWITCH_B.dcs_cat_chldprd a, BBB_SWITCH_B.BBB_CATEGORY b, BBB_SWITCH_B.BBB_PRODUCT c, BBB_SWITCH_B.dcs_prd_chldsku g, BBB_SWITCH_B.bbb_sku h, BBB_SWITCH_B.dcs_price h1, BBB_SWITCH_B.dcs_price i, BBB_SWITCH_B.bbb_price h2, BBB_SWITCH_B.bbb_price i1 
					where a.category_id = b.category_id and c.product_id = a.child_prd_id 
					and c.product_id = g.product_id (+) and g.sku_id = h.sku_id (+) and c.sku_low_price = h1.sku_id (+) and c.sku_high_price = i.sku_id (+) 
					and h1.price_id = h2.price_id (+) and i.price_id = i1.price_id (+)
					and ( b.last_mod_date >'''|| p_lastModifiedDate ||'''
					  or c.last_mod_date >'''|| p_lastModifiedDate ||'''
					  or h.last_mod_date > '''|| p_lastModifiedDate ||'''
					  or h2.last_mod_date >'''|| p_lastModifiedDate ||'''
					  or i1.last_mod_date >'''|| p_lastModifiedDate ||'''
					  ) order by a.category_id, a.sequence_num;
			BEGIN
				FOR id IN (SELECT cat_id, prod_id FROM ENDECA_EXP_GS_CAT_PRODS)
				LOOP
				  update ENDECA_EXP_GS_CAT_PRODS x set x.seq_num = 
				  NVL((select sequence_num  FROM cat_product_seq t where id.cat_id = t.category_id and t.child_prd_id = id.prod_id), '999') 
				  where x.cat_id = id.cat_id and id.prod_id = x.prod_id;
				END LOOP;
			END;
		ELSE
			insert into ENDECA_EXP_GS_CAT_PRODS (cat_id, prod_id, seq_num) 
			SELECT a.category_id,a.child_prd_id, NVL(b.sequence_num,'999') from dcs_cat_chldprd a LEFT JOIN cat_product_seq b ON a.category_id = b.category_id and a.child_prd_id=b.child_prd_id;
		END IF;
		COMMIT;
		endeca_query:='select cat_id,prod_id,seq_num from ENDECA_EXP_GS_CAT_PRODS where (cat_id like ''4%'' OR cat_id like ''5%'' OR cat_id like ''6%'')';
		dbms_output.put_line('Export_GS_Category_Products: '||endeca_query);
		v_message :='Export_GS_Category_Products : Query:'||endeca_query;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'CATEGORY_ID|CHILD_PRD_ID|SEQUENCE_NUM' ) INTO l_rows FROM dual;
		dbms_output.put_line('Export_GS_Category_Products: '||l_rows);
		v_message :='Export_GS_Category_Products : ENDS: Total rows:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_Category_Products: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_Category_Products: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_Category_Products;

	PROCEDURE Export_GS_Prices ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		nCount NUMBER;
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_Price'; 
		dbms_output.put_line('Export_GS_Prices : '||endeca_query);
		v_message :='Export_GS_Prices :STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_GS_EXPORT_PRICES';
		IF(nCount > 0)THEN
			EXECUTE immediate 'truncate table BBB_SWITCH_B.ENDECA_GS_EXPORT_PRICES ';   
		END IF;

		insert into BBB_SWITCH_B.endeca_gs_export_prices (sku_id, was_price,last_mod_date) select t1.sku_id, t1.list_price,t2.last_mod_date from BBB_SWITCH_B.dcs_price t1, BBB_SWITCH_B.bbb_price t2 , BBB_SWITCH_B.bbb_sku a where t1.price_id = t2.price_id(+) and t1.sku_id = a.sku_id and t1.price_list = 'plist100005' and ((a.GS_BBB_WEB_OFFERED = '1' and  a.GS_BBB_DISABLED = '0') OR (a.GS_CA_WEB_OFFERED = '1' and a.GS_CA_DISABLED = '0') OR (a.GS_BAB_WEB_OFFERED = '1' and a.GS_BAB_DISABLED = '0')) order by t1.sku_id ;

		MERGE INTO BBB_SWITCH_B.endeca_gs_export_prices t USING (select distinct sku_id,list_price from BBB_SWITCH_B.dcs_price  where price_list='plist100004') t1 on (t.sku_id = t1.sku_id) WHEN MATCHED THEN UPDATE SET t.is_price =t1.list_price;
		MERGE INTO BBB_SWITCH_B.endeca_gs_export_prices t USING (select distinct sku_id,list_price from BBB_SWITCH_B.dcs_price  where price_list='plist100003') t1 on (t.sku_id = t1.sku_id) WHEN MATCHED THEN UPDATE SET t.ca_is_price =t1.list_price;
		MERGE INTO BBB_SWITCH_B.endeca_gs_export_prices t USING (Select distinct sku_id,list_price from BBB_SWITCH_B.dcs_price  where price_list='plist100002') t1 on (t.sku_id = t1.sku_id) WHEN MATCHED THEN UPDATE Set t.ca_was_price =t1.list_price;
		endeca_query:='select sku_id,LTRIM(to_char(was_price,999999999999.99)),LTRIM(to_char(case when is_price is null or is_price = '''' then was_price else is_price end,999999999999.99)),LTRIM(to_char(ca_was_price,999999999999.99)),LTRIM(to_char(case when ca_is_price is null or ca_is_price = '''' then ca_was_price else ca_is_price end,999999999999.99)) from BBB_SWITCH_B.endeca_gs_export_prices';

		dbms_output.put_line('Export_GS_Prices: '||endeca_query);
		v_message :='Export_GS_Prices :Query:'||endeca_query;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		COMMIT;
		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'SKU_ID|WAS_PRICE|IS_PRICE|CA_WAS_PRICE|CA_IS_PRICE') INTO l_rows FROM dual;

		dbms_output.put_line('Export_GS_Prices: '||l_rows);
		v_message :='Export_GS_Prices : ENDS: Total Rows:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN  -- handles all other errors
			dbms_output.put_line('Export_GS_Prices: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message := 'Export_GS_Prices: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
			COMMIT;
	END Export_GS_Prices;

	PROCEDURE Export_GS_Product_Site_Prop( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		nCount NUMBER;
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN 
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_PrStP'; 
		v_message :='Export_GS_Product_Site_Prop:STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

		SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_EXPORT_GS_PROD_PROP';
		IF(nCount > 0)THEN
			EXECUTE immediate 'truncate table BBB_SWITCH_B.ENDECA_EXPORT_GS_PROD_PROP ';
		END IF; 
		IF p_feedType = 'PARTIAL' THEN 
			execute immediate 'insert into BBB_SWITCH_B.endeca_export_gs_prod_prop(product_id,web_offered_flag, disable_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price, enable_date, site_id) select a.product_id, GS_BBB_WEB_OFFERED, GS_BBB_DISABLED, display_name, dbms_lob.substr( long_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price,b.enable_date,''GS_BedBathUS'' from BBB_SWITCH_B.dcs_product a, BBB_SWITCH_B.bbb_product b where a.product_id=b.product_id and b.GS_BBB_WEB_OFFERED = ''1'' and  b.GS_BBB_DISABLED = ''0'' and a.product_id in (select child_prd_id from BBB_SWITCH_B.dcs_cat_chldprd union all select product_id from BBB_SWITCH_B.bbb_prd_reln where NVL(like_unlike, 0) = 0) and b.last_mod_date > '''|| p_lastModifiedDate ||'''';
			execute immediate 'insert into BBB_SWITCH_B.endeca_export_gs_prod_prop(product_id, web_offered_flag, disable_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price,enable_date, site_id) select a.product_id, GS_BAB_WEB_OFFERED, GS_BAB_DISABLED, display_name, dbms_lob.substr( long_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price,b.enable_date,''GS_BuyBuyBaby'' from BBB_SWITCH_B.dcs_product a, BBB_SWITCH_B.bbb_product b where a.product_id=b.product_id and b.GS_BAB_WEB_OFFERED = ''1'' and  b.GS_BAB_DISABLED = ''0'' and a.product_id in (select child_prd_id from BBB_SWITCH_B.dcs_cat_chldprd union all select product_id from BBB_SWITCH_B.bbb_prd_reln where NVL(like_unlike, 0) = 0) and b.last_mod_date > '''|| p_lastModifiedDate ||'''';
			execute immediate 'insert into BBB_SWITCH_B.endeca_export_gs_prod_prop(product_id,web_offered_flag, disable_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price,enable_date, site_id) select a.product_id, GS_CA_WEB_OFFERED, GS_CA_DISABLED, display_name, dbms_lob.substr( long_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price,b.enable_date,''GS_BedBathCanada'' from BBB_SWITCH_B.dcs_product a, BBB_SWITCH_B.bbb_product b where a.product_id=b.product_id and b.GS_CA_WEB_OFFERED = ''1'' and  b.GS_CA_DISABLED = ''0'' and a.product_id in (select child_prd_id from BBB_SWITCH_B.dcs_cat_chldprd union all select product_id from BBB_SWITCH_B.bbb_prd_reln where NVL(like_unlike, 0) = 0) and b.last_mod_date > '''|| p_lastModifiedDate ||'''';
		ELSE 
			execute immediate 'insert into BBB_SWITCH_B.endeca_export_gs_prod_prop(product_id,web_offered_flag, disable_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price,enable_date, site_id ) select a.product_id, GS_BBB_WEB_OFFERED, GS_BBB_DISABLED, display_name, dbms_lob.substr( long_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price,b.enable_date,''GS_BedBathUS'' from BBB_SWITCH_B.dcs_product a, BBB_SWITCH_B.bbb_product b where a.product_id=b.product_id and b.GS_BBB_WEB_OFFERED = ''1'' and  b.GS_BBB_DISABLED = ''0'' and a.product_id in (select child_prd_id from BBB_SWITCH_B.dcs_cat_chldprd union all select product_id from BBB_SWITCH_B.bbb_prd_reln where NVL(like_unlike, 0) = 0) ';
			execute immediate 'insert into BBB_SWITCH_B.endeca_export_gs_prod_prop(product_id,web_offered_flag, disable_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price,enable_date, site_id ) select a.product_id, GS_BAB_WEB_OFFERED, GS_BAB_DISABLED, display_name, dbms_lob.substr( long_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price,b.enable_date,''GS_BuyBuyBaby'' from BBB_SWITCH_B.dcs_product a, BBB_SWITCH_B.bbb_product b where a.product_id=b.product_id and b.GS_BAB_WEB_OFFERED = ''1'' and  b.GS_BAB_DISABLED = ''0'' and a.product_id in (select child_prd_id from BBB_SWITCH_B.dcs_cat_chldprd union all select product_id from BBB_SWITCH_B.bbb_prd_reln where NVL(like_unlike, 0) = 0) ';
			execute immediate 'insert into BBB_SWITCH_B.endeca_export_gs_prod_prop(product_id,web_offered_flag, disable_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price,enable_date, site_id ) select a.product_id, GS_CA_WEB_OFFERED, GS_CA_DISABLED, display_name, dbms_lob.substr( long_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price,b.enable_date,''GS_BedBathCanada'' from BBB_SWITCH_B.dcs_product a, BBB_SWITCH_B.bbb_product b where a.product_id=b.product_id and b.GS_CA_WEB_OFFERED = ''1'' and  b.GS_CA_DISABLED = ''0'' and a.product_id in (select child_prd_id from BBB_SWITCH_B.dcs_cat_chldprd union all select product_id from BBB_SWITCH_B.bbb_prd_reln where NVL(like_unlike, 0) = 0) ';
		END IF; 

		--Web CA Site
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''displayName'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.product_title =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.product_title)'; 
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''description'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.short_description =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.short_description)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t2.product_id,t1.attribute_value_clob from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''longDescription'' and t1.attribute_value_clob is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.long_description =NVL(replace(tt1.attribute_value_clob,''"'',''&quot;''), t.long_description)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''skuLowPrice'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.sku_low_price =NVL(tt1.attribute_value_string, t.sku_low_price)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''skuHighPrice'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.sku_high_price =NVL(tt1.attribute_value_string, t.sku_high_price)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''priceRangeDescrip'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.price_range_descrip =NVL(tt1.attribute_value_string, t.price_range_descrip)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t2.product_id,t1.attribute_value_date from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''enableDate'' and t1.attribute_value_date is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.enable_date =NVL(tt1.attribute_value_date, t.enable_date)';
		--Web Baby Site
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''displayName'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.product_title =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.product_title)'; 
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''description'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.short_description =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.short_description)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t2.product_id,t1.attribute_value_clob from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''longDescription'' and t1.attribute_value_clob is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.long_description =NVL(replace(tt1.attribute_value_clob,''"'',''&quot;''), t.long_description)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''skuLowPrice'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.sku_low_price =NVL(tt1.attribute_value_string, t.sku_low_price)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''skuHighPrice'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.sku_high_price =NVL(tt1.attribute_value_string, t.sku_high_price)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''priceRangeDescrip'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.price_range_descrip =NVL(tt1.attribute_value_string, t.price_range_descrip)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t2.product_id,t1.attribute_value_date from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''enableDate'' and t1.attribute_value_date is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.enable_date =NVL(tt1.attribute_value_date, t.enable_date)';

		--GS Sites
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t1.site_id,t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id   and t1.attribute_name = ''displayName'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.product_title =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.product_title)'; 
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t1.site_id,t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id   and t1.attribute_name = ''description'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.short_description =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.short_description)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t1.site_id,t2.product_id,t1.attribute_value_clob from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id   and t1.attribute_name = ''longDescription'' and t1.attribute_value_clob is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.long_description =NVL(replace(tt1.attribute_value_clob,''"'',''&quot;''), t.long_description)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t1.site_id,t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id   and t1.attribute_name = ''skuLowPrice'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.sku_low_price =NVL(tt1.attribute_value_string, t.sku_low_price)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t1.site_id,t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id   and t1.attribute_name = ''skuHighPrice'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.sku_high_price =NVL(tt1.attribute_value_string, t.sku_high_price)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t1.site_id,t2.product_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_prod_site_translations t2, BBB_SWITCH_B.bbb_prod_translations t1 where t2.translation_id=t1.translation_id   and t1.attribute_name = ''priceRangeDescrip'' and t1.attribute_value_string is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.price_range_descrip =NVL(tt1.attribute_value_string, t.price_range_descrip)';

		--GS Enable Dates
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t1.product_id,t1.gs_bbb_enable_date from BBB_SWITCH_B.bbb_product t1
		where t1.gs_bbb_enable_date is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathUS'') WHEN MATCHED THEN UPDATE SET t.enable_date =NVL(tt1.gs_bbb_enable_date, t.enable_date)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t1.product_id,t1.gs_bab_enable_date from BBB_SWITCH_B.bbb_product t1
		where t1.gs_bab_enable_date is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.enable_date =NVL(tt1.gs_bab_enable_date, t.enable_date)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_export_gs_prod_prop t USING (select t1.product_id,t1.gs_ca_enable_date from BBB_SWITCH_B.bbb_product t1
		where t1.gs_ca_enable_date is not null) tt1 on (t.product_id=tt1.product_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.enable_date =NVL(tt1.gs_ca_enable_date, t.enable_date)';

		execute immediate 'delete from BBB_SWITCH_B.endeca_export_gs_prod_prop where product_id in ( select distinct e1.product_id from BBB_SWITCH_B.endeca_export_gs_prod_prop e1, BBB_SWITCH_B.bbb_product p1 where p1.product_id = e1.product_id and ( p1.collection_flag = 1 or p1.lead_prd_flag = 1) and e1.product_id not in (  select distinct e.product_id from BBB_SWITCH_B.endeca_export_gs_prod_prop e, BBB_SWITCH_B.bbb_product p, BBB_SWITCH_B.bbb_prd_prd_reln r where p.product_id = e.product_id and p.product_id = r.product_id and e.product_id = r.product_id and ( p.collection_flag = 1 or p.lead_prd_flag = 1) ) )';

		endeca_query:='select distinct ''*#*'',product_id, web_offered_flag, disable_flag, product_title, dbms_lob.substr( short_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price,TO_CHAR (enable_date, ''DD-MON-YY''), site_id from BBB_SWITCH_B.endeca_export_gs_prod_prop order by product_id'; 

		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|WEB_OFFERED_FLAG|DISABLE_FLAG|PRODUCT_TITLE|SHORT_DESCRIPTION|LONG_DESCRIPTION|PRICE_RANGE_DESCRIP|SKU_LOW_PRICE|SKU_HIGH_PRICE|ENABLE_DATE|SITE_ID' ) INTO l_rows FROM dual; 
		dbms_output.put_line('Export_GS_Product_Site_Prop: '||l_rows); 
		v_message :='Export_GS_Product_Site_Prop: ENDS : Total rows:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_Product_Site_Prop: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message_type :='I';
			v_message :='Export_GS_Product_Site_Prop: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
			COMMIT;
	END Export_GS_Product_Site_Prop;

	PROCEDURE Export_GS_SKU_Site_Properties ( p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2) 
	IS 
		l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
		endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
		nCount NUMBER;
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN 
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Exp_GS_SkStP';   
		v_message :='Export_GS_SKU_Site_Properties:STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_EXPT_GS_SKU_SITE_PROPTS';
		IF(nCount > 0)THEN
		EXECUTE immediate 'truncate table BBB_SWITCH_B.ENDECA_EXPT_GS_SKU_SITE_PROPTS ';
		END IF;      
		IF p_feedType = 'PARTIAL' THEN
			execute immediate 'insert into BBB_SWITCH_B.endeca_expt_gs_sku_site_propts (sku_id, web_offered_flag, disable_flag,sku_title, short_description, site_id,last_mod_date) select b.sku_id, b.GS_BBB_WEB_OFFERED, b.GS_BBB_DISABLED, a.display_name, a.description,''GS_BedBathUS'',b.last_mod_date from BBB_SWITCH_B.bbb_sku b,BBB_SWITCH_B.dcs_sku a where b.sku_id=a.sku_id and b.GS_BBB_WEB_OFFERED = ''1'' and  b.GS_BBB_DISABLED = ''0'' and b.last_mod_date > '''|| p_lastModifiedDate ||'''';
			execute immediate 'insert into BBB_SWITCH_B.endeca_expt_gs_sku_site_propts (sku_id, web_offered_flag, disable_flag,sku_title, short_description, site_id,last_mod_date) select b.sku_id, b.GS_BAB_WEB_OFFERED, b.GS_BAB_DISABLED, a.display_name, a.description,''GS_BuyBuyBaby'',b.last_mod_date from BBB_SWITCH_B.bbb_sku b,BBB_SWITCH_B.dcs_sku a where b.sku_id=a.sku_id and b.GS_BAB_WEB_OFFERED = ''1'' and  b.GS_BAB_DISABLED = ''0'' and b.last_mod_date > '''|| p_lastModifiedDate ||'''';
			execute immediate 'insert into BBB_SWITCH_B.endeca_expt_gs_sku_site_propts (sku_id, web_offered_flag, disable_flag,sku_title, short_description, site_id,last_mod_date) select b.sku_id, b.GS_CA_WEB_OFFERED, b.GS_CA_DISABLED, a.display_name, a.description,''GS_BedBathCanada'',b.last_mod_date from BBB_SWITCH_B.bbb_sku b,BBB_SWITCH_B.dcs_sku a where b.sku_id=a.sku_id and b.GS_CA_WEB_OFFERED = ''1'' and  b.GS_CA_DISABLED = ''0'' and b.last_mod_date > '''|| p_lastModifiedDate ||'''';
		ELSE
			execute immediate 'insert into BBB_SWITCH_B.endeca_expt_gs_sku_site_propts (sku_id, web_offered_flag, disable_flag,sku_title, short_description, site_id,last_mod_date) select b.sku_id, b.GS_BBB_WEB_OFFERED, b.GS_BBB_DISABLED, a.display_name, a.description,''GS_BedBathUS'',b.last_mod_date from BBB_SWITCH_B.bbb_sku b,BBB_SWITCH_B.dcs_sku a where b.sku_id=a.sku_id and b.GS_BBB_WEB_OFFERED = ''1'' and  b.GS_BBB_DISABLED = ''0''';
			execute immediate 'insert into BBB_SWITCH_B.endeca_expt_gs_sku_site_propts (sku_id, web_offered_flag, disable_flag,sku_title, short_description, site_id,last_mod_date) select b.sku_id, b.GS_BAB_WEB_OFFERED, b.GS_BAB_DISABLED, a.display_name, a.description,''GS_BuyBuyBaby'',b.last_mod_date from BBB_SWITCH_B.bbb_sku b,BBB_SWITCH_B.dcs_sku a where b.sku_id=a.sku_id and b.GS_BAB_WEB_OFFERED = ''1'' and  b.GS_BAB_DISABLED = ''0''';
			execute immediate 'insert into BBB_SWITCH_B.endeca_expt_gs_sku_site_propts (sku_id, web_offered_flag, disable_flag,sku_title, short_description, site_id,last_mod_date) select b.sku_id, b.GS_CA_WEB_OFFERED, b.GS_CA_DISABLED, a.display_name, a.description,''GS_BedBathCanada'',b.last_mod_date from BBB_SWITCH_B.bbb_sku b,BBB_SWITCH_B.dcs_sku a where b.sku_id=a.sku_id and b.GS_CA_WEB_OFFERED = ''1'' and  b.GS_CA_DISABLED = ''0''';
		END IF;
		--Web Baby
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_expt_gs_sku_site_propts t USING (select t2.sku_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_sku_site_translations t2, BBB_SWITCH_B.bbb_sku_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''displayName'' and t1.attribute_value_string is not null) tt1 on (t.sku_id=tt1.sku_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.sku_title =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.sku_title)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_expt_gs_sku_site_propts t USING (select t2.sku_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_sku_site_translations t2, BBB_SWITCH_B.bbb_sku_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BuyBuyBaby'' and t1.attribute_name = ''description'' and t1.attribute_value_string is not null) tt1 on (t.sku_id=tt1.sku_id and t.site_id=''GS_BuyBuyBaby'') WHEN MATCHED THEN UPDATE SET t.short_description =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.short_description)'; 
		--Web CA
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_expt_gs_sku_site_propts t USING (select t2.sku_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_sku_site_translations t2, BBB_SWITCH_B.bbb_sku_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''displayName'' and t1.attribute_value_string is not null) tt1 on (t.sku_id=tt1.sku_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.sku_title =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.sku_title)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_expt_gs_sku_site_propts t USING (select t2.sku_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_sku_site_translations t2, BBB_SWITCH_B.bbb_sku_translations t1 where t2.translation_id=t1.translation_id  and t1.site_id=''BedBathCanada'' and t1.attribute_name = ''description'' and t1.attribute_value_string is not null) tt1 on (t.sku_id=tt1.sku_id and t.site_id=''GS_BedBathCanada'') WHEN MATCHED THEN UPDATE SET t.short_description =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.short_description)';
		--GS properties
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_expt_gs_sku_site_propts t USING (select t1.site_id,t2.sku_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_sku_site_translations t2, BBB_SWITCH_B.bbb_sku_translations t1 where t2.translation_id=t1.translation_id  and t1.attribute_name = ''displayName'' and t1.attribute_value_string is not null) tt1 on (t.sku_id=tt1.sku_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.sku_title =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.sku_title)';
		execute immediate 'MERGE INTO BBB_SWITCH_B.endeca_expt_gs_sku_site_propts t USING (select t1.site_id,t2.sku_id,t1.attribute_value_string from BBB_SWITCH_B.bbb_sku_site_translations t2, BBB_SWITCH_B.bbb_sku_translations t1 where t2.translation_id=t1.translation_id and t1.attribute_name = ''description'' and t1.attribute_value_string is not null) tt1 on (t.sku_id=tt1.sku_id and t.site_id=tt1.site_id) WHEN MATCHED THEN UPDATE SET t.short_description =NVL(replace(tt1.attribute_value_string,''"'',''&quot;''), t.short_description)';

		endeca_query:='select ''*#*'',sku_id, web_offered_flag,disable_flag, sku_title, short_description, site_id from BBB_SWITCH_B.endeca_expt_gs_sku_site_propts order by sku_id'; 

		SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|SKU_ID|WEB_OFFERED_FLAG|DISABLE_FLAG|SKU_TITLE|SHORT_DESCRIPTION|SITE_ID' ) INTO l_rows FROM dual; 
		dbms_output.put_line('Export_GS_SKU_Site_Properties: '||l_rows);
		v_message :='Export_GS_SKU_Site_Properties:ENDS: Total Rows:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	EXCEPTION
		WHEN OTHERS THEN -- handles all other errors
			dbms_output.put_line('Export_GS_SKU_Site_Properties: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
			v_message :='Export_GS_SKU_Site_Properties: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
			v_message_type := 'I';
			GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
			COMMIT;
	END Export_GS_SKU_Site_Properties;	
	
	PROCEDURE Export_GS_GoFile(p_filename VARCHAR2, p_lastModifiedDate TIMESTAMP, p_feedType VARCHAR2)
	IS
		l_rows NUMBER;
		endeca_query VARCHAR(2000);
		v_process_cd VARCHAR2(12); -- Endeca Procedure Name
		v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
		v_message VARCHAR(2500); -- message to log
		v_message_type CHAR(1); --Message type to log, A for info, I for exception
	BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='GS_Go_File';   
		endeca_query:='select sysdate from dual';
		v_message := 'Export_GS_GoFile : STARTS';
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

		IF p_feedType = 'PARTIAL' THEN
			SELECT dump_csv( endeca_query, '|', p_feedType, 'partial.go', NULL, 'SYSDATE' ) INTO l_rows	FROM dual;
		ELSE
			SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'SYSDATE' ) INTO l_rows FROM dual;
		END IF;
		dbms_output.put_line('Export_GS_GoFile: '|| l_rows);
		v_message :='Export_GS_GoFile : ENDS: Total Rows:'||l_rows;
		GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	END Export_GS_GoFile;

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
			l_dir:= 'ATG_APP_PARTIAL_GS';
		ELSE 
			l_dir:= 'ATG_APP_FULL_GS';
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
	
END ATG_GS_EndecaExport_Pkg;
/
show error;
COMMIT;
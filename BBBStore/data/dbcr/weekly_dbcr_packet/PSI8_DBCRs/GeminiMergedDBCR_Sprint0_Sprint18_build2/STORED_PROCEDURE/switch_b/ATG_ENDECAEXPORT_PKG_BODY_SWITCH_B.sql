-- Build Version : 2.08.12.002
SET ECHO ON;
set DEFINE OFF;
set SERVEROUTPUT ON;

--------------------------------------------------------
--  File created - Tuesday-January-15-2013   
--  DDL for Package Body ATG_ENDECAEXPORT_PKG
--------------------------------------------------------
CREATE OR REPLACE PACKAGE BODY BBB_SWITCH_B.ATG_ENDECAEXPORT_PKG AS

PROCEDURE Log_message(p_process_cd VARCHAR2, p_process_sub_cd VARCHAR2, p_message VARCHAR2, p_row_status CHAR)
  IS
  v_schema VARCHAR2(30); --row_xng_usr
  nextVal number(14);
  BEGIN

  select BBB_PIM_STG.PROCESS_SUPPORT_SEQ.NEXTVAL into nextVal from dual;
    INSERT INTO BBB_PIM_STG.PROCESS_SUPPORT
    ( ID, PROCESS_CD, PROCESS_SUB_CD, MESSAGE, ROW_XNG_DT, ROW_XNG_USR, ROW_STATUS )
    (select nextVal, p_process_cd, p_process_sub_cd, RTRIM(SUBSTR( p_message,1,512)) , SYSDATE, 'PIM' , p_row_status  from dual);
  commit;

  EXCEPTION
  
    WHEN OTHERS THEN -- handles all other errors
      dbms_output.put_line('Log_message: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  
  END Log_message;


  PROCEDURE Export_Products_And_Skus ( p_lastModifiedDate varchar2, p_feedtype varchar2)
   
   IS
	nCount number;
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
  v_process_sub_cd:='Exp_PRD&_SKU';
	  
  select count(*) into nCount from all_tables where table_name ='ENDECA_EXPORT_PRODUCTS';
	if nCount > 0 Then
    execute immediate 'truncate table ENDECA_EXPORT_PRODUCTS';
  end if; 
  
  select count(*) into nCount from all_tables where table_name ='ENDECA_EXPORT_SKUS';
	if nCount > 0 Then
    execute immediate 'truncate table ENDECA_EXPORT_SKUS';
  end if;
  
	dbms_output.put_line('Export_Products_And_Skus:');
	v_message := 'Export_Products_And_Skus : START';
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
   if p_feedType   = 'PARTIAL' then
   
  --get all product which got modified (reason code 1)
insert into endeca_export_products (product_Id,reason_code) select product_Id,'1' from bbb_product where last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS');
dbms_output.put_line('Export_Products_And_Skus: inserts completed for reason code 1');
	v_message := 'Export_Products_And_Skus: inserts completed for reason code 1';
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
commit;
--Lokesh Insert collection stats here ENDECA_EXPORT_PRODUCTS
dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_PRODUCTS', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);

-- get all products for which only one sku is modified (reason code 2)
merge into endeca_export_products t using (select distinct product_Id from Dcs_Prd_Chldsku where sku_id in (select sku_id from bbb_sku where last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS'))) t1 on (t.product_id = t1.product_id)
when matched then update set t.reason_code =t.reason_code || ',' || '2'
when not matched then insert (t.product_id,t.reason_code) values (t1.product_id,'2');

dbms_output.put_line('Export_Products_And_Skus: inserts completed for reason code 2');
	v_message := 'Export_Products_And_Skus: inserts completed for reason code 2';
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
-- get all those products for set of sku's where only prices for those set of sku's is modifed but sku itself is not modified (reason code 3)

merge into endeca_export_products t using (select distinct product_id from dcs_prd_chldsku where sku_id in (select dc.sku_id from dcs_prd_chldsku dc ,bbb_price p ,dcs_price dp where
dc.sku_Id =  dp.sku_Id
and dp.price_id =  p.price_id
and p.last_mod_date> to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS'))) t1 on (t.product_id = t1.product_id)
When Matched Then Update Set T.Reason_Code =T.Reason_Code || ',' || '3'
when not matched then insert (t.product_id,t.reason_code) values (t1.product_id,'3');

dbms_output.put_line('Export_Products_And_Skus: inserts completed for reason code 3');
	v_message := 'Export_Products_And_Skus: inserts completed for reason code 3';
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	
-- get all those products and their parent collection products for which relation got modified (reason code 4)
merge into endeca_export_products t using (select product_id from
(select distinct product_id from bbb_prd_reln 
    where 
     last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') and product_id is not null)
     union
     (select distinct r.product_id from bbb_prd_reln r1,bbb_prd_prd_reln r 
    where 
    r1.product_relan_id = r.product_relan_id
     and r1.last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS'))
    )t1 on (t.product_id = t1.product_id)
When Matched Then Update Set T.Reason_Code =T.Reason_Code || ',' || '4'
when not Matched then insert (t.product_id,t.reason_code) values (t1.product_id,'4');

dbms_output.put_line('Export_Products_And_Skus: inserts completed for reason code 4');
	v_message := 'Export_Products_And_Skus: inserts completed for reason code 4';
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
-- get all those remaining child products for collection products which were not sent in above query as their relation didn't got modified (reason code 5)

begin
for id in (select product_id from endeca_export_products)
 loop
        merge into endeca_export_products t using  
         (select c.product_id from bbb_prd_reln c, bbb_prd_prd_reln r where
         r.product_id = id.product_id
         and r.product_relan_id = c.product_relan_id
         and exists(select 1 from bbb_prd_prd_reln where product_id = id.product_id))t1 
         on(t.product_id = t1.product_id) 
		 when not matched then insert (t.product_id,t.reason_code) values (t1.product_id,'5');
 end loop;
end;

dbms_output.put_line('Export_Products_And_Skus: inserts completed for reason code 5');
	v_message := 'Export_Products_And_Skus: inserts completed for reason code 5';
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
commit;
--Lokesh Insert collection stats here
dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_PRODUCTS', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);
	
insert into endeca_export_skus (sku_id) (select distinct sku_id from dcs_prd_chldsku d,endeca_export_products ep where d.product_id = ep.product_id );
commit;
--Lokesh insert collection stats here ENDECA_EXPORT_SKUS
dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_SKUS', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);

dbms_output.put_line('Export_Products_And_Skus: inserts completed for skus '); 
	v_message := 'Export_Products_And_Skus Ends: inserts completed for skus';
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

commit;
   end if;
 EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_Products_And_Skus: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_Products_And_Skus: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_Products_And_Skus;

  PROCEDURE Export_ColorColorGroups ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2)
   
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
      v_process_sub_cd:='Exp_ColColGr';
  endeca_query:='SELECT COLOR_GRP_ID, COLOR_CD FROM BBB_CORE.BBB_COLOR_COLOR_GROUP';
  
     v_message :='Export_ColorColorGroups STARTS: '||endeca_query;
     Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
     dbms_output.put_line('Export_ColorColorGroups: '||endeca_query);

  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'COLOR_GRP_ID|COLOR_CD' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_ColorColorGroups: Total rows:'||l_rows);
      v_message :='Export_ColorColorGroups ENDS: rows returned'||l_rows;
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_ColorColorGroups: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_ColorColorGroups: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_ColorColorGroups;
 -- Added as part of CR 8
  --Export_Colleges_States
PROCEDURE Export_Colleges_States(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_Col_Stat';
	  
  endeca_query:='select distinct(a.school_ver_id),b.school_name,a.college_logo,b.state,c.descrip from BBB_SCHOOLS_VER a,BBB_CORE.BBB_SCHOOLS b, bbb_states c         
        where a.school_ver_id=b.school_id and b.state=c.state_id';

     v_message :='Export_Colleges_States: STARTS : '||endeca_query;
     dbms_output.put_line('Export_Colleges_States: '||endeca_query);
     Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	 
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'SCHOOL_VER_ID|SCHOOL_NAME|SMALL_LOGO_URL|STATE_ID|DESCRIP' )
  INTO l_rows
  FROM dual;

      dbms_output.put_line('Export_Colleges_States: Total rows: '||l_rows);
      v_message :='Export_Colleges_States: ENDS: rows returned'||l_rows;
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_Colleges_States: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_Colleges_States: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_Colleges_States;
  --End 
  ---College Taxanomy
PROCEDURE Export_Colleges_Taxonomy(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
  var_num1     NUMBER;
  v_process_cd VARCHAR2(12); -- Endeca Procedure Name
  v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
  v_message VARCHAR(2500); -- message to log
  v_message_type CHAR(1); --Message type to log, A for info, I for exception
  BEGIN
   var_num1:= 69999;
    v_process_cd:='EN_FULL';
	IF p_feedType  = 'PARTIAL' THEN
		v_process_cd:='EN_PARTIAL';
	END IF;
    v_message_type:='A';
    v_process_sub_cd:='Exp_Col_Tax';
    
      
      dbms_output.put_line('Export_Colleges_Taxonomy: '||endeca_query);
      endeca_query:='select 69999+rownum,''69999'',b.school_name,a.college_logo from BBB_SCHOOLS_VER a,BBB_CORE.BBB_SCHOOLS b
        where a.school_ver_id=b.school_id';
     dbms_output.put_line('Export_Colleges_Taxonomy: '||endeca_query);
     v_message :='Export_Colleges_Taxonomy: STARTS: '||endeca_query;
     Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, '69999||SCHOOL_NAME|', 'CHILD_CAT_ID|CATEGORY_ID|DISPLAY_NAME|SMALL_LOGO_URL' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_Colleges_Taxonomy: Total rows '||l_rows);
      v_message :='Export_Colleges_Taxonomy: ENDS : Total rows returned'||l_rows;
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_Colleges_Taxonomy: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_Colleges_Taxonomy: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_Colleges_Taxonomy;
  --College Taxanomy End
-- End:: Added as part of CR 8
PROCEDURE Export_Features(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
    v_process_sub_cd:='Exp_Feature';
  endeca_query:='SELECT FACET_ID,DESCRIPTION from BBB_CORE.BBB_FACET_TYPES';
     v_message :='Export_Features: STARTS: '||endeca_query;
     Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'ID|FEATURE_NAME' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_Features: Total Rows'||l_rows);
      v_message :='Export_Features: ENDS: Total rows returned '||l_rows;
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_Features: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_Features: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);


  END Export_Features;
PROCEDURE Export_SKU_Features(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_SKU_Ftur';
	  
      endeca_query:='SELECT ITEM_ID, FACET_ID, FACET_VALUE, FACET_VALUE_ID from BBB_CORE.BBB_FACET_SKU';
      v_message :='Export_SKU_Features: STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'SKU_ID|FEATURE_ID|FEATURE_VALUE|FEATURE_VALUE_ID' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_SKU_Features: ENDS: '||l_rows);
	  v_message :='Export_SKU_Features: ENDS: Total rows returned:'||l_rows;
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_SKU_Features: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_SKU_Features: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);


  END Export_SKU_Features;
PROCEDURE Export_Product_Item_Attributes(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
  v_process_cd VARCHAR2(12); -- Endeca Procedure Name
  v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
  v_message VARCHAR(2500); -- message to log
  v_message_type CHAR(1); --Message type to log, A for info, I for exception
  nCount NUMBER;
  nCountConfig NUMBER;

  useAttrSitesTbl VARCHAR2(100);
  BEGIN
	  v_process_cd:='EN_FULL';
	  IF p_feedType  = 'PARTIAL' THEN
		v_process_cd:='EN_PARTIAL';
      END IF;
      v_message_type:='A';
      v_process_sub_cd:='Exp_PRD_ATTR'; 
      useAttrSitesTbl:='TRUE';
      v_message :='Export_Product_Item_Attributes: STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	  	
		 select count(*) into nCount from all_tables where table_name ='ENDECA_PRODUCT_ITEM_ATTR';
	 IF(nCount > 0)THEN
		execute immediate 'truncate table BBB_SWITCH_B.ENDECA_PRODUCT_ITEM_ATTR'; 
	 END IF;
 
      select count(*) into nCountConfig from BBB_SWITCH_B.BBB_CONFIG_KEY_VALUE where config_key ='EXTRACT_PRD_ATTR_SITES_DATA';
     IF(nCountConfig > 0)THEN
        select config_value into useAttrSitesTbl from BBB_SWITCH_B.BBB_CONFIG_KEY_VALUE where config_key ='EXTRACT_PRD_ATTR_SITES_DATA';
        v_message :='Export_Product_Item_Attributes: config_value for config_value: '||useAttrSitesTbl;
          dbms_output.put_line('Export_Product_Item_Attributes: config_value for config_value: '||useAttrSitesTbl);
        Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
     END IF;  

   IF(useAttrSitesTbl ='TRUE' or useAttrSitesTbl='true') THEN
      
        v_message :='Export_Product_Item_Attributes: Extracting data using non flatten BBB_ATTR_RELN_SITES table';
        dbms_output.put_line(v_message);
        Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
       endeca_query   :='SELECT distinct A.PRODUCT_ID,B.SKU_ATTRIBUTE_ID,C.SITE_ID,B.START_DATE,B.END_DATE,B.MISC_INFO             
      FROM BBB_PRD_ATTR_RELN A, BBB_ATTR_RELN B, BBB_ATTR_RELN_SITES C, BBB_PRODUCT D            
                  WHERE A.PRD_ATTR_RELN_ID=B.SKU_ATTR_RELN_ID 
                  AND A.PRD_ATTR_RELN_ID = C.SKU_ATTR_RELN_ID
      and A.PRODUCT_ID=D.PRODUCT_ID
                  AND NVL(b.start_date,sysdate) <= sysdate and NVL(b.end_date, sysdate) >= sysdate';
        IF p_feedType   = 'PARTIAL' THEN
          endeca_query := 'SELECT distinct A.PRODUCT_ID,B.SKU_ATTRIBUTE_ID,C.SITE_ID,B.START_DATE,B.END_DATE,B.MISC_INFO             
            FROM BBB_PRD_ATTR_RELN A, BBB_ATTR_RELN B, BBB_ATTR_RELN_SITES C, BBB_PRODUCT D, ENDECA_EXPORT_PRODUCTS E            
                  WHERE A.PRD_ATTR_RELN_ID=B.SKU_ATTR_RELN_ID 
                  AND A.PRD_ATTR_RELN_ID = C.SKU_ATTR_RELN_ID
            and A.PRODUCT_ID=D.PRODUCT_ID     
            AND NVL(b.start_date,sysdate) <= sysdate and NVL(b.end_date, sysdate) >= sysdate and (b.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') or A.PRODUCT_ID=E.PRODUCT_ID )';
        END IF;
   ELSIF (useAttrSitesTbl='false' or useAttrSitesTbl='FALSE')THEN


        v_message :='Export_Product_Item_Attributes: Extracting data using flatten BBB_ATTR_RELN table';
        dbms_output.put_line(v_message);
        Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	  -- Insert BedBathUS data in temp table
	insert into BBB_SWITCH_B.ENDECA_PRODUCT_ITEM_ATTR (product_id, SKU_ATTRIBUTE_ID, SITE_ID, start_date, END_DATE, 
    MISC_INFO, attr_last_modified_date) ( SELECT distinct A.PRODUCT_ID,B.SKU_ATTRIBUTE_ID,'BedBathUS', B.START_DATE,B.END_DATE,B.MISC_INFO, B.last_mod_date             
	FROM BBB_SWITCH_B.BBB_PRD_ATTR_RELN A, BBB_SWITCH_B.BBB_ATTR_RELN B, BBB_SWITCH_B.BBB_PRODUCT D            
            WHERE A.PRD_ATTR_RELN_ID=B.SKU_ATTR_RELN_ID 
            and A.PRODUCT_ID=D.PRODUCT_ID and B.BBB_Flag=1
            AND NVL(b.start_date,sysdate) <= sysdate and NVL(b.end_date, sysdate) >= sysdate);

	-- Insert BuyBuyBaby data in temp table
	insert into BBB_SWITCH_B.ENDECA_PRODUCT_ITEM_ATTR (product_id, SKU_ATTRIBUTE_ID, SITE_ID, start_date, END_DATE, 
    MISC_INFO, attr_last_modified_date) (  SELECT distinct A.PRODUCT_ID,B.SKU_ATTRIBUTE_ID,'BuyBuyBaby',B.START_DATE,B.END_DATE,B.MISC_INFO , b.last_mod_date            
	FROM BBB_SWITCH_B.BBB_PRD_ATTR_RELN A, BBB_SWITCH_B.BBB_ATTR_RELN B, BBB_SWITCH_B.BBB_PRODUCT D            
            WHERE A.PRD_ATTR_RELN_ID=B.SKU_ATTR_RELN_ID 
            and A.PRODUCT_ID=D.PRODUCT_ID and  B.BAB_Flag=1
            AND NVL(b.start_date,sysdate) <= sysdate and NVL(b.end_date, sysdate) >= sysdate);

	-- Insert BedBathCanada data in temp table
	insert into BBB_SWITCH_B.ENDECA_PRODUCT_ITEM_ATTR (product_id, SKU_ATTRIBUTE_ID, SITE_ID, start_date, END_DATE, 
    MISC_INFO, attr_last_modified_date) (  SELECT distinct A.PRODUCT_ID,B.SKU_ATTRIBUTE_ID,'BedBathCanada',B.START_DATE,B.END_DATE,B.MISC_INFO , b.last_mod_date            
	FROM BBB_SWITCH_B.BBB_PRD_ATTR_RELN A, BBB_SWITCH_B.BBB_ATTR_RELN B, BBB_SWITCH_B.BBB_PRODUCT D            
            WHERE A.PRD_ATTR_RELN_ID=B.SKU_ATTR_RELN_ID 
            and A.PRODUCT_ID=D.PRODUCT_ID and B.CA_Flag=1
            AND NVL(b.start_date,sysdate) <= sysdate and NVL(b.end_date, sysdate) >= sysdate);
commit;
--Lokesh insert collection stats here ENDECA_PRODUCT_ITEM_ATTR
dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_PRODUCT_ITEM_ATTR', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);

	endeca_query :='SELECT distinct A.PRODUCT_ID,A.SKU_ATTRIBUTE_ID,A.SITE_ID,A.START_DATE,A.END_DATE,A.MISC_INFO  from BBB_SWITCH_B.ENDECA_PRODUCT_ITEM_ATTR A';
        IF p_feedType   = 'PARTIAL' THEN
	endeca_query := 'SELECT distinct A.PRODUCT_ID,A.SKU_ATTRIBUTE_ID,A.SITE_ID,A.START_DATE,A.END_DATE,A.MISC_INFO  from BBB_SWITCH_B.ENDECA_PRODUCT_ITEM_ATTR A, BBB_SWITCH_B.ENDECA_EXPORT_PRODUCTS E  
                  where (A.PRODUCT_ID=E.PRODUCT_ID )';
        END IF;
   END IF;

	
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'PRODUCT_ID|SKU_ATTRIBUTE_ID|SITE_ID|START_DATE|END_DATE|MISC_INFO' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_Product_Item_Attributes: '||l_rows);
  	  v_message :='Export_Product_Item_Attributes: ENDS: Total rows returned: '||l_rows;
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_Product_Item_Attributes: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
    v_message_type :='I';
    v_message := 'Export_Product_Item_Attributes: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
    Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_Product_Item_Attributes;
PROCEDURE Export_Reviews(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_Reviews'; 
      v_message :='Export_Reviews: STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  endeca_query:='SELECT PRODUCT_ID,AVERAGE_OVERALL_RATING,TOTAL_REVIEW_COUNT,SITE_ID FROM BBB_CORE.BBB_BAZAAR_VOICE';
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'PRODUCT_ID|AVERAGE_OVERALL_RATING|TOTAL_REVIEW_COUNT|SITE_ID' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_Reviews: '||l_rows);
      v_message :='Export_Reviews: ENDS: Total rows returned: '||l_rows;
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_Reviews;
PROCEDURE Export_SKUs(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_SKUs';   
      v_message :='Export_SKUs: STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

			endeca_query:='select ''*#*'',a.sku_id,
                case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
                case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,
                case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, 
                case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id,
                d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id,
                d.zoom_index, d.anywhere_zoom, b.jda_dept_id, b.jda_sub_dept_id, b.jda_class, b.gift_cert_flag, b.college_id, b.sku_type,b.email_out_of_stock,b.color,b.color_group,replace(b.sku_size,''"'',''&quot;''),b.gift_wrap_eligible,b.vdc_sku_type,b.vdc_sku_message,b.upc,
                case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.assembly_time,b.is_assembly_offered,b.ltl_flag,b.order_to_ship_sla,b.case_weight
				from dcs_sku a, bbb_sku b, dcs_sku_media c, bbb_sku_media d 
where a.sku_id=b.sku_id and a.sku_id=c.sku_id(+) and a.sku_id=d.sku_id(+) '
  ;
      IF p_feedType = 'PARTIAL' THEN
			endeca_query := 'select distinct ''*#*'',b.sku_id,
                            case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
                            case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,
                            case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, 
                            case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id,
                            d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id,
                            d.zoom_index, d.anywhere_zoom, b.jda_dept_id, b.jda_sub_dept_id, b.jda_class, b.gift_cert_flag, b.college_id, b.sku_type,b.email_out_of_stock,b.color,b.color_group,replace(b.sku_size,''"'',''&quot;''),b.gift_wrap_eligible,b.vdc_sku_type,b.vdc_sku_message,b.upc,
                            case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id,b.assembly_time,b.is_assembly_offered,b.ltl_flag,b.order_to_ship_sla,b.case_weight
							from bbb_sku b,bbb_sku_media d,endeca_export_skus es
							where es.sku_id=b.sku_id and es.sku_id=d.sku_id(+) and exists (select * from dcs_Price i where i.sku_id=es.sku_id and exists (select * from bbb_price j where j.price_id=i.price_id))';
      END IF;
      SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|SKU_ID|THUMBNAIL_IMAGE_ID|SMALL_IMAGE_ID|LARGE_IMAGE_ID|MEDIUM_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|ANYWHERE_ZOOM|JDA_DEPT_ID|JDA_SUB_DEPT_ID|JDA_CLASS|GIFT_CERT_FLAG|COLLEGE_ID|SKU_TYPE|EMAIL_OUT_OF_STOCK|COLOR|COLOR_GROUP|SKU_SIZE|GIFT_WRAP_ELIGIBLE|VDC_SKU_TYPE|VDC_SKU_MESSAGE|UPC|VERT_IMAGE_ID|ASSEMBLY_TIME|IS_ASSEMBLY_OFFERED|LTL_FLAG|ORDER_TO_SHIP_SLA|CASE_WEIGHT' )
	  INTO l_rows
      FROM dual;
      dbms_output.put_line('Export_SKUs: '||l_rows);
      v_message :='Export_SKUs: ENDS : Total rows returned: '||l_rows;
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_SKUs: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_SKUs: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_SKUs;
PROCEDURE Export_Collection_SKUs(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
     v_process_sub_cd:='Exp_Coll_SKU'; 

     v_message :='EXPORT_COLLECTION_SKUS: STARTS';
     Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

    select count(*) into nCount from all_tables where table_name ='ENDECA_EXPORT_COLLECTION_SKUS';
	 IF(nCount > 0)THEN
		execute immediate 'truncate table endeca_export_collection_skus'; 
	 END IF;
  
   IF P_FEEDTYPE   = 'PARTIAL' THEN 
    execute immediate 'insert into endeca_export_collection_skus (select a.product_id,c.product_id as childprd,null,pt.site_id as product_site_id,d.sku_id,d.sequence_num,
	t.site_id as sku_site_id,null from bbb_product a,bbb_prd_prd_reln b,bbb_prd_reln c,dcs_prd_chldsku d,bbb_prod_site_translations ps,bbb_prod_translations pt,  bbb_sku_site_translations s,bbb_sku_translations t,bbb_product x, endeca_export_products ep where a.product_id=b.product_id and c.product_id=x.product_id and b.product_relan_id = c.product_relan_id and d.product_id =c.product_id and x.product_id =ps.product_id and ps.translation_id=pt.translation_id and a.collection_flag  =''1'' and  c.like_unlike = ''1'' and pt.attribute_name =''prodDisable'' and pt.attribute_value_boolean =''0'' and s.translation_id =t.translation_id and d.sku_id =s.sku_id and t.attribute_name   =''disable'' and t.attribute_value_boolean   =''0'' and a.product_id = ep.product_id and a.product_id in (select distinct(a1.product_id) from bbb_product a1,
    bbb_prd_prd_reln b1,bbb_prd_reln c1,dcs_prd_chldsku d1,bbb_sku f1,bbb_product x1 where a1.product_id=b1.product_id and b1.product_relan_id =c1.product_relan_id and d1.product_id =c1.product_id  and a1.collection_flag =''1'' and  c1.like_unlike = ''1'' and x1.product_id = c1.product_id and  d1.sku_id =f1.sku_id and (a1.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') or f1.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') or x1.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS''))))';
  ELSE 
	execute immediate 'insert into endeca_export_collection_skus (select a.product_id,c.product_id as childprd,null,pt.site_id as product_site_id,d.sku_id,d.sequence_num,
	t.site_id as sku_site_id,null from bbb_product a,bbb_prd_prd_reln b,bbb_prd_reln c,dcs_prd_chldsku d,bbb_prod_site_translations ps,bbb_prod_translations pt,  bbb_sku_site_translations s,bbb_sku_translations t,bbb_product x where a.product_id=b.product_id and c.product_id=x.product_id and b.product_relan_id = c.product_relan_id and d.product_id =c.product_id and x.product_id =ps.product_id and ps.translation_id=pt.translation_id and a.collection_flag  =''1'' and  c.like_unlike = ''1'' and pt.attribute_name =''prodDisable'' and pt.attribute_value_boolean =''0'' and s.translation_id =t.translation_id and d.sku_id =s.sku_id and t.attribute_name   =''disable'' and t.attribute_value_boolean   =''0'')';
  END IF;
  commit;
  --Lokesh insert collection stats here ENDECA_EXPORT_COLLECTION_SKUS
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_COLLECTION_SKUS', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);
  execute immediate 'update endeca_export_collection_skus cs set cs.childprd_weboffered_flag= (select nvl(pt.attribute_value_boolean,0) from bbb_prod_site_translations ps,bbb_prod_translations pt where cs.childprd=ps.product_id and ps.translation_id=pt.translation_id and pt.site_id=cs.product_site_id and pt.attribute_name  =''webOffered''), cs.sku_weboffered_flag= (select nvl(pt.attribute_value_boolean,0) from bbb_sku_site_translations ps, bbb_sku_translations pt where cs.sku_id=ps.sku_id
	and ps.translation_id=pt.translation_id and pt.site_id=cs.sku_site_id and pt.attribute_name =''webOffered'')';
  IF p_feedType   = 'PARTIAL' THEN
    execute immediate 'insert into endeca_export_collection_skus ( select a.product_id,c.product_id as childprd, nvl(x.web_offered_flag,0) as childprd_web_offered_flag,
    ''BedBathUS'' as product_site_id,d.sku_id, d.sequence_num,''BedBathUS'' as sku_site_id, nvl(f.web_offered_flag,0) as sku_web_offered_flag from bbb_product a,
    bbb_prd_prd_reln b,bbb_prd_reln c,dcs_prd_chldsku d,bbb_sku f,bbb_product x, endeca_export_products ep where a.product_id=b.product_id and b.product_relan_id =c.product_relan_id and d.product_id =c.product_id  and a.collection_flag =''1'' and  c.like_unlike = ''1'' and x.product_id = c.product_id and  x.disable_flag = ''0'' and d.sku_id =f.sku_id and f.disable_flag =''0'' and a.product_id = ep.product_id and a.product_id in (select distinct(a1.product_id) from bbb_product a1,bbb_prd_prd_reln b1,bbb_prd_reln c1,dcs_prd_chldsku d1,bbb_sku f1,bbb_product x1 where a1.product_id=b1.product_id and b1.product_relan_id =c1.product_relan_id and d1.product_id =c1.product_id  and a1.collection_flag =''1'' and  c1.like_unlike = ''1'' and x1.product_id = c1.product_id and  x1.disable_flag = ''0'' and d1.sku_id =f1.sku_id and f1.disable_flag =''0'' and (a1.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') or f1.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') or x1.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS''))))';
   ELSE
    execute immediate 'insert into endeca_export_collection_skus ( select a.product_id,c.product_id as childprd, nvl(x.web_offered_flag,0) as childprd_web_offered_flag,
    ''BedBathUS'' as product_site_id,d.sku_id, d.sequence_num,''BedBathUS'' as sku_site_id, nvl(f.web_offered_flag,0) as sku_web_offered_flag from bbb_product a,
    bbb_prd_prd_reln b,bbb_prd_reln c,dcs_prd_chldsku d,bbb_sku f,bbb_product x where a.product_id=b.product_id and b.product_relan_id =c.product_relan_id and d.product_id =c.product_id  and a.collection_flag =''1'' and  c.like_unlike = ''1'' and x.product_id = c.product_id and x.disable_flag = ''0'' and d.sku_id =f.sku_id and f.disable_flag =''0'')';
  END IF;
  commit;
  --Lokesh insert collection stats here ENDECA_EXPORT_COLLECTION_SKUS
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_COLLECTION_SKUS', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);

  endeca_query:='select distinct PRODUCT_ID,SKU_ID, SEQUENCE_NUM, SKU_SITE_ID from ENDECA_EXPORT_COLLECTION_SKUS where CHILDPRD_WEBOFFERED_FLAG=''1'' and SKU_WEBOFFERED_FLAG =''1'' order by product_id,sku_id,sequence_num';
       select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'PRODUCT_ID|SKU_ID|SEQUENCE_NUM|SITE_ID' ) into l_rows from dual; 
	   dbms_output.put_line('EXPORT_COLLECTION_SKUS: '||l_rows);
	   v_message :='EXPORT_COLLECTION_SKUS: ENDS: Total rows returned: '||l_rows;
     Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	 commit;
  EXCEPTION
      WHEN OTHERS THEN  -- handles all other errors
          dbms_output.put_line('export_collection_skus: Error!'); 
		  v_message_type:='I';
		  v_message :='export_collection_skus: Error!';
		  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
          execute Immediate 'commit';
END EXPORT_COLLECTION_SKUS;
PROCEDURE Export_Product_SKUs ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2)
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
      v_process_sub_cd:='Exp_PRD_SKUS'; 

      v_message :='Export_Product_SKUs: STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

      select count(*) into nCount from all_tables where table_name ='ENDECA_EXPORT_PROD_SKUS';
	  IF(nCount > 0)THEN
			execute immediate 'truncate table endeca_export_prod_skus'; 
	  END IF;
	  
   
      --endeca_query:='select product_id,sku_id,sequence_num from dcs_prd_chldsku where product_id in (select product_id from bbb_product where product_id not in (select product_id from bbb_prd_reln where like_unlike=1) union select child_prd_id from dcs_cat_chldprd)';
      If P_Feedtype = 'PARTIAL' Then
          execute immediate 'insert into endeca_export_prod_skus (product_id, sku_id, sequence_num, site_id, web_offered_flag)  select cs.product_id, cs.sku_id, cs.sequence_num, t.site_id, NVL(t.attribute_value_boolean,0) from bbb_sku_site_translations s, bbb_sku_translations t, dcs_prd_chldsku cs, bbb_product a, bbb_sku b, endeca_export_products ep where s.translation_id=t.translation_id and cs.sku_id=s.sku_id and s.sku_id = b.sku_id and cs.product_id = a.product_id and cs.product_id in ( select product_id from bbb_product where product_id not in ( select product_id from bbb_prd_reln where like_unlike=''1'' )union select child_prd_id from dcs_cat_chldprd) and t.attribute_name=''webOffered'' and a.product_id = ep.product_id';
      else
          execute immediate 'insert into endeca_export_prod_skus (product_id, sku_id, sequence_num, site_id, web_offered_flag)  select cs.product_id, cs.sku_id, cs.sequence_num, t.site_id, NVL(t.attribute_value_boolean,0) from bbb_sku_site_translations s, bbb_sku_translations t, dcs_prd_chldsku cs where s.translation_id=t.translation_id and cs.sku_id=s.sku_id and cs.product_id in (select product_id from bbb_product where product_id not in (select product_id from bbb_prd_reln where like_unlike=''1'') union select child_prd_id from dcs_cat_chldprd) and t.attribute_name=''webOffered''';
      end if;
  commit;	  
--Lokesh insert collection stats here ENDECA_EXPORT_PROD_SKUS
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_PROD_SKUS', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);

	  execute immediate 'update endeca_export_prod_skus t set t.disable_flag = (select min(distinct NVL(t1.attribute_value_boolean,0)) from dcs_sku,bbb_sku_site_translations,bbb_sku_translations t1 where dcs_sku.sku_id=bbb_sku_site_translations.sku_id and bbb_sku_site_translations.translation_id=t1.translation_id and dcs_sku.sku_id=t.sku_id and t1.site_id=t.site_id and t1.attribute_name = ''disable'')'; 
      
      if p_feedType = 'PARTIAL' then 
         dbms_output.put_line('Beta'); 
         v_message :='Export_Product_SKUs: Partial Feed';
		     Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
          execute immediate 'insert into endeca_export_prod_skus(product_id, sku_id, sequence_num, web_offered_flag,disable_flag, site_id) select cs.product_id, cs.sku_id, cs.sequence_num, s.web_offered_flag, s.disable_flag,''BedBathUS'' from bbb_sku s, dcs_prd_chldsku cs, bbb_product a, endeca_export_products ep where cs.sku_id=s.sku_id and cs.product_id in (select product_id from bbb_product where product_id not in (select product_id from bbb_prd_reln where like_unlike=''1'')union select child_prd_id from dcs_cat_chldprd) and a.product_id = cs.product_id and a.product_id = ep.product_id';
      else 
          execute immediate 'insert into endeca_export_prod_skus(product_id, sku_id, sequence_num, web_offered_flag,disable_flag, site_id) select cs.product_id, cs.sku_id, cs.sequence_num, s.web_offered_flag, s.disable_flag,''BedBathUS'' from bbb_sku s, dcs_prd_chldsku cs where cs.sku_id=s.sku_id and cs.product_id in (select product_id from bbb_product where product_id not in (select product_id from bbb_prd_reln where like_unlike=''1'') union select child_prd_id from dcs_cat_chldprd)';
      end if;
  commit;	  
--Lokesh insert collection stats here ENDECA_EXPORT_PROD_SKUS
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_PROD_SKUS', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);
      
	  endeca_query:='select distinct product_id, sku_id,sequence_num,site_id from endeca_export_prod_skus
		where disable_flag =''0'' and web_offered_flag =''1'' order by product_id,site_id,sequence_num'; 
     
      select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'PRODUCT_ID|SKU_ID|SEQUENCE_NUM|SITE_ID' ) into l_rows from dual; 
      
      dbms_output.put_line('Export_Product_SKUs: '||l_rows); 
  	  v_message :='Export_Product_SKUs: ENDS: Total rows returned:'||l_rows;
  	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	  commit;
  EXCEPTION
      WHEN OTHERS THEN  -- handles all other errors
          dbms_output.put_line('Export_Product_Skus: Error!'); 
		  v_message :='Export_Product_SKUs: Error!';
		  v_message_type :='I';
		  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
          execute Immediate 'commit';

  END Export_Product_SKUs;
PROCEDURE Export_UnlikeProducts(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_UlikePrd'; 
      v_message :='Export_UnlikeProducts: STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  endeca_query:=
  'select distinct ''*#*'', a.product_id,gift_cert,collection_flag,swatch_flag,brand_id,b.INTL_RESTRICTED,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code,
            case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,
            case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id,
            case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
            case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id,
            d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index, 
            case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail,
            d.anywhere_zoom, case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end,
            case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id,g.keywords_string
from dcs_product a, bbb_product b, dcs_prd_media c, bbb_prd_media d, BBB_CORE.BBB_SCHOOLS f,bbb_prd_keywords g
            where a.product_id = b.product_id and a.product_id=c.product_id(+)
            and a.product_id=d.product_id(+) and a.product_id = g.product_id(+) and b.college_id=f.school_id(+)
            and a.product_id in ( select distinct c1.product_id from bbb_product a1, bbb_prd_prd_reln b1, bbb_prd_reln c1  where a1.product_id=b1.product_id
and b1.product_relan_id=c1.product_relan_id and c1.like_unlike=''0'') '
  ;
  IF p_feedType  = 'PARTIAL' THEN
    endeca_query:=
    'select distinct''*#*'',
	a.product_id,gift_cert,collection_flag,swatch_flag,brand_id,b.INTL_RESTRICTED,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code, case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id, case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id, case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index,  case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail, d.anywhere_zoom, case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end, case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id, k.keywords_string from dcs_product a, bbb_product b, dcs_prd_media c, bbb_prd_media d, BBB_CORE.BBB_SCHOOLS f, dcs_prd_chldsku g, bbb_sku h, dcs_cat_chldprd i, bbb_category j,bbb_prd_keywords k where a.product_id = b.product_id and a.product_id=c.product_id(+) and a.product_id=d.product_id(+) and b.college_id=f.school_id(+) and a.product_id = k.product_id(+) and a.product_id=g.product_id(+) and g.sku_id=h.sku_id(+) and a.product_id=i.child_prd_id (+) and i.category_id = j.category_id and a.product_id in ( select distinct c1.product_id from bbb_product a1, bbb_prd_prd_reln b1, bbb_prd_reln c1  where a1.product_id=b1.product_id and b1.product_relan_id=c1.product_relan_id and c1.like_unlike=''0'') and ( b.last_mod_date > to_date('''
    || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') or h.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'')  or j.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') ) ' ;
  END IF;
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|INTL_RESTRICTED|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|VERT_IMAGE_ID|SHOP_GUIDE_ID|META_KEYWORDS' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_UnlikeProducts: '||l_rows);
	  v_message :='Export_UnlikeProducts: ENDS: Total rows returned:'||l_rows;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_UnlikeProducts: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_UnlikeProducts: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);


  END Export_UnlikeProducts;
PROCEDURE Export_Accessories(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_Accessos'; 
      v_message :='Export_Accessories: STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  endeca_query:=
  'select distinct ''*#*'',a.product_id,gift_cert,collection_flag,swatch_flag,brand_id,b.INTL_RESTRICTED,b.email_out_of_stock,            
            show_images_in_collection,f.school_name,rollup_type_code,
            case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id, 
            case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id,
            case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
            case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id,
            d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index, 
            case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail,
            anywhere_zoom,case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end,
            case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id,g.keywords_string
from dcs_product a, bbb_product b, dcs_prd_media c, bbb_prd_media d, BBB_CORE.BBB_SCHOOLS f, bbb_prd_keywords g            
            where a.product_id = b.product_id and a.product_id=c.product_id(+)
            and a.product_id=d.product_id(+) and b.college_id=f.school_id(+) and a.product_id = g.product_id(+)
            and a.product_id in ( select distinct c.product_id from bbb_product a, bbb_prd_prd_reln b, bbb_prd_reln c  where a.product_id=b.product_id
and b.product_relan_id=c.product_relan_id and a.lead_prd_flag=''1'')'
  ;
  IF p_feedType  = 'PARTIAL' THEN
    endeca_query:=
    'select distinct ''*#*'',a.product_id,gift_cert,collection_flag,swatch_flag,brand_id,b.INTL_RESTRICTED,b.email_out_of_stock, show_images_in_collection,f.school_name,rollup_type_code, case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id, case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index,  case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail, anywhere_zoom,case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end, case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id, k.keywords_string from dcs_product a, bbb_product b, dcs_prd_media c, bbb_prd_media d, BBB_CORE.BBB_SCHOOLS f, dcs_prd_chldsku g, bbb_sku h, dcs_cat_chldprd i, bbb_category j, bbb_prd_keywords k where a.product_id = b.product_id and a.product_id=c.product_id(+) and a.product_id=d.product_id(+) and b.college_id=f.school_id(+) and a.product_id=g.product_id(+) and a.product_id=k.product_id(+) and g.sku_id=h.sku_id(+) and a.product_id=i.child_prd_id(+) and i.category_id = j.category_id and a.product_id in ( select distinct c.product_id  from bbb_product a, bbb_prd_prd_reln b, bbb_prd_reln c  where a.product_id=b.product_id and b.product_relan_id=c.product_relan_id and a.lead_prd_flag=''1'') and ( b.last_mod_date > to_date('''
    || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') or h.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'')  or j.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') ) ' ;
  END IF;
      dbms_output.put_line('Export_Accessories: '||endeca_query);
	  v_message :='Export_Accessories: Query:'||endeca_query;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|INTL_RESTRICTED|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|VERT_IMAGE_ID|SHOP_GUIDE_ID|META_KEYWORDS' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_Accessories:Total Rows returned: '||l_rows);
	  v_message :='Export_Accessories: ENDS :Total rows returned:'||l_rows;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_Accessories: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_Accessories: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);


  END Export_Accessories;
PROCEDURE Export_Lead_Products(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_Lead_PRD'; 
      v_message :='Export_Lead_Products: STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  endeca_query:=
  'select distinct ''*#*'',a.product_id,gift_cert,collection_flag,swatch_flag,brand_id,b.INTL_RESTRICTED,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code,      
        case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id, 
        case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id,
        case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
        case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id,
        d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index, 
        case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail,
        anywhere_zoom, case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end,
        case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id, g.keywords_string
        from dcs_product a, bbb_product b, dcs_prd_media c, bbb_prd_media d, bbb_prd_prd_reln r, BBB_CORE.BBB_SCHOOLS f, bbb_prd_keywords g 
        where a.product_id = b.product_id and a.product_id = r.product_id and a.product_id=c.product_id(+) and a.product_id=d.product_id(+) 
and b.college_id=f.school_id(+) and a.product_id=g.product_id(+) and b.lead_prd_flag=''1'' '
  ;
  IF p_feedType  = 'PARTIAL' THEN
    endeca_query:=
    'select distinct ''*#*'',a.product_id,gift_cert,collection_flag,swatch_flag,brand_id,b.INTL_RESTRICTED,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code, case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id, case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index,  case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail, anywhere_zoom, case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end, case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id, k.keywords_string from dcs_product a, bbb_product b, dcs_prd_media c, bbb_prd_media d, bbb_prd_prd_reln r, BBB_CORE.BBB_SCHOOLS f, dcs_prd_chldsku g, bbb_sku h, dcs_cat_chldprd i, bbb_category j, bbb_prd_keywords k where a.product_id = b.product_id and a.product_id = r.product_id and a.product_id=g.product_id(+) and g.sku_id=h.sku_id(+) and a.product_id=c.product_id(+) and a.product_id=d.product_id(+) and a.product_id=i.child_prd_id (+) and a.product_id = k.product_id(+) and i.category_id = j.category_id and b.college_id=f.school_id(+) and b.lead_prd_flag=''1''  and ( b.last_mod_date > to_date('''
    || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') or h.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'')  or j.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') ) ' ;
  END IF;
    dbms_output.put_line('endeca_query: '||endeca_query);
	v_message :='Export_Lead_Products: Query:'||endeca_query;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|INTL_RESTRICTED|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|VERT_IMAGE_ID|SHOP_GUIDE_ID|META_KEYWORDS' )
  INTO l_rows
  FROM dual;
    dbms_output.put_line('Export_Lead_Products: '||l_rows);
	v_message :='Export_Lead_Products: ENDS: Total rows returned:'||l_rows;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_Lead_Products: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_Lead_Products: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_Lead_Products;
PROCEDURE Export_Simple_Products(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_SMPL_PRD'; 
      v_message :='Export_Simple_Products: STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  endeca_query:=
  'select distinct ''*#*'',a.product_id,gift_cert,collection_flag,swatch_flag,brand_id,b.INTL_RESTRICTED,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code,        
        case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id, 
        case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id,
        case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
        case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id,
        d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index, 
        case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail,
        anywhere_zoom,case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end,
        case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id, g.keywords_string
        from dcs_product a, bbb_product b, dcs_prd_media c, bbb_prd_media d, BBB_CORE.BBB_SCHOOLS f, bbb_prd_keywords g 
        where a.product_id = b.product_id and a.product_id=c.product_id(+) and a.product_id=d.product_id(+) and a.product_id = g.product_id(+)
and b.college_id=f.school_id(+) and b.collection_flag=''0'' and b.lead_prd_flag = ''0'' and exists(select 1 from dcs_cat_chldprd a1 where a1.child_prd_id = a.product_id) '
  ;
  IF p_feedType  = 'PARTIAL' THEN
    endeca_query:=
    'select distinct ''*#*'',a.product_id,gift_cert,collection_flag,swatch_flag,brand_id,b.INTL_RESTRICTED,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code, case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id, case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index,  case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail, anywhere_zoom,case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end, case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id, k.keywords_string from dcs_product a, bbb_product b, dcs_prd_media c, bbb_prd_media d, BBB_CORE.BBB_SCHOOLS f, dcs_prd_chldsku g, bbb_sku h, dcs_cat_chldprd i, bbb_category j, bbb_prd_keywords k where a.product_id = b.product_id and a.product_id=c.product_id(+) and a.product_id=d.product_id(+)  and a.product_id=g.product_id(+) and g.sku_id=h.sku_id(+) and a.product_id = k.product_id(+) and a.product_id=i.child_prd_id (+) and i.category_id = j.category_id and b.college_id=f.school_id(+) and b.collection_flag=''0'' and b.lead_prd_flag = ''0'' and exists (select 1 from dcs_cat_chldprd a1 where a1.child_prd_id = a.product_id)  and ( b.last_mod_date > to_date('''
    || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') or h.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'')  or j.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') ) ' ;
  END IF;
    dbms_output.put_line('Export_Simple_Products: '||endeca_query);
	v_message :='Export_Simple_Products: Query:'||endeca_query;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|INTL_RESTRICTED|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|VERT_IMAGE_ID|SHOP_GUIDE_ID|META_KEYWORDS' )
  INTO l_rows
  FROM dual;
    dbms_output.put_line('Export_Simple_Products: '||l_rows);
	v_message :='Export_Simple_Products: ENDS: Rows returned:'||l_rows;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_Simple_Products: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_Simple_Products: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_Simple_Products;
PROCEDURE Export_Collection_Products(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_Col_PRD'; 
    dbms_output.put_line('Export_Collection_Products: '||p_feedType);
	  v_message :='Export_Collection_Products: STARTS: p_feedType:'||p_feedType;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  endeca_query:=
  'select distinct ''*#*'',a.product_id,gift_cert,collection_flag,swatch_flag,brand_id,b.INTL_RESTRICTED,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code,        
        case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id, 
        case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id,
        case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id, 
        case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id,
        d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index, 
        case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail,
        anywhere_zoom,case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end,
        case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id, g.keywords_string
        from dcs_product a, bbb_product b, dcs_prd_media c, bbb_prd_media d, bbb_prd_prd_reln r, BBB_CORE.BBB_SCHOOLS f, bbb_prd_keywords g 
        where a.product_id = b.product_id and a.product_id = r.product_id and a.product_id=c.product_id(+) and a.product_id=d.product_id(+) and a.product_id = g.product_id(+) 
and b.college_id=f.school_id(+) and b.collection_flag=''1'' '
  ;
  IF p_feedType = 'PARTIAL' THEN
        dbms_output.put_line('Export_Collection_Products: Creating Partial query now');
		v_message :='Export_Collection_Products: Creating Partial query now';
		Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
    endeca_query:=
    'select distinct ''*#*'',a.product_id,gift_cert,collection_flag,swatch_flag,brand_id,b.INTL_RESTRICTED,b.email_out_of_stock,show_images_in_collection,f.school_name,rollup_type_code, case when b.scene7_url is not null then b.scene7_url||''?$63$'' else '''' end as small_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$146$'' else '''' end as medium_image_id, case when b.scene7_url is not null then b.scene7_url||''?$'' else '''' end as thumbnail_image_id,  case when b.scene7_url is not null then b.scene7_url||''?$478$'' else '''' end as large_image_id, d.swatch_image_id as swatch_image_id, d.zoom_image_id as zoom_image_id, d.zoom_index,  case when b.scene7_url is not null then b.scene7_url||''?$83$'' else '''' end as collection_thumbnail, anywhere_zoom,case when b.seo_url like ''%.jsp%'' then '''' else replace(b.seo_url,''%'',''%25'') end, case when b.scene7_url is not null then b.scene7_url||''?$146v$'' else '''' end as vert_image_id, b.shop_guide_id, m.keywords_string from dcs_product a, bbb_product b, dcs_prd_media c, bbb_prd_media d, bbb_prd_prd_reln r, BBB_CORE.BBB_SCHOOLS f, dcs_price h, dcs_price i, bbb_price h1, dcs_cat_chldprd k, bbb_category l, bbb_prd_keywords m where a.product_id = b.product_id  and a.product_id = r.product_id(+)   and a.product_id=c.product_id(+)  and a.product_id=d.product_id(+)   and b.sku_low_price = h.sku_id (+)  and b.sku_high_price = i.sku_id (+)  and h.price_id = h1.price_id and b.college_id=f.school_id(+) and a.product_id = m.product_id(+) and a.product_id=k.child_prd_id (+) and k.category_id = l.category_id (+) and b.collection_flag=''1''  and ( b.last_mod_date > to_date('''
    || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'')  or h1.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'')  or l.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') ) ' ;
  END IF;
    dbms_output.put_line('Export_Collection_Products: '||endeca_query);
	v_message :='Export_Collection_Products: endeca_query:'||endeca_query;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|GIFT_CERT|COLLECTION_FLAG|SWATCH_FLAG|BRAND_ID|INTL_RESTRICTED|EMAIL_OUT_OF_STOCK|SHOW_IMAGES_IN_COLLECTION|SCHOOL_NAME|ROLLUP_TYPE_CODE|SMALL_IMAGE_ID|MEDIUM_IMAGE_ID|THUMBNAIL_IMAGE_ID|LARGE_IMAGE_ID|SWATCH_IMAGE_ID|ZOOM_IMAGE_ID|ZOOM_INDEX|COLLECTION_THUMBNAIL|ANYWHERE_ZOOM|SEO_URL|VERT_IMAGE_ID|SHOP_GUIDE_ID|META_KEYWORDS' )
  INTO l_rows
  FROM dual;
    dbms_output.put_line('Export_Collection_Products: '||l_rows);
	v_message :='Export_Collection_Products: ENDS: Total rows:'||l_rows;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_Collection_Products: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_Collection_Products: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_Collection_Products;
PROCEDURE Export_Brands(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_Brands'; 
      endeca_query:='select ''*#*'',a.brand_id,brand_descrip,brand_image,site_id from BBB_BRANDS a, bbb_brand_sites b where a.brand_id=b.brand_id';
       dbms_output.put_line('Export_Brands: '||endeca_query);     
  	  v_message :='Export_Brands: STARTS: endeca_query:'||endeca_query;
  	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  IF p_feedType   = 'PARTIAL' THEN
	endeca_query := 'select distinct ''*#*'',a.brand_id,brand_descrip,brand_image,site_id from BBB_BRANDS a, bbb_brand_sites b,bbb_product c, endeca_export_products d where a.brand_id=b.brand_id and c.brand_id=a.brand_id and (a.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'')or c.product_id=d.product_id )';
  END IF;
      dbms_output.put_line('Export_Brands: '||endeca_query);  
	  v_message :='Export_Brands: endeca_query:'||endeca_query;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|BRAND_ID|BRAND_DESCRIP|BRAND_IMAGE|SITE_ID' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_Brands: '||l_rows);
	  v_message :='Export_Brands: ENDS: Total rows:'||l_rows;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_Brands: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_Brands: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);


  END Export_Brands;
PROCEDURE Export_SKU_Item_Attributes(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_SKU_IAtb'; 
      v_message :='Export_SKU_Item_Attributes: STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

	select count(*) into nCount from all_tables where table_name ='ENDECA_SKU_ITEM_ATTR';
	 IF(nCount > 0)THEN
		execute immediate 'truncate table BBB_SWITCH_B.ENDECA_SKU_ITEM_ATTR'; 
	 END IF;
	
	-- Insert site specific data into temporary Table Start	  
	insert into BBB_SWITCH_B.ENDECA_SKU_ITEM_ATTR (sku_id, SKU_ATTRIBUTE_ID, SITE_ID, start_date, END_DATE, MISC_INFO)
		( select a.sku_id, b.sku_attribute_id,'BedBathUS', b.start_date, b.end_date, b.misc_info 
		from BBB_SWITCH_B.BBB_SKU_ATTR_RELN a, BBB_SWITCH_B.BBB_ATTR_RELN b
		where a.sku_attr_reln_id=b.sku_attr_reln_id and b.BBB_Flag=1
		and NVL(b.start_date,sysdate) <= sysdate 
		and NVL(b.end_date, sysdate) >= sysdate); 

	insert into BBB_SWITCH_B.ENDECA_SKU_ITEM_ATTR (sku_id, SKU_ATTRIBUTE_ID, SITE_ID, start_date, END_DATE, MISC_INFO)
		( select a.sku_id, b.sku_attribute_id,'BuyBuyBaby', b.start_date, b.end_date, b.misc_info 
		from BBB_SWITCH_B.BBB_SKU_ATTR_RELN a, BBB_SWITCH_B.BBB_ATTR_RELN b
		where a.sku_attr_reln_id=b.sku_attr_reln_id and b.BAB_Flag=1
		and NVL(b.start_date,sysdate) <= sysdate 
		and NVL(b.end_date, sysdate) >= sysdate); 
		
	insert into BBB_SWITCH_B.ENDECA_SKU_ITEM_ATTR (sku_id, SKU_ATTRIBUTE_ID, SITE_ID, start_date, END_DATE, MISC_INFO)
		( select a.sku_id, b.sku_attribute_id,'BedBathCanada', b.start_date, b.end_date, b.misc_info 
		from BBB_SWITCH_B.BBB_SKU_ATTR_RELN a, BBB_SWITCH_B.BBB_ATTR_RELN b
		where a.sku_attr_reln_id=b.sku_attr_reln_id and b.CA_Flag=1
		and NVL(b.start_date,sysdate) <= sysdate 
		and NVL(b.end_date, sysdate) >= sysdate); 
	--Insert site specific data into temporary Table End
  commit;
--Lokesh insert collection stats here ENDECA_SKU_ITEM_ATTR
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_SKU_ITEM_ATTR', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);

  endeca_query   :=	'select sku_id, sku_attribute_id, site_id, start_date, end_date, misc_info from BBB_SWITCH_B.ENDECA_SKU_ITEM_ATTR';
  If P_Feedtype   = 'PARTIAL' Then
    endeca_query :=	'select a.sku_id, a.sku_attribute_id, a.site_id, a.start_date, a.end_date, a.misc_info from BBB_SWITCH_B.ENDECA_SKU_ITEM_ATTR a, BBB_SWITCH_B.BBB_SKU d ,
					BBB_SWITCH_B.endeca_export_skus es where a.sku_id = es.sku_id and a.sku_id=d.sku_id';
  END IF;
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'SKU_ID|SKU_ATTRIBUTE_ID|SITE_ID|START_DATE|END_DATE|MISC_INFO' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_SKU_Item_Attributes: '||l_rows);
	  v_message :='Export_SKU_Item_Attributes: ENDS: Total rows:'||l_rows;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_SKU_Item_Attributes: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_SKU_Item_Attributes: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);


  END Export_SKU_Item_Attributes;
PROCEDURE Export_Item_Attributes(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_Item_Atb'; 
      v_message :='Export_Item_Attributes: STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);      
      endeca_query:='select ''*#*'',a.sku_attribute_id, replace (display_descrip, ''"'',''''''''), image_url,action_url, place_holder, intl_flag, priority, site_id, start_date, end_date,
            CASE WHEN INSTR( display_descrip, ''>'') > 1 THEN SUBSTR( display_descrip, INSTR( display_descrip, ''>'') + 1, INSTR( display_descrip, ''</'') - INSTR( display_descrip, ''>'') - 1 ) ELSE display_descrip END ATTRIBUTE_DISPLAY_NAME
            from BBB_SKU_ATTRIBUTES_INFO a, BBB_SKU_ATTRIBUTE_SITES b where a.sku_attribute_id = b.sku_attribute_id 
            and NVL(a.start_date,sysdate) <= sysdate and NVL(a.end_date, sysdate) >= sysdate and place_holder is not null';
  IF p_feedType  = 'PARTIAL' THEN
          endeca_query:='select distinct ''*#*'',c.sku_attribute_id, replace (display_descrip, ''"'',''''''''), a.image_url, a.action_url, a.place_holder, a.intl_flag, a.priority, b.site_id, a.start_date, a.end_date,
                CASE WHEN INSTR( display_descrip, ''>'') > 1 THEN SUBSTR( display_descrip, INSTR( display_descrip, ''>'') + 1, INSTR( display_descrip, ''</'') - INSTR( display_descrip, ''>'') - 1 ) ELSE display_descrip END ATTRIBUTE_DISPLAY_NAME
from BBB_SKU_ATTRIBUTES_INFO a, BBB_SKU_ATTRIBUTE_SITES b, BBB_ATTR_RELN c,BBB_PRODUCT d,BBB_PRD_ATTR_RELN f                
where a.sku_attribute_id = b.sku_attribute_id and b.sku_attribute_id = c.sku_attribute_id and f.prd_attr_reln_id=c.sku_attr_reln_id and f.product_id=d.product_id                
                and NVL(a.start_date,sysdate) <= sysdate and NVL(a.end_date, sysdate) >= sysdate and place_holder is not null
and (c.last_mod_date > to_date(''' || p_lastModifiedDate ||
    ''',''YYYY-MM-DD HH24:MI:SS'') or d.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS''))';
  END IF;
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|SKU_ATTRIBUTE_ID|DISPLAY_DESCRIP|IMAGE_URL|ACTION_URL|PLACE_HOLDER|INTL_FLAG|PRIORITY|SITE_ID|START_DATE|END_DATE|ATTRIBUTE_DISPLAY_NAME' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_Item_Attributes: '||l_rows);
	  v_message :='Export_Item_Attributes: ENDS: Total rows:'||l_rows;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_Item_Attributes: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_Item_Attributes: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_Item_Attributes;
PROCEDURE Export_Prod_Media_Sites(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_PRDMEDIA'; 
      endeca_query:='select b.product_id,b.media_id,c.site_id from BBB_PRD_OTHER_MEDIA b,BBB_OTHER_MEDIA_SITES c where b.media_id = c.media_id(+)';
      v_message :='Export_Prod_Media_Sites: STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  IF p_feedType   = 'PARTIAL' THEN
          endeca_query := 'select b.product_id,b.media_id,c.site_id from BBB_PRD_OTHER_MEDIA b,BBB_OTHER_MEDIA_SITES c,BBB_PRODUCT a where b.media_id = c.media_id(+) and a.product_id = b.product_id and a.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'')' ;
  END IF;
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'PRODUCT_ID|MEDIA_ID|SITE_ID' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_Prod_Media_Sites: '||l_rows);
	  v_message :='Export_Prod_Media_Sites: ENDS: Total rows:'||l_rows;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  END Export_Prod_Media_Sites;
PROCEDURE Export_Media(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_MEDIA'; 
      endeca_query:='select ''*#*'',media_id,media_type,provider_id,media_source,media_description,comments,media_transcript,widget_id from bbb_other_media where NVL(start_date,sysdate) <= sysdate and NVL(end_date, sysdate) >= sysdate and provider_id=''12''';
  IF p_feedType  = 'PARTIAL' THEN
          endeca_query:='select ''*#*'',a.media_id,a.media_type,a.provider_id,a.media_source,a.media_description,a.comments,a.media_transcript,a.widget_id 
                        from bbb_other_media a, BBB_PRD_OTHER_MEDIA b, BBB_PRODUCT c
                        where a.media_id = b.media_id and b.product_id = c.product_id
                        and a.provider_id = ''12''
                        and NVL(a.start_date,sysdate) <= sysdate and NVL(a.end_date, sysdate) >= sysdate
                        and ( a.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') 
                            or c.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') )' ;
  END IF;
      dbms_output.put_line('Export_Media: '||endeca_query);
	  v_message :='Export_Media: endeca_query:STARTS:'||endeca_query;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|MEDIA_ID|MEDIA_TYPE|PROVIDER_ID|MEDIA_SOURCE|MEDIA_DESCRIPTION|COMMENTS|MEDIA_TRANSCRIPT|WIDGET_ID' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_Media: '||l_rows);
	  v_message :='Export_Media:ENDS:'||l_rows;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_Media: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_Media: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_Media;
PROCEDURE Export_Category_Products ( p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function
    endeca_query varchar(2000); -- pkg var to capture the SQL query
	nCount NUMBER;
	v_process_cd VARCHAR2(12); -- Endeca Procedure Name
	v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
	v_message VARCHAR(2500); -- message to log
	v_message_type CHAR(1); --Message type to log, A for info, I for exception
	
	useOldCatProductSeq VARCHAR2(100);
	nCountConf NUMBER;
	      
	
  BEGIN
		v_process_cd:='EN_FULL';
		IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
		END IF;
      v_message_type:='A';
      v_process_sub_cd:='Exp_Cat_Prd'; 
      v_message :='Export_category_products:STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	  useOldCatProductSeq:='TRUE';
	  
	 
       select count(*) into nCountConf from BBB_SWITCH_B.BBB_CONFIG_KEY_VALUE where config_key ='IS_USE_OLD_CAT_PRODUCT_SEQ';
     IF(nCountConf > 0)THEN
        select config_value into useOldCatProductSeq from BBB_SWITCH_B.BBB_CONFIG_KEY_VALUE where config_key ='IS_USE_OLD_CAT_PRODUCT_SEQ';
        v_message :='Export_Product_Item_Attributes: config_value for config_value: '||useOldCatProductSeq;
          dbms_output.put_line('Export_Product_Item_Attributes: config_value for config_value: '||useOldCatProductSeq);
        Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
     END IF;
	 

	SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_EXPORT_CAT_PRODS';
	IF(nCount > 0)THEN
		EXECUTE immediate 'truncate table ENDECA_EXPORT_CAT_PRODS ';
    END IF;  
  IF(useOldCatProductSeq ='FALSE' or useOldCatProductSeq='false') THEN
    if p_feedType = 'PARTIAL' then
		insert into ENDECA_EXPORT_CAT_PRODS (cat_id, prod_id, seq_num) 
			select distinct a.category_id, a.child_prd_id, a.sequence_num
				from dcs_cat_chldprd a, BBB_CATEGORY b, BBB_PRODUCT c, dcs_prd_chldsku g, bbb_sku h, dcs_price h1, dcs_price i, bbb_price h2, bbb_price i1 
				where a.category_id = b.category_id and c.product_id = a.child_prd_id 
				and c.product_id = g.product_id (+) and g.sku_id = h.sku_id (+) and c.sku_low_price = h1.sku_id (+) and c.sku_high_price = i.sku_id (+) 
				and h1.Price_Id = h2.Price_Id (+) and i.Price_Id = i1.Price_Id (+)
				and ( b.Last_Mod_Date > To_Date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') 
				  or c.Last_Mod_Date > To_Date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') 
				  or h.Last_Mod_Date > To_Date(p_lastModifiedDate,'Yyyy-Mm-Dd Hh24:Mi:Ss') 
				  or h2.Last_Mod_Date > To_Date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') 
				  or i1.last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') 
				  )order by a.category_id, a.sequence_num;
		
		BEGIN
			FOR id IN (SELECT cat_id, prod_id FROM ENDECA_EXPORT_CAT_PRODS)
			LOOP
			  update ENDECA_EXPORT_CAT_PRODS x set x.seq_num = 
			  NVL((select sequence_num  FROM bbb_cat_product_seq t where id.cat_id = t.category_id and t.child_prd_id = id.prod_id), '999') 
			  where x.cat_id = id.cat_id and id.prod_id = x.prod_id;
			END LOOP;
		END;
	else
		insert into ENDECA_EXPORT_CAT_PRODS (cat_id, prod_id, seq_num) 
		SELECT a.category_id,a.child_prd_id, NVL(b.sequence_num,'999') from dcs_cat_chldprd a LEFT JOIN bbb_cat_product_seq b ON a.category_id = b.category_id and a.child_prd_id=b.child_prd_id;
	end if;
	else
	 if p_feedType = 'PARTIAL' then
		insert into ENDECA_EXPORT_CAT_PRODS (cat_id, prod_id, seq_num) 
			select distinct a.category_id, a.child_prd_id, a.sequence_num
				from dcs_cat_chldprd a, BBB_CATEGORY b, BBB_PRODUCT c, dcs_prd_chldsku g, bbb_sku h, dcs_price h1, dcs_price i, bbb_price h2, bbb_price i1 
				where a.category_id = b.category_id and c.product_id = a.child_prd_id 
				and c.product_id = g.product_id (+) and g.sku_id = h.sku_id (+) and c.sku_low_price = h1.sku_id (+) and c.sku_high_price = i.sku_id (+) 
				and h1.Price_Id = h2.Price_Id (+) and i.Price_Id = i1.Price_Id (+)
				and ( b.Last_Mod_Date > To_Date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') 
				  or c.Last_Mod_Date > To_Date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') 
				  or h.Last_Mod_Date > To_Date(p_lastModifiedDate,'Yyyy-Mm-Dd Hh24:Mi:Ss') 
				  or h2.Last_Mod_Date > To_Date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') 
				  or i1.last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') 
				  )order by a.category_id, a.sequence_num;
		
		BEGIN
			FOR id IN (SELECT cat_id, prod_id FROM ENDECA_EXPORT_CAT_PRODS)
			LOOP
			  update ENDECA_EXPORT_CAT_PRODS x set x.seq_num = 
			  NVL((select sequence_num  FROM cat_product_seq t where id.cat_id = t.category_id and t.child_prd_id = id.prod_id), '999') 
			  where x.cat_id = id.cat_id and id.prod_id = x.prod_id;
			END LOOP;
		END;
	else
		insert into ENDECA_EXPORT_CAT_PRODS (cat_id, prod_id, seq_num) 
		SELECT a.category_id,a.child_prd_id, NVL(b.sequence_num,'999') from dcs_cat_chldprd a LEFT JOIN cat_product_seq b ON a.category_id = b.category_id and a.child_prd_id=b.child_prd_id;
	end if;
	
	end if;
	
	commit;
	--Lokesh insert stats collection here ENDECA_EXPORT_CAT_PRODS
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_CAT_PRODS', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);
	endeca_query:='select cat_id,prod_id,seq_num from ENDECA_EXPORT_CAT_PRODS';
	dbms_output.put_line('Export_category_products: '||endeca_query);
	v_message :='Export_category_products:Query:'||endeca_query;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'CATEGORY_ID|CHILD_PRD_ID|SEQUENCE_NUM' ) into l_rows from dual;
    dbms_output.put_line('Export_Category_Products: '||l_rows);
	v_message :='Export_category_products:ENDS: Total rows:'||l_rows;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_category_products: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_category_products: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);


END Export_Category_Products;
PROCEDURE Export_BBB_Taxonomy(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_BBB_Tax'; 
	  
      endeca_query:='select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
            from dcs_category a, bbb_category b, dcs_cat_chldcat c where a.category_id = b.category_id 
            and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''1%'' and c.category_id like ''1%''
            order by a.category_id';
  IF p_feedType   = 'PARTIAL' THEN
          endeca_query := 'select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
            from dcs_category a, bbb_category b, dcs_cat_chldcat c where a.category_id = b.category_id 
            and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''1%'' and c.category_id like ''1%''
            and b.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') order by a.category_id' ;
  END IF;
    dbms_output.put_line('Export_BBB_Taxonomy: '||endeca_query);
	v_message :='Export_BBB_Taxonomy:STARTS:Query: '||endeca_query;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
		  
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, '10000||Bed Bath & Beyond||||', 'CHILD_CAT_ID|CATEGORY_ID|DISPLAY_NAME|IS_COLLEGE|NODE_TYPE|PHANTOM_CATEGORY|GUIDE_ID' )
  INTO l_rows
  FROM dual;
    dbms_output.put_line('Export_BBB_Taxonomy: '||l_rows);
	v_message :='Export_BBB_Taxonomy:ENDS :Total rows:'||l_rows;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  
EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_BBB_Taxonomy: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_BBB_Taxonomy: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);


  END Export_BBB_Taxonomy;
PROCEDURE Export_BAB_Taxonomy(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_BAB_TAX'; 
      endeca_query:='select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
            from dcs_category a, bbb_category b, dcs_cat_chldcat c where a.category_id = b.category_id 
            and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''3%'' and c.category_id like ''3%''
            order by a.category_id';
      dbms_output.put_line('Export_BAB_Taxonomy: '||endeca_query);
  	  v_message :='Export_BAB_Taxonomy:STARTS:Query: '||endeca_query;
  	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  IF p_feedType   = 'PARTIAL' THEN
          endeca_query := 'select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
            from dcs_category a, bbb_category b, dcs_cat_chldcat c where a.category_id = b.category_id 
            and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''3%'' and c.category_id like ''3%''
            and b.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') order by a.category_id' ;
  END IF;
      dbms_output.put_line('Export_BAB_Taxonomy: '||endeca_query);
  	  v_message :='Export_BAB_Taxonomy:Query:'||endeca_query;
  	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, '30000||Buy Buy Baby||||', 'CHILD_CAT_ID|CATEGORY_ID|DISPLAY_NAME|IS_COLLEGE|NODE_TYPE|PHANTOM_CATEGORY|GUIDE_ID' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_BAB_Taxonomy: '||l_rows);
	  v_message :='Export_BAB_Taxonomy:ENDS: Total rows:'||l_rows;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_BAB_Taxonomy: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_BAB_Taxonomy: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);


  END Export_BAB_Taxonomy;
PROCEDURE Export_CA_Taxonomy(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_CA_TAX'; 
      endeca_query:='select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
            from dcs_category a, bbb_category b, dcs_cat_chldcat c where a.category_id = b.category_id 
            and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''2%'' and c.category_id like ''2%''
            order by a.category_id';
      dbms_output.put_line('Export_CA_Taxonomy: '||endeca_query);
	  v_message :='Export_CA_Taxonomy:STARTS: Query :'||endeca_query;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  IF p_feedType   = 'PARTIAL' THEN
          endeca_query := 'select a.category_id, c.category_id, display_name, b.is_college, b.node_type, b.phantom_category, b.shop_guide_id 
            from dcs_category a, bbb_category b, dcs_cat_chldcat c where a.category_id = b.category_id 
            and b.disable_flag=''0'' and c.child_cat_id = a.category_id and a.category_id like ''2%'' and c.category_id like ''2%''
            and b.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') order by a.category_id' ;
  END IF;
      dbms_output.put_line('Export_CA_Taxonomy: '||endeca_query);
	  v_message :='Export_CA_Taxonomy:Query :'||endeca_query;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, '20000||Bed Bath Canada||||', 'CHILD_CAT_ID|CATEGORY_ID|DISPLAY_NAME|IS_COLLEGE|NODE_TYPE|PHANTOM_CATEGORY|GUIDE_ID' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_CA_Taxonomy: '||l_rows);
	  v_message :='Export_CA_Taxonomy:ENDS: Total rows:'||l_rows;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

    EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_CA_Taxonomy: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_CA_Taxonomy: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_CA_Taxonomy;
PROCEDURE Export_StaticPages_Site(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_SPG_SITE'; 
      endeca_query:='select distinct static_template_id,site_id from BBB_Static_Site';
      dbms_output.put_line('Export_StaticPages_Site: '||endeca_query);
  	  v_message :='Export_StaticPages_Site:STARTS: Query:'||endeca_query;
  	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	  
  IF p_feedType   = 'PARTIAL' THEN
          endeca_query := 'select distinct a.static_template_id,a.site_id from BBB_Static_Site a,BBB_Static_Template b where b.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'')' ;
  END IF;
      dbms_output.put_line('Export_StaticPages_Site: '||endeca_query);
	  v_message :='Export_StaticPages_Site:Query:'||endeca_query;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	  	  
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'STATIC_TEMPLATE_ID|SITE_ID' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_StaticPages_Site: '||l_rows);
	  v_message :='Export_StaticPages_Site:ENDS: Total Rows:'||l_rows;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	
  EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_StaticPages_Site: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_StaticPages_Site: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);


  END Export_StaticPages_Site;
PROCEDURE Export_StaticPages(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_Statc_PG'; 
      endeca_query:='select ''*#*'',static_template_id,page_name,page_title,page_header_copy,page_copy,bbb_page_name,page_type,case when seo_url like ''%.jsp%'' then '''' else replace(seo_url,''%'',''%25'') end from BBB_Static_Template';
      dbms_output.put_line('Export_StaticPages: '||endeca_query);
  	  v_message :='Export_StaticPages:STARTS: Query:'||endeca_query;
  	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  IF p_feedType   = 'PARTIAL' THEN
          endeca_query := endeca_query || ' where last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'')' ;
  END IF;
      dbms_output.put_line('Export_StaticPages: '||endeca_query);
	  v_message :='Export_StaticPages:Query:'||endeca_query;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|STATIC_TEMPLATE_ID|PAGE_NAME|PAGE_TITLE|PAGE_HEADER_COPY|PAGE_COPY|BBB_PAGE_NAME|PAGE_TYPE|SEO_URL' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_StaticPages: '||l_rows);
	  v_message :='Export_StaticPages:ENDS: Total Rows:'||l_rows;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
  
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_StaticPages: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_StaticPages: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);


  END Export_StaticPages;
PROCEDURE Export_Guides_sites(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_Guide_ST'; 
      endeca_query:='select distinct GUIDES_ID, site_id from bbb_guides_site';
      v_message :='Export_Guides_sites:STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  IF p_feedType   = 'PARTIAL' THEN
          endeca_query := ' select distinct a.GUIDES_ID, a.site_id from bbb_guides_site a,BBB_GUIDES b where b.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'')' ;
  END IF;
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'GUIDES_ID|SITE_ID' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_Guides_sites: '||l_rows);
	  v_message :='Export_Guides_sites:ENDS:Total Rows:'||l_rows;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  END Export_Guides_sites;
PROCEDURE Export_Guides(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_Guides'; 
      v_message :='Export_Guides:STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);      
      endeca_query:='select ''*#*'',a.guides_id, CONTENT_TYPE, guides_category,title,image_url,image_alt_text,short_description,long_description from BBB_GUIDES a , bbb_guides_long_desc b where a.guides_id = b.guides_id';
  IF p_feedType   = 'PARTIAL' THEN
          endeca_query := endeca_query || ' and  a.last_mod_date > to_date(''' || p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'')' ;
  END IF;
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|GUIDES_ID|CONTENT_TYPE|GUIDES_CATEGORY|TITLE|IMAGE_URL|IMAGE_ALT_TEXT|SHORT_DESCRIPTION|LONG_DESCRIPTION' )
  INTO l_rows
  FROM dual;
      dbms_output.put_line('Export_Guides: '||l_rows);
	  v_message :='Export_Guides:ENDS: Total Rows:'||l_rows;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  EXCEPTION
  
    WHEN OTHERS THEN -- handles all other errors
      dbms_output.put_line('Export_Guides: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
    v_message_type :='I';
    v_message := 'Export_Guides: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
    Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_Guides;

PROCEDURE Export_Prices(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Exp_Prices'; 

      dbms_output.put_line('Export_Prices: '||endeca_query);
  	  v_message :='Export_Prices:STARTS';
  	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_EXPORT_PRICES';
  IF(nCount > 0)THEN
   EXECUTE immediate 'truncate table endeca_export_prices ';   
  END IF;
  
  
  

  insert into endeca_export_prices (sku_id, was_price,last_mod_date) select t1.sku_id, t1.list_price,t2.last_mod_date from dcs_price t1,bbb_price t2 where t1.price_id = t2.price_id(+) and t1.price_list = 'plist100005' order by t1.sku_id;
  commit;
  --Lokesh Insert collection stats here endeca_export_prices
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_PRICES', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);
  
  MERGE INTO endeca_export_prices t USING (select distinct sku_id,list_price from dcs_price  where price_list='plist100004') t1 on (t.sku_id = t1.sku_id) WHEN MATCHED THEN UPDATE SET t.is_price =t1.list_price;
  MERGE INTO endeca_export_prices t USING (select distinct sku_id,list_price from dcs_price  where price_list='plist100003') t1 on (t.sku_id = t1.sku_id) WHEN MATCHED THEN UPDATE SET t.ca_is_price =t1.list_price;
  MERGE INTO endeca_export_prices t USING (Select distinct sku_id,list_price from dcs_price  where price_list='plist100002') t1 on (t.sku_id = t1.sku_id) WHEN MATCHED THEN UPDATE Set t.ca_was_price =t1.list_price;
  MERGE INTO endeca_export_prices t USING (SELECT distinct sku_id,list_price from dcs_price  where price_list='MXNPLIST100002') t1 on (t.sku_id = t1.sku_id) WHEN MATCHED THEN UPDATE SET t.mx_is_price =t1.list_price;
  MERGE INTO endeca_export_prices t USING (Select distinct sku_id,list_price from dcs_price  where price_list='MXNPLIST100001') t1 on (t.sku_id = t1.sku_id) WHEN MATCHED THEN UPDATE Set t.mx_was_price =t1.list_price;
  commit;
   --Lokesh Insert collection stats here endeca_export_prices
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_PRICES', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);
      endeca_query:='select sku_id,LTRIM(to_char(was_price,999999999999.99)),LTRIM(to_char(case when is_price is null or is_price = '''' then was_price else is_price end,999999999999.99)),LTRIM(to_char(ca_was_price,999999999999.99)),LTRIM(to_char(case when ca_is_price is null or ca_is_price = '''' then ca_was_price else ca_is_price end,999999999999.99)),to_char(mx_was_price),to_char(case when mx_is_price is null or mx_is_price = '''' then mx_was_price else mx_is_price end) from endeca_export_prices';
      dbms_output.put_line('Export_Prices: '||endeca_query);
	  v_message :='Export_Prices:Query:'||endeca_query;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  commit;
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'SKU_ID|WAS_PRICE|IS_PRICE|CA_WAS_PRICE|CA_IS_PRICE|MX_WAS_PRICE|MX_IS_PRICE' )
  INTO l_rows
  FROM dual;
  
      dbms_output.put_line('Export_Prices: '||l_rows);
	  v_message :='Export_Prices:ENDS: Total Rows:'||l_rows;
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
EXCEPTION
   WHEN OTHERS THEN  -- handles all other errors
    dbms_output.put_line('Export_Prices: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
    v_message_type :='I';
    v_message := 'Export_Prices: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
    Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);


  commit;
  END Export_Prices;
PROCEDURE Export_Product_Site_Properties(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Ex_PRD_SPROP'; 
	  
      v_message :='Export_Product_Site_Properties:STARTS';
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_EXPORT_PROD_PROPERTIES';
  IF(nCount > 0)THEN
     EXECUTE immediate 'truncate table endeca_export_prod_properties ';
  END IF;	

  
 
    IF p_feedType = 'PARTIAL' THEN
         
--	EXECUTE immediate
--   'insert into endeca_export_prod_properties (product_id, site_id, web_offered_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price,enable_date) select distinct s.product_id, t.site_id, NVL(t.attribute_value_boolean,0), p.display_name, p.description, dbms_lob.substr( p.long_description,8000,1), b.price_range_descrip, b.sku_low_price, b.sku_high_price, b.enable_date from  dcs_product p, bbb_product b, bbb_prod_site_translations s, bbb_prod_translations t, dcs_prd_chldsku g, bbb_sku h where p.product_id=g.product_id (+) and g.sku_id=h.sku_id (+) and p.product_id=s.product_id and s.translation_id=t.translation_id and p.product_id=b.product_id and p.product_id in (
--     select distinct child_prd_id from dcs_cat_chldprd union all select distinct product_id from bbb_prd_reln pr where pr.like_unlike = 0 ) and t.attribute_name=''webOffered''  
--	and ( b.last_mod_date > to_date('''|| p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') or h.last_mod_date > to_date('''|| p_lastModifiedDate ||''',''YYYY-MM-DD HH24:MI:SS'') ) ';
   
   insert into endeca_export_prod_properties (product_id, site_id, web_offered_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price,enable_date) 
   select distinct p.product_id, is_web_offered.site_id, is_web_offered.web_offered_flag, p.display_name, p.description, 
dbms_lob.substr( p.long_description,8000,1), b.price_range_descrip, b.sku_low_price, b.sku_high_price, b.enable_date
--select distinct p.product_id, is_web_offered.site_id, is_web_offered.web_offered_flag
from  dcs_product p, bbb_product b,
(
-- is prod web offered
select distinct s.product_id, t.site_id, NVL(t.attribute_value_boolean, 0) web_offered_flag  
from BBB_PROD_TRANSLATIONS t, bbb_prod_site_translations s, dcs_product p
where t.attribute_name = 'webOffered'
and s.translation_id = t.translation_id
and p.product_id = s.product_id
) is_web_offered,
    (
    -- union updated prods and skus
        select product_id
        from
        (
        -- updated skus
            select distinct p.product_id
            from dcs_product p, dcs_prd_chldsku g, bbb_sku h
            where p.product_id = g.product_id (+) 
            and g.sku_id = h.sku_id (+)
            and h.last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS')
        )
        union
        select product_id from
        (
        -- updated products
        select distinct ccp.child_prd_id product_id
            from dcs_cat_chldprd  ccp, bbb_product b
            where ccp.child_prd_id = b.product_id
            and b.last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS')
            union all 
            select distinct pr.product_id from bbb_prd_reln pr, bbb_product b
            where pr.like_unlike = 0
            and pr.product_id = b.product_id
            and b.last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS')
        )
    ) updated_prods_and_skus,
    (
        select distinct child_prd_id product_id
        from dcs_cat_chldprd 
        union all 
        select distinct product_id from bbb_prd_reln pr
        where pr.like_unlike = 0
    ) product_pool
where p.product_id = b.product_id
and p.product_id = updated_prods_and_skus.product_id
and p.product_id = is_web_offered.product_id
and p.product_id = product_pool.product_id;
   
  ELSE
  
    insert into endeca_export_prod_properties (product_id, site_id, web_offered_flag, product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price, enable_date) select s.product_id, t.site_id, NVL(t.attribute_value_boolean,0), p.display_name, p.description,  dbms_lob.substr( p.long_description,8000,1), b.price_range_descrip, b.sku_low_price, b.sku_high_price, b.enable_date from dcs_product p, bbb_product b, bbb_prod_site_translations s, bbb_prod_translations t where p.product_id=b.product_id and p.product_id=s.product_id and s.translation_id=t.translation_id and p.product_id in (select child_prd_id from dcs_cat_chldprd union all select product_id from bbb_prd_reln where NVL(like_unlike, 0) = 0) and t.attribute_name='webOffered';
    
  END IF;
  
  commit;
--Lokesh  insert collection stats here ENDECA_EXPORT_PROD_PROPERTIES
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_PROD_PROPERTIES', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);
update endeca_export_prod_properties t set t.disable_flag = (select min(distinct NVL(t1.attribute_value_boolean,0)) from bbb_prod_site_translations t2, bbb_prod_translations t1 where t.product_id=t2.product_id  and t2.translation_id=t1.translation_id  and t1.site_id=t.site_id  and t1.attribute_name = 'prodDisable');
 
update endeca_export_prod_properties t set t.product_title = NVL( (select replace(t1.attribute_value_string,'"','&quot;') from bbb_prod_site_translations t2, bbb_prod_translations t1 where t.product_id=t2.product_id  and t2.translation_id=t1.translation_id  and t1.site_id=t.site_id  and t1.attribute_name = 'displayName' and t1.attribute_value_string is not null), t.product_title);
  
update endeca_export_prod_properties t set t.short_description = NVL( (select replace(t1.attribute_value_string,'"','&quot;') from bbb_prod_site_translations t2, bbb_prod_translations t1 where t.product_id=t2.product_id  and t2.translation_id=t1.translation_id  and t1.site_id=t.site_id  and t1.attribute_name = 'description' and t1.attribute_value_string is not null), t.short_description);
 
update endeca_export_prod_properties t set t.long_description = NVL( (select replace(t1.attribute_value_clob,'"','&quot;') from bbb_prod_site_translations t2, bbb_prod_translations t1 where t.product_id=t2.product_id  and t2.translation_id=t1.translation_id  and t1.site_id=t.site_id  and t1.attribute_name = 'longDescription' and t1.attribute_value_clob is not null),t.long_description);
 
update endeca_export_prod_properties t set t.sku_low_price = NVL( (select t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t.product_id=t2.product_id  and t2.translation_id=t1.translation_id  and t1.site_id=t.site_id  and t1.attribute_name = 'skuLowPrice' and t1.attribute_value_string is not null),t.sku_low_price);
 
update endeca_export_prod_properties t set t.sku_high_price = NVL( (select t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t.product_id=t2.product_id  and t2.translation_id=t1.translation_id  and t1.site_id=t.site_id  and t1.attribute_name = 'skuHighPrice' and t1.attribute_value_string is not null),t.sku_high_price);
 
update endeca_export_prod_properties t set t.price_range_descrip = NVL( (select t1.attribute_value_string from bbb_prod_site_translations t2, bbb_prod_translations t1 where t.product_id=t2.product_id  and t2.translation_id=t1.translation_id  and t1.site_id=t.site_id  and t1.attribute_name = 'priceRangeDescrip' and t1.attribute_value_string is not null),t.price_range_descrip);
update endeca_export_prod_properties t set t.enable_date = NVL((select t1.attribute_value_date from dcs_product, bbb_prod_site_translations t2, bbb_prod_translations t1 where dcs_product.product_id=t2.product_id and t2.translation_id=t1.translation_id and dcs_product.product_id=t.product_id and t1.site_id=t.site_id and t1.attribute_name = 'enableDate'), t.enable_date);
   	
	IF p_feedType = 'PARTIAL' THEN
   
     insert into endeca_export_prod_properties(product_id, web_offered_flag, disable_flag,product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price, enable_date, site_id) select a.product_id, web_offered_flag, disable_flag, display_name, dbms_lob.substr( description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price, b.enable_date, 'BedBathUS' from dcs_product a, bbb_product b where a.product_id=b.product_id and (a.product_id in (select child_prd_id from dcs_cat_chldprd union all select product_id from bbb_prd_reln where NVL(like_unlike, 0) = 0) and b.last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') or (a.product_id in (select product_id from dcs_prd_chldsku  where sku_id in(select sku_id from bbb_sku where bbb_sku.last_mod_date > to_date (p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS')))));
   
  ELSE
    
   insert into endeca_export_prod_properties(product_id, web_offered_flag, disable_flag,product_title, short_description, long_description, price_range_descrip, sku_low_price, sku_high_price, enable_date, site_id ) select a.product_id, web_offered_flag, disable_flag, display_name, dbms_lob.substr( description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price, b.enable_date, 'BedBathUS' from dcs_product a, bbb_product b where a.product_id=b.product_id and a.product_id in (select child_prd_id from dcs_cat_chldprd union all select product_id from bbb_prd_reln where NVL(like_unlike, 0) = 0);
   
  END IF;  
  commit;
--Lokesh  insert collection stats here ENDECA_EXPORT_PROD_PROPERTIES
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_PROD_PROPERTIES', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);
 
  delete from endeca_export_prod_properties where product_id in ( select distinct e1.product_id from endeca_export_prod_properties e1, bbb_product p1 where p1.product_id = e1.product_id and ( p1.collection_flag = 1 or p1.lead_prd_flag = 1) and e1.product_id not in (  select distinct e.product_id from endeca_export_prod_properties e, bbb_product p, bbb_prd_prd_reln r where p.product_id = e.product_id and p.product_id = r.product_id and e.product_id = r.product_id and ( p.collection_flag = 1 or p.lead_prd_flag = 1) ) );
  commit; 
--Lokesh  insert collection stats here ENDECA_EXPORT_PROD_PROPERTIES
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_PROD_PROPERTIES', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);
  endeca_query:='select distinct ''*#*'',product_id, web_offered_flag, disable_flag, product_title, dbms_lob.substr( short_description,8000,1), dbms_lob.substr( long_description,8000,1), price_range_descrip, sku_low_price, sku_high_price, TO_CHAR (enable_date, ''DD-MON-YY''), site_id from endeca_export_prod_properties where product_id NOT IN (''GiftWrapBaby'',''GiftWrapCanada'',''GiftWrapUS'') order by product_id';
 
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|WEB_OFFERED_FLAG|DISABLE_FLAG|PRODUCT_TITLE|SHORT_DESCRIPTION|LONG_DESCRIPTION|PRICE_RANGE_DESCRIP|SKU_LOW_PRICE|SKU_HIGH_PRICE|ENABLE_DATE|SITE_ID' )
  INTO l_rows
  FROM dual;
 
	dbms_output.put_line('Export_Product_Site_Properties: '||l_rows);
	v_message :='Export_Product_Site_Properties: ENDS : Total rows:'||l_rows;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

EXCEPTION
   WHEN OTHERS THEN -- handles all other errors
	dbms_output.put_line('Export_Product_Site_Properties: Error!');
	DBMS_OUTPUT.PUT_LINE (SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
	v_message_type :='I';
	v_message :='Export_Product_Site_Properties: Error!';
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	v_message :='Export_Product_Site_Properties: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  commit;
  END Export_Product_Site_Properties;

PROCEDURE Export_SKU_Site_Properties(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
      v_process_sub_cd:='Ex_SKU_SPROP';   
     SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_EXPT_SKU_SITE_PROPTS';
       IF(nCount > 0)THEN
     EXECUTE immediate 'truncate table endeca_expt_sku_site_propts ';
     END IF;      
    If P_Feedtype = 'PARTIAL' Then
     insert into endeca_expt_sku_site_propts (sku_id, site_id, web_offered_flag,last_mod_date,customization_offered_flag,personalization_type,eligible_customization_codes) select bbb_sku_site_translations.sku_id, bbb_sku_translations.site_id,  bbb_sku_translations.attribute_value_boolean,bbb_sku_translations.last_mod_date,bbb_sku.customization_offered_flag,bbb_sku.personalization_type,bbb_sku.eligible_customization_codes from dcs_sku,bbb_sku,bbb_sku_site_translations,bbb_sku_translations,endeca_export_skus es where dcs_sku.sku_id=bbb_sku_site_translations.sku_id and dcs_sku.sku_id=bbb_sku.sku_id and bbb_sku_site_translations.translation_id=bbb_sku_translations.translation_id and bbb_sku_translations.attribute_name = 'webOffered' and bbb_sku.sku_id = es.sku_id and bbb_sku_translations.site_id='BedBathUS' ;
	 
	insert into endeca_expt_sku_site_propts (sku_id, site_id, web_offered_flag,last_mod_date,customization_offered_flag,personalization_type,eligible_customization_codes) select bbb_sku_site_translations.sku_id, bbb_sku_translations.site_id,  bbb_sku_translations.attribute_value_boolean,bbb_sku_translations.last_mod_date,bbb_sku.ca_customization_offered_flag,bbb_sku.personalization_type,bbb_sku.eligible_customization_codes from dcs_sku,bbb_sku,bbb_sku_site_translations,bbb_sku_translations,endeca_export_skus es where dcs_sku.sku_id=bbb_sku_site_translations.sku_id and dcs_sku.sku_id=bbb_sku.sku_id and bbb_sku_site_translations.translation_id=bbb_sku_translations.translation_id and bbb_sku_translations.attribute_name = 'webOffered' and bbb_sku.sku_id = es.sku_id and bbb_sku_translations.site_id='BedBathCanada' ;
	
	insert into endeca_expt_sku_site_propts (sku_id, site_id, web_offered_flag,last_mod_date,customization_offered_flag,personalization_type,eligible_customization_codes) select bbb_sku_site_translations.sku_id, bbb_sku_translations.site_id,  bbb_sku_translations.attribute_value_boolean,bbb_sku_translations.last_mod_date,bbb_sku.bab_customization_offered_flag,bbb_sku.personalization_type,bbb_sku.eligible_customization_codes from dcs_sku,bbb_sku,bbb_sku_site_translations,bbb_sku_translations,endeca_export_skus es where dcs_sku.sku_id=bbb_sku_site_translations.sku_id and dcs_sku.sku_id=bbb_sku.sku_id and bbb_sku_site_translations.translation_id=bbb_sku_translations.translation_id and bbb_sku_translations.attribute_name = 'webOffered' and bbb_sku.sku_id = es.sku_id and bbb_sku_translations.site_id='BuyBuyBaby' ;
  ELSE
    insert into endeca_expt_sku_site_propts (sku_id, site_id, web_offered_flag,last_mod_date,customization_offered_flag,personalization_type,eligible_customization_codes) select bbb_sku_site_translations.sku_id, bbb_sku_translations.site_id,  bbb_sku_translations.attribute_value_boolean,bbb_sku_translations.last_mod_date,bbb_sku.customization_offered_flag,bbb_sku.personalization_type,bbb_sku.eligible_customization_codes from dcs_sku,bbb_sku_site_translations,bbb_sku_translations,bbb_sku where dcs_sku.sku_id=bbb_sku_site_translations.sku_id and bbb_sku_site_translations.translation_id=bbb_sku_translations.translation_id and bbb_sku_translations.attribute_name = 'webOffered' and bbb_sku_translations.site_id='BedBathUS' and dcs_sku.sku_id=bbb_sku.sku_id ;
	
    insert into endeca_expt_sku_site_propts (sku_id, site_id, web_offered_flag,last_mod_date,customization_offered_flag,personalization_type,eligible_customization_codes) select bbb_sku_site_translations.sku_id, bbb_sku_translations.site_id,  bbb_sku_translations.attribute_value_boolean,bbb_sku_translations.last_mod_date,bbb_sku.ca_customization_offered_flag,bbb_sku.personalization_type,bbb_sku.eligible_customization_codes from dcs_sku,bbb_sku_site_translations,bbb_sku_translations,bbb_sku where dcs_sku.sku_id=bbb_sku_site_translations.sku_id and bbb_sku_site_translations.translation_id=bbb_sku_translations.translation_id and bbb_sku_translations.attribute_name = 'webOffered' and bbb_sku_translations.site_id='BedBathCanada' and dcs_sku.sku_id=bbb_sku.sku_id ;
	
    insert into endeca_expt_sku_site_propts (sku_id, site_id, web_offered_flag,last_mod_date,customization_offered_flag,personalization_type,eligible_customization_codes) select bbb_sku_site_translations.sku_id, bbb_sku_translations.site_id,  bbb_sku_translations.attribute_value_boolean,bbb_sku_translations.last_mod_date,bbb_sku.bab_customization_offered_flag,bbb_sku.personalization_type,bbb_sku.eligible_customization_codes from dcs_sku,bbb_sku_site_translations,bbb_sku_translations,bbb_sku where dcs_sku.sku_id=bbb_sku_site_translations.sku_id and bbb_sku_site_translations.translation_id=bbb_sku_translations.translation_id and bbb_sku_translations.attribute_name = 'webOffered' and bbb_sku_translations.site_id='BuyBuyBaby' and dcs_sku.sku_id=bbb_sku.sku_id ;
  END IF;
  commit;
-- Lokesh Insert collection stats here ENDECA_EXPT_SKU_SITE_PROPTS
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPT_SKU_SITE_PROPTS', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);

  update endeca_expt_sku_site_propts t set t.sku_title = NVL((replace((select t1.attribute_value_string from dcs_sku,bbb_sku_site_translations,bbb_sku_translations t1 where dcs_sku.sku_id=bbb_sku_site_translations.Sku_Id and bbb_sku_site_translations.translation_id=t1.translation_id and dcs_sku.sku_id=t.Sku_Id and t1.site_id=t.site_id and t1.attribute_name = 'displayName'),'"','&quot;')), t.sku_title);
  update endeca_expt_sku_site_propts t set t.disable_flag = (select t1.attribute_value_boolean from dcs_sku,bbb_sku_site_translations,bbb_sku_translations t1 where dcs_sku.sku_id=bbb_sku_site_translations.sku_id and bbb_sku_site_translations.translation_id=t1.translation_id and dcs_sku.sku_id=t.sku_id and t1.site_id=t.site_id and t1.attribute_name = 'disable');
  update endeca_expt_sku_site_propts t set t.short_description = NVL((replace((select t1.attribute_value_string from dcs_sku,bbb_sku_site_translations,bbb_sku_translations t1 where dcs_sku.sku_id=bbb_sku_site_translations.sku_id and bbb_sku_site_translations.translation_id=t1.translation_id and dcs_sku.sku_id=t.sku_id and t1.site_id=t.site_id and t1.attribute_name = 'description'),'"','&quot;')), t.short_description);
  IF p_feedType = 'PARTIAL' THEN
     insert into endeca_expt_sku_site_propts (sku_id, web_offered_flag, disable_flag,sku_title, short_description, site_id,last_mod_date,customization_offered_flag,personalization_type,eligible_customization_codes) select bbb_sku.sku_id, bbb_sku.web_offered_flag, bbb_sku.disable_flag, replace(dcs_sku.display_name,'"','&quot;'), replace(dcs_sku.description,'"','&quot;'),'BedBathUS',bbb_sku.last_mod_date,bbb_sku.customization_offered_flag,bbb_sku.personalization_type , bbb_sku.eligible_customization_codes from bbb_sku,dcs_sku,endeca_export_skus es where bbb_sku.sku_id=dcs_sku.sku_id and bbb_sku.sku_id = es.sku_id ;

  Else
    insert into endeca_expt_sku_site_propts (sku_id, web_offered_flag, disable_flag,sku_title, short_description, site_id,last_mod_date,customization_offered_flag,personalization_type,eligible_customization_codes) select bbb_sku.sku_id, bbb_sku.web_offered_flag, bbb_sku.disable_flag, replace(dcs_sku.display_name,'"','&quot;'),  replace(dcs_sku.description,'"','&quot;'),'BedBathUS',bbb_sku.last_mod_date ,bbb_sku.customization_offered_flag,bbb_sku.personalization_type , bbb_sku.eligible_customization_codes from bbb_sku,dcs_sku where bbb_sku.sku_id=dcs_sku.sku_id ;
	
  	
  END IF;
  commit;
-- Lokesh Insert collection stats here ENDECA_EXPT_SKU_SITE_PROPTS
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPT_SKU_SITE_PROPTS', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);
 endeca_query:='select ''*#*'',sku_id, web_offered_flag,disable_flag, replace(sku_title,''"'',''&quot;''), replace(short_description,''"'',''&quot;''), site_id,customization_offered_flag,personalization_type,eligible_customization_codes  from endeca_expt_sku_site_propts where sku_id NOT IN (''GiftWrapBaby'',''GiftWrapCanada'',''GiftWrapUS'') order by sku_id';
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|SKU_ID|WEB_OFFERED_FLAG|DISABLE_FLAG|SKU_TITLE|SHORT_DESCRIPTION|SITE_ID|CUSTOMIZATION_OFFERED|PERSONALIZATION_TYPE|ELIGIBLE_CUSTOMIZATIONS')
  INTO l_rows
  FROM dual;
  
  dbms_output.put_line('Export_SKU_Site_Properties: '||l_rows);
  v_message :='Export_SKU_Site_Properties:ENDS: Total Rows:'||l_rows;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
EXCEPTION
    WHEN OTHERS THEN -- handles all other errors
	  dbms_output.put_line('Export_SKU_Site_Properties: Error!');
    v_message :='Export_SKU_Site_Properties:: Error!';
    v_message_type := 'I';
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  commit;
  END Export_SKU_Site_Properties;
  
  
  PROCEDURE Export_Prod_site_sales_data (p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2)
  IS
    l_rows number; -- pkg var to capture the number of rows affected from the function 
    endeca_query varchar(2000); -- pkg var to capture the SQL query 
	nCount NUMBER;
	nTotal_Sales_Rows_Count NUMBER;
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
      v_process_sub_cd:='Ex_SALE_DATA';   

      v_message :='Export_Prod_site_sales_data: STARTS' ;
      Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

	SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_PROD_SITE_RELATION';
    IF(nCount > 0)THEN
		EXECUTE immediate 'truncate table endeca_prod_site_relation ';
    END IF;
	insert into endeca_prod_site_relation(PRODUCT_ID, site_id) select product_id, 'BedBathUS' from BBB_PRODUCT;
	insert into endeca_prod_site_relation(PRODUCT_ID, site_id) select product_id, 'BuyBuyBaby' from BBB_PRODUCT;
	insert into endeca_prod_site_relation(PRODUCT_ID, site_id) select product_id, 'BedBathCanada' from BBB_PRODUCT;
	commit;

	--Lokesh insert collection stats here ENDECA_PROD_SITE_RELATION
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_PROD_SITE_RELATION', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);

	
	endeca_query:='select distinct ''*#*'', eps.product_id, nvl(bsd.UNIT_SALES,''0''), nvl(bsd.ORDER_SALES,''0''), nvl(bsd.TOTAL_SALES,''0''), eps.site_id from endeca_prod_site_relation eps, BBB_CORE.BBB_SALES_DATA bsd where eps.product_id = bsd.product_id(+) and eps.site_id = bsd.site_id(+) order by eps.product_id';
	
	select count(*) into nTotal_Sales_Rows_Count from BBB_CORE.BBB_SALES_DATA bsd where bsd.total_sales > 0;     
	IF(nTotal_Sales_Rows_Count > 0) THEN
		dbms_output.put_line('Export_Prod_site_sales_data: Generating Product Site Sales Data file as no of rows with total_sales count > 0  are  '|| nTotal_Sales_Rows_Count);
		v_message :='Export_Prod_site_sales_data: Generating Product Site Sales Data file as no of rows with total_sales count > 0  are  '|| nTotal_Sales_Rows_Count;
		Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
		select dump_csv( endeca_query, '|', p_feedType, p_filename, null, 'NEW_RECORD|PRODUCT_ID|UNIT_SALES|ORDER_SALES|TOTAL_SALES|SITE_ID' ) into l_rows from dual;
		dbms_output.put_line('Export_Prod_site_sales_data: '||l_rows); 
		v_message :='Export_Prod_site_sales_data: ENDS: Total Rows :'||l_rows ;
		Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	END IF;


	EXCEPTION
    WHEN OTHERS THEN  -- handles all other errors
      dbms_output.put_line('Export_Prod_site_sales_data: Error!'); 
  		DBMS_OUTPUT.PUT_LINE (SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  		v_message_type :='I';
  		v_message :='Export_Prod_site_sales_data: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  		Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  		commit;
  END Export_Prod_site_sales_data;
  
  --BBBSL-1740
  
  PROCEDURE Export_Prices_Updates( p_filename VARCHAR2, p_lastModifiedDate VARCHAR2, p_feedType VARCHAR2)
IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
  nCount NUMBER;
  ROWDATA VARCHAR2(12);
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
	v_process_sub_cd:='Exp_Price_U';   

	dbms_output.put_line('Export_Prices_Updates: Started');
	v_message :='Export_Prices_Updates: STARTS';
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  SELECT count(*) into nCount FROM all_tables where table_name ='ENDECA_EXPORT_PRICES_UPDATES';
   dbms_output.put_line('nCount' || nCount );
   	v_message :='Export_Prices_Updates: nCount :'||nCount;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  IF(nCount > 0)THEN
  
   EXECUTE immediate 'truncate table endeca_export_prices_updates'; 
    
  END IF;
  
  
  IF p_feedType = 'PARTIAL' THEN
	dbms_output.put_line('Partial' );
	v_message :='Export_Prices_Updates: Partial';
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
    insert into endeca_export_prices_updates (product_id, price_range_descrip,sku_low_price,sku_high_price,site_id)
		select distinct(p.product_id),p.price_range_descrip, p.sku_low_price,p.sku_high_price,
		case
		when t1.price_list = 'plist100002' then  'BedBathCanada'
		when t1.price_list = 'plist100003' then  'BedBathCanada'
		when t1.price_list = 'plist100004' then  'BedBathUS'
		when t1.price_list = 'plist100005' then  'BedBathUS'
		when t1.price_list = 'MXNPLIST100001' then  'BedBathUS'
		when t1.price_list = 'MXNPLIST100002' then  'BedBathUS'
		end as site_id
		from dcs_price t1, bbb_price t2, 
		dcs_prd_chldsku s,bbb_product p,
		bbb_prd_reln prd, bbb_prd_prd_reln prd_reln
		where t1.price_id = t2.price_id(+) 
		and s.sku_id = t1.sku_id
		and prd.product_id = s.product_id
		and prd.product_relan_id = prd_reln.product_relan_id
		and prd_reln.product_id = p.product_id
		and p.collection_flag='1'
		and t2.last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') order by p.product_id;
	
	insert into endeca_export_prices_updates (product_id, price_range_descrip,sku_low_price,sku_high_price,site_id) select distinct(p.product_id),p.price_range_descrip, p.sku_low_price,p.sku_high_price,
                                case
                                when t1.price_list = 'plist100002' then  'BedBathCanada'
                                when t1.price_list = 'plist100003' then  'BedBathCanada'
                                when t1.price_list = 'plist100004' then  'BedBathUS'
                                when t1.price_list = 'plist100005' then  'BedBathUS'
				when t1.price_list = 'MXNPLIST100001' then  'BedBathUS'
                                when t1.price_list = 'MXNPLIST100002' then  'BedBathUS'
                                end as site_id
                        from dcs_price t1, bbb_price t2, 
                                dcs_prd_chldsku s,bbb_product p
                        where t1.price_id = t2.price_id(+) 
                                and p.product_id = s.product_id 
                                and s.sku_id = t1.sku_id  
                                and t2.last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') order by p.product_id;
  dbms_output.put_line('Insert Done' );
	v_message :='Export_Prices_Updates: Insert Done';
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  commit;
-- Lokesh insert collection stats here ENDECA_EXPORT_PRICES_UPDATES	
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_PRICES_UPDATES', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);

      FOR ROWDATA IN (select * from ENDECA_EXPORT_PRICES_UPDATES where site_id='BedBathUS')
		LOOP
    
    insert into ENDECA_EXPORT_PRICES_UPDATES( PRODUCT_ID,PRICE_RANGE_DESCRIP,SKU_LOW_PRICE,SKU_HIGH_PRICE,SITE_ID)
    VALUES (ROWDATA.product_id,ROWDATA.PRICE_RANGE_DESCRIP,ROWDATA.SKU_LOW_PRICE,ROWDATA.SKU_HIGH_PRICE,'BuyBuyBaby'); 
      
    END LOOP;
    dbms_output.put_line('Buy Buy Baby Updated' );
  	v_message :='Export_Prices_Updates: Buy Buy Baby Updated';
  	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  END IF;
   commit;
-- Lokesh insert collection stats here ENDECA_EXPORT_PRICES_UPDATES	
  dbms_stats.gather_table_stats(ownname => 'BBB_SWITCH_B', tabname => 'ENDECA_EXPORT_PRICES_UPDATES', estimate_percent => 100, method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);
                                
endeca_query:='select product_id,price_range_descrip,sku_low_price,sku_high_price,site_id from endeca_export_prices_updates order by product_id'; 

  dbms_output.put_line('Export_Prices_Updates: '||endeca_query); 
	v_message :='Export_Prices_Updates: Query:'||endeca_query;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'PRODUCT_ID|PRICE_RANGE_STRING|SKU_LOW_PRICE|SKU_HIGH_PRICE|SITE_ID' )
  INTO l_rows
  FROM dual;
  
  dbms_output.put_line('Export_Prices_Updates: '||l_rows);
	v_message :='Export_Prices_Updates:ENDS: Total Rows:'||l_rows;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	
EXCEPTION
WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('Export_Prices_Updates: Error!');
	v_message_type :='I';
	v_message :='Export_Prices_Updates: Error!';
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
END Export_Prices_Updates;
PROCEDURE Export_EXIM_Customization_Code (
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
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
	v_process_sub_cd:='EXP_EXIM';
	v_message_type:='A';
	IF p_feedType  = 'PARTIAL' THEN
		v_process_cd:='EN_PARTIAL';
	END IF;
     
 endeca_query:='select customization_code ,description from BBB_CORE.BBB_EXIM_CUSTOMIZATION_CODES';
  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'CUSTOMIZATION_CODE|CUSTOMIZATION_DESCRIPTION')
  INTO l_rows
  FROM dual;
  
  dbms_output.put_line('Export_EXIM_Customization_Code: '||l_rows);
  v_message :='Export_EXIM_Customization_Code:ENDS: Total Rows:'||l_rows;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
EXCEPTION
    WHEN OTHERS THEN -- handles all other errors
	  dbms_output.put_line('Export_EXIM_Customization_Code: Error!');
    v_message :='Export_EXIM_Customization_Code:: Error!';
    v_message_type := 'I';
	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  commit;
  END Export_EXIM_Customization_Code;
  
--BBBSL-1740

--Search Config
  PROCEDURE Export_Search_Config_Items(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  query VARCHAR(2000); -- pkg var to capture the SQL query
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
      v_process_sub_cd:='ExSrchConf'; 

      dbms_output.put_line('Export_Search_Config_Items:STARTS');
  	  v_message :='Export_Search_Config_Items:STARTS';
  	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  
  query:='SELECT DISTINCT ''*#*'',GRP1.GROUP_NAME AS GROUP_NAME,PROP_ITM1.PROPERTY_NAME,SRCH_ITM1.PRIORITY AS PRIORITY
			FROM BBB_SWITCH_B.BBB_SEARCH_CONFIG_GROUPS GRP1, BBB_SWITCH_B.BBB_SEARCH_GROUP_ITEMS GRP_ITEM1, BBB_SWITCH_B.BBB_SEARCHABLE_ITEM SRCH_ITM1,
			BBB_SWITCH_B.BBB_SEARCH_PROPERTY_ITEM PROP_ITM1, BBB_SWITCH_B.BBB_SEARCH_PROPERTY_REF_ITEM PROP_REF_ITM1 WHERE GRP_ITEM1.GROUP_ID=GRP1.GROUP_ID 
			AND GRP_ITEM1.SEARCH_ITEM_ID=SRCH_ITM1.SEARCH_ITEM_ATTR_ID 
			AND SRCH_ITM1.SEARCH_ITEM_ATTR_ID=PROP_REF_ITM1.SEARCH_ITEM_ATTR_ID AND PROP_REF_ITM1.PROPERTY_ID=PROP_ITM1.PROPERTY_ID AND SRCH_ITM1.SEARCHABLE=1 
 			UNION SELECT DISTINCT ''*#*'',GRP2.GROUP_NAME, FCT_TYPE.DESCRIPTION, SRCH_ITM2.PRIORITY
			FROM BBB_SWITCH_B.BBB_SEARCH_CONFIG_GROUPS GRP2, BBB_SWITCH_B.BBB_FACET_TYPES_VER FCT_TYP1, BBB_SWITCH_B.BBB_SEARCHABLE_ITEM SRCH_ITM2, BBB_CORE.BBB_FACET_TYPES FCT_TYPE,
			BBB_SWITCH_B.BBB_SEARCH_GROUP_ITEMS SRCH_GRP2 WHERE GRP2.GROUP_ID=SRCH_GRP2.GROUP_ID AND SRCH_GRP2.SEARCH_ITEM_ID=SRCH_ITM2.SEARCH_ITEM_ATTR_ID and
			SRCH_ITM2.SEARCH_ITEM_ATTR_ID =FCT_TYP1.SEARCH_ITEM_ATTR_ID and FCT_TYP1.SKU_FACET_ID=FCT_TYPE.FACET_ID AND SRCH_ITM2.SEARCHABLE=1 ORDER BY GROUP_NAME,PRIORITY'; 

    dbms_output.put_line('Export_Search_Config_Items: '|| query); 
  
  SELECT dump_csv( query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|GROUP_NAME|PROPERTY_TYPE|PRIORITY' )
  INTO l_rows
  FROM dual;
  
  dbms_output.put_line('Export_Search_Config_Items: '||l_rows);
  v_message :='Export_Search_Config_Items:ENDS: Total Rows:'||l_rows;
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
EXCEPTION
   WHEN OTHERS THEN  -- handles all other errors
   dbms_output.put_line('Export_Search_Config_Items: Error! :'||SQLCODE );
   dbms_output.put_line('Export_Search_Config_Items: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
   v_message_type :='I';
   v_message := 'Export_Search_Config_Items: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
   Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_Search_Config_Items;

  -- Search Config | Groups with Rel Ranks

  PROCEDURE Export_SearchGroup_RelRanks(
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
  IS
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  query VARCHAR(2000); -- pkg var to capture the SQL query
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
      v_process_sub_cd:='ExSrchGRel'; 

      dbms_output.put_line('Export_SearchGroup_RelRanks:STARTS');
  	  v_message :='Export_SearchGroup_RelRanks:STARTS';
  	  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  
  query:='SELECT DISTINCT ''*#*'',GRP1.GROUP_NAME,GRP1.GROUP_RELRANK AS RELRANK
			FROM BBB_SWITCH_B.BBB_SEARCH_CONFIG_GROUPS GRP1'; 

    dbms_output.put_line('Export_SearchGroup_RelRanks: '|| query); 
  
  SELECT dump_csv( query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|GROUP_NAME|RELRANK' )
  INTO l_rows
  FROM dual;
  
  dbms_output.put_line('Export_SearchGroup_RelRanks: '||l_rows);
  v_message :='Export_SearchGroup_RelRanks:ENDS: Total Rows:'||l_rows;
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
EXCEPTION
   WHEN OTHERS THEN  -- handles all other errors
   dbms_output.put_line('Export_SearchGroup_RelRanks: Error! :'||SQLCODE );
   dbms_output.put_line('Export_SearchGroup_RelRanks: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
   v_message_type :='I';
   v_message := 'Export_SearchGroup_RelRanks: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
   Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_SearchGroup_RelRanks;  
   
  -- R2.2.1 || Harmon Tabs || START

	PROCEDURE Export_Tabs_Sites(
		p_filename         VARCHAR2,
		p_lastModifiedDate VARCHAR2,
		p_feedType         VARCHAR2)
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
		  v_process_sub_cd:='Exp_Tab_Site';
		  endeca_query:='select distinct a.tab_id,a.site_id from bbb_tab_sites a,bbb_product_tabs b where a.site_id like ''B%'' and b.tab_id = a.tab_id and b.tab_name =''harmon'' order by a.tab_id,a.site_id';
		  v_message :='Export_Tabs_Sites: endeca_query:'||endeca_query;
		  dbms_output.put_line('Export_Tabs_Sites: '||endeca_query);
		  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
		  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'HARMON_TAB_ID|SITE_ID') INTO l_rows FROM dual;
		  dbms_output.put_line('Export_Tabs_Sites: '||l_rows);
		  v_message :='Export_Tabs_Sites: ENDS: Total rows:'||l_rows;
		  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	EXCEPTION
	  WHEN OTHERS THEN -- handles all other errors
		dbms_output.put_line('Export_Tabs_Sites: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
		v_message_type :='I';
		v_message := 'Export_Tabs_Sites: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
		Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	END Export_Tabs_Sites;

	PROCEDURE Export_Prod_Harmon_Tabs(
		p_filename         VARCHAR2,
		p_lastModifiedDate VARCHAR2,
		p_feedType         VARCHAR2)
	  IS
	  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
	  endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
	  v_process_cd VARCHAR2(12); -- Endeca Procedure Name
	  v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
	  v_message VARCHAR(2500); -- message to log
	  v_message_type CHAR(1); --Message type to log, A for info, I for exception

	  BEGIN
		  v_process_cd:='EN_FULL';
			  v_message_type:='A';
		  v_process_sub_cd:='Exp_Prod_Tab';
		  endeca_query:='select ''*#*'', b.product_id,a.tab_id,a.tab_name,RTRIM(SUBSTR( a.tab_content,1,32000)) from bbb_product_tabs a, bbb_prod_tabs_reln b where b.tab_id = a.tab_id and a.tab_name =''harmon'' order by b.product_id';
		  IF p_feedType  = 'PARTIAL' THEN
			v_process_cd:='EN_PARTIAL';
			endeca_query:='select ''*#*'', b.product_id,a.tab_id,a.tab_name,RTRIM(SUBSTR( a.tab_content,1,32000)) from bbb_product_tabs a, bbb_prod_tabs_reln b, endeca_export_products ep where b.product_id = ep.product_id and b.tab_id = a.tab_id and a.tab_name =''harmon'' order by b.product_id';
		  END IF;
		  v_message :='Export_Prod_Harmon_Tabs: endeca_query:'||endeca_query;
		  dbms_output.put_line('Export_Prod_Harmon_Tabs: '||endeca_query);
		  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
		  SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|PRODUCT_ID|HARMON_TAB_ID|HARMON_TAB_NAME|HARMON_TAB_CONTENT') INTO l_rows FROM dual;
		  dbms_output.put_line('Export_Prod_Harmon_Tabs: '||l_rows);
		  v_message :='Export_Prod_Harmon_Tabs: ENDS: Total rows:'||l_rows;
		  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	EXCEPTION
	  WHEN OTHERS THEN -- handles all other errors
		dbms_output.put_line('Export_Prod_Harmon_Tabs: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
		v_message_type :='I';
		v_message := 'Export_Prod_Harmon_Tabs: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
		Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
	END Export_Prod_Harmon_Tabs;

-- R2.2.1 || Harmon Tabs || END
  
  
  PROCEDURE Export_GoFile(p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2)
  IS
    l_rows number;
    endeca_query varchar(2000);
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
	v_process_sub_cd:='Exp_Price_U';   
  endeca_query:='select sysdate from dual';

  v_message := 'Export_GoFile : STARTS';
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

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
		v_message :='Export_GoFile : ENDS: Total Rows:'||l_rows;
		Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);

  END Export_GoFile;
  procedure export_store_only(p_filename varchar2, p_lastModifiedDate varchar2, p_feedType varchar2)
/*
    New Procedure to create a feed file of all the Manual Item Entry SKUs (MIE).  RSP 8/27/2014 
    
    Modification History: 
    Programmer  Date        Modification
    RSP         11/17/2014  Add Baby, Canada, Guided Selling available flags    
     
*/ 

is
  l_output        utl_file.file_type;
  l_colCnt      NUMBER DEFAULT 0;
  l_separator   VARCHAR2(10) DEFAULT '';
  l_cnt         NUMBER DEFAULT 0;
  v_dir         VARCHAR2(40);  
  l_headr       varchar2(500) := 'NEW_RECORD|SKU_ID|MIE_DISPLAY_NAME|MIE_DESCRIPTION|MIE_PRODUCT_ID|MIE_ITEM_EXCL_SITE_FLAG_RES_CD|MIE_SKU_PRODUCT_ID|MIE_IS_STORE_SKU|MIE_BBB_WEB_OFFERED_FLAG|MIE_BBB_DISABLED_FLAG|MIE_BAB_WEB_OFFERED_FLAG|MIE_BAB_DISABLED_FLAG|MIE_CA_WEB_OFFERED_FLAG|MIE_CA_DISABLED_FLAG|MIE_GS_BBB_WEB_OFFERED|MIE_GS_BBB_DISABLED|MIE_GS_BAB_WEB_OFFERED|MIE_GS_BAB_DISABLED|MIE_GS_CA_WEB_OFFERED|MIE_GS_CA_DISABLED';
  v_total       number := 0;
  l_filename    varchar(50) := 'MIESkuProduct.txt';
  
  -- Error Handling
  v_process_cd    BBB_PIM_STG.process_support.process_cd%TYPE := 'ATG_ENDECA';
  v_process_sub_cd  BBB_PIM_STG.process_support.process_sub_cd%TYPE := 'exp_store';
  v_start_dt   DATE                     := SYSDATE;
  v_end_dt     DATE                     := NULL;
  v_err_dt     date      := NULL;
  v_err_cd     number(10);
  v_err_msg    varchar2(200);
  v_message    BBB_PIM_STG.process_support.message%TYPE := NULL;
  v_event_dt   date    := SYSDATE;
  v_event_msg  varchar(200)   := NULL;  
  v_text       VARCHAR2(1000);  
  v_partial_dt date;
  
  CURSOR cur_ss is
--  select '*#*' as MIE_NEW,
--         bs.sku_id as MIE_SKU_ID, 
--         ds.DISPLAY_NAME as MIE_DISPLAY_NAME, 
--         ds.DESCRIPTION as MIE_DESCRIPTION, 
--         BBB_SWITCH_B.GET_PRODUCT_ID(bs.SKU_ID) as MIE_PRODUCT_ID,         
--         i.SITE_FLAG as MIE_ITEM_EXCLUDE_SITE_FLAG,
--         'sku_'||bs.SKU_ID as MIE_SKU_PRODUCT_ID, 
--         bs.IS_STORE_SKU as MIE_IS_STORE_SKU,
--         bs.WEB_OFFERED_FLAG as MIE_WEB_OFFERED_FLAG,
--         bs.DISABLE_FLAG as MIE_DISABLE_FLAG

--   from BBB_SWITCH_B.bbb_sku bs inner join BBB_SWITCH_B.dcs_sku ds
--   on bs.SKU_ID = ds.SKU_ID
--   left outer join bbb_BBB_SWITCH_B.item_excluded i on 
--   i.SKU = bs.SKU_ID 
--   where regexp_like(trim(bs.SKU_ID ),'[0-9]');

-- Do not want Dept 988 - ECO SKUs - RSP 12/3/2014 
-- Calling function to get the Site Flag because not all sku's in item_excluded are excluded due to end_date.  No longer doing outer join to item_excluded.  RSP 1/19/2015
-- We are now getting the reason_codes and site flags from the BBB_CORE.ITEM_EXCLUDED table.  There could be more than 1. 
  select '*#*' as MIE_NEW,
         bs.sku_id as MIE_SKU_ID, 
         ds.DISPLAY_NAME as MIE_DISPLAY_NAME, 
         ds.DESCRIPTION as MIE_DESCRIPTION, 
         BBB_SWITCH_B.GET_PRODUCT_ID(bs.SKU_ID) as MIE_PRODUCT_ID,         
         --i.SITE_FLAG as MIE_ITEM_EXCLUDE_SITE_FLAG,
         BBB_CORE.GET_ITEM_EXCLUDED_SITE_FLAG(bs.SKU_ID) as MIE_ITEM_EXCL_SITE_FLAG_RES_CD , 
         'sku_'||bs.SKU_ID as MIE_SKU_PRODUCT_ID, 
         bs.IS_STORE_SKU as MIE_IS_STORE_SKU,
         
         bs.WEB_OFFERED_FLAG as MIE_WEB_OFFERED_FLAG,
         bs.DISABLE_FLAG as MIE_DISABLE_FLAG, 
        -- New RSP 11/17/2014  
        nvl((
            select regexp_substr(translation_id, '.{1}$') from BBB_SWITCH_B.bbb_sku_site_translations st
            where st.sku_id = bs.sku_id and instr( translation_id, '2_en_US_webOffered') > 0
        ),'N') MIE_BAB_WEB_OFFERED_FLAG,
        nvl((
            select regexp_substr(translation_id, '.{1}$') from BBB_SWITCH_B.bbb_sku_site_translations st
            where st.sku_id = bs.sku_id and instr( translation_id, '2_en_US_disable') > 0
        ),'N') MIE_BAB_DISABLED_FLAG,
        nvl((
            select regexp_substr(translation_id, '.{1}$') from BBB_SWITCH_B.bbb_sku_site_translations st
            where st.sku_id = bs.sku_id and instr( translation_id, '3_en_US_webOffered') > 0
        ),'N') MIE_CA_WEB_OFFERED_FLAG,
        nvl((
            select regexp_substr(translation_id, '.{1}$') from BBB_SWITCH_B.bbb_sku_site_translations st
            where st.sku_id = bs.sku_id and instr( translation_id, '3_en_US_disable') > 0
        ),'N') MIE_CA_DISABLED_FLAG,                        
        ( CASE WHEN bs.GS_BBB_WEB_OFFERED = 1 THEN 'Y' ELSE 'N' END ) MIE_GS_BBB_WEB_OFFERED, 
        ( CASE WHEN bs.GS_BBB_DISABLED = 1 THEN 'Y' ELSE 'N' END ) MIE_GS_BBB_DISABLED, 
        ( CASE WHEN bs.GS_BAB_WEB_OFFERED = 1 THEN 'Y' ELSE 'N' END ) MIE_GS_BAB_WEB_OFFERED, 
        ( CASE WHEN bs.GS_BAB_DISABLED = 1 THEN 'Y' ELSE 'N' END ) MIE_GS_BAB_DISABLED, 
        ( CASE WHEN bs.GS_CA_WEB_OFFERED = 1 THEN 'Y' ELSE 'N' END ) MIE_GS_CA_WEB_OFFERED, 
        ( CASE WHEN bs.GS_CA_DISABLED = 1 THEN 'Y' ELSE 'N' END ) MIE_GS_CA_DISABLED

   from BBB_SWITCH_B.bbb_sku bs inner join BBB_SWITCH_B.dcs_sku ds
   on bs.SKU_ID = ds.SKU_ID
   where regexp_like(trim(bs.SKU_ID ),'[0-9]') and bs.JDA_DEPT_ID <> '988';

  CURSOR cur_ss_partial (partial_dt date) is
--  select '*#*' as MIE_NEW,
--         bs.sku_id as MIE_SKU_ID, 
--         ds.DISPLAY_NAME as MIE_DISPLAY_NAME, 
--         ds.DESCRIPTION as MIE_DESCRIPTION, 
--         BBB_SWITCH_B.GET_PRODUCT_ID(bs.SKU_ID) as MIE_PRODUCT_ID,         
--         i.SITE_FLAG as MIE_ITEM_EXCLUDE_SITE_FLAG,
--         'sku_'||bs.SKU_ID as MIE_SKU_PRODUCT_ID, 
--         bs.IS_STORE_SKU as MIE_IS_STORE_SKU,
--         bs.WEB_OFFERED_FLAG as MIE_WEB_OFFERED_FLAG,
--         bs.DISABLE_FLAG as MIE_DISABLE_FLAG

--   from BBB_SWITCH_B.bbb_sku bs inner join BBB_SWITCH_B.dcs_sku ds
--   on bs.SKU_ID = ds.SKU_ID
--   left outer join bbb_BBB_SWITCH_B.item_excluded i on 
--   i.SKU = bs.SKU_ID 
--   where regexp_like(trim(bs.SKU_ID ),'[0-9]');

-- Do not want Dept 988 - ECO SKUs - RSP 12/3/2014 
-- Calling function to get the Site Flag because not all sku's in item_excluded are excluded due to end_date.  No longer doing outer join to item_excluded.  RSP 1/19/2015
-- We are now getting the reason_codes and site flags from the BBB_CORE.ITEM_EXCLUDED table.  There could be more than 1. 
  select '*#*' as MIE_NEW,
         bs.sku_id as MIE_SKU_ID, 
         ds.DISPLAY_NAME as MIE_DISPLAY_NAME, 
         ds.DESCRIPTION as MIE_DESCRIPTION, 
         BBB_SWITCH_B.GET_PRODUCT_ID(bs.SKU_ID) as MIE_PRODUCT_ID,         
         --i.SITE_FLAG as MIE_ITEM_EXCLUDE_SITE_FLAG,
         BBB_CORE.GET_ITEM_EXCLUDED_SITE_FLAG(bs.SKU_ID) as MIE_ITEM_EXCL_SITE_FLAG_RES_CD , 
         'sku_'||bs.SKU_ID as MIE_SKU_PRODUCT_ID, 
         bs.IS_STORE_SKU as MIE_IS_STORE_SKU,
         
         bs.WEB_OFFERED_FLAG as MIE_WEB_OFFERED_FLAG,
         bs.DISABLE_FLAG as MIE_DISABLE_FLAG, 
        -- New RSP 11/17/2014  
        nvl((
            select regexp_substr(translation_id, '.{1}$') from BBB_SWITCH_B.bbb_sku_site_translations st
            where st.sku_id = bs.sku_id and instr( translation_id, '2_en_US_webOffered') > 0
        ),'N') MIE_BAB_WEB_OFFERED_FLAG,
        nvl((
            select regexp_substr(translation_id, '.{1}$') from BBB_SWITCH_B.bbb_sku_site_translations st
            where st.sku_id = bs.sku_id and instr( translation_id, '2_en_US_disable') > 0
        ),'N') MIE_BAB_DISABLED_FLAG,
        nvl((
            select regexp_substr(translation_id, '.{1}$') from BBB_SWITCH_B.bbb_sku_site_translations st
            where st.sku_id = bs.sku_id and instr( translation_id, '3_en_US_webOffered') > 0
        ),'N') MIE_CA_WEB_OFFERED_FLAG,
        nvl((
            select regexp_substr(translation_id, '.{1}$') from BBB_SWITCH_B.bbb_sku_site_translations st
            where st.sku_id = bs.sku_id and instr( translation_id, '3_en_US_disable') > 0
        ),'N') MIE_CA_DISABLED_FLAG,                        
        ( CASE WHEN bs.GS_BBB_WEB_OFFERED = 1 THEN 'Y' ELSE 'N' END ) MIE_GS_BBB_WEB_OFFERED, 
        ( CASE WHEN bs.GS_BBB_DISABLED = 1 THEN 'Y' ELSE 'N' END ) MIE_GS_BBB_DISABLED, 
        ( CASE WHEN bs.GS_BAB_WEB_OFFERED = 1 THEN 'Y' ELSE 'N' END ) MIE_GS_BAB_WEB_OFFERED, 
        ( CASE WHEN bs.GS_BAB_DISABLED = 1 THEN 'Y' ELSE 'N' END ) MIE_GS_BAB_DISABLED, 
        ( CASE WHEN bs.GS_CA_WEB_OFFERED = 1 THEN 'Y' ELSE 'N' END ) MIE_GS_CA_WEB_OFFERED, 
        ( CASE WHEN bs.GS_CA_DISABLED = 1 THEN 'Y' ELSE 'N' END ) MIE_GS_CA_DISABLED

   from BBB_SWITCH_B.bbb_sku bs inner join BBB_SWITCH_B.dcs_sku ds
   on bs.SKU_ID = ds.SKU_ID
   where regexp_like(trim(bs.SKU_ID ),'[0-9]') and bs.JDA_DEPT_ID <> '988' and
   bs.LAST_MOD_DATE > partial_dt;

     
  rec_ss cur_ss%rowtype;
  
    
begin
    IF p_feedtype = 'PARTIAL' THEN
        v_dir := 'ATG_APP_PARTIAL';
        ELSE
        v_dir:= 'ATG_APP_FULL'; 
    END IF;
    
    v_partial_dt := TO_DATE(p_lastModifiedDate, 'YYYY-MM-DD HH24:MI:SS');
    
    l_output := utl_file.fopen( v_dir, l_filename, 'w', 32767);
    --  utl_file.put( l_output, l_headr );
    UTL_FILE.PUT_LINE(l_output, l_headr);
    
    IF p_feedtype = 'FULL' then
        OPEN cur_ss;
    ELSE
        OPEN cur_ss_partial (v_partial_dt) ;
    end if;
    
    LOOP
      IF p_feedtype = 'FULL' then
          FETCH cur_ss INTO rec_ss;
          EXIT WHEN cur_ss%NOTFOUND; 
      else
          FETCH cur_ss_partial INTO rec_ss;
          EXIT WHEN cur_ss_partial%NOTFOUND; 
      end if;
      --EXIT WHEN cur_ss%NOTFOUND or v_total > 3;  -- Testing 
 
      v_total := v_total + 1;

      v_text := rec_ss.MIE_NEW                              ||'|'
            ||rec_ss.MIE_SKU_ID                             ||'|'
            ||rec_ss.MIE_DISPLAY_NAME                       ||'|'
            ||rec_ss.MIE_DESCRIPTION                        ||'|'
            ||rec_ss.MIE_PRODUCT_ID                         ||'|'
            ||rec_ss.MIE_ITEM_EXCL_SITE_FLAG_RES_CD         ||'|'
            ||rec_ss.MIE_SKU_PRODUCT_ID                     ||'|'
            ||rec_ss.MIE_IS_STORE_SKU                       ||'|'
            ||rec_ss.MIE_WEB_OFFERED_FLAG                   ||'|'
            ||rec_ss.MIE_DISABLE_FLAG                       ||'|'
            -- New 11/17/2014 RSP  
            ||rec_ss.MIE_BAB_WEB_OFFERED_FLAG               ||'|'                       
            ||rec_ss.MIE_BAB_DISABLED_FLAG                  ||'|'
            ||rec_ss.MIE_CA_WEB_OFFERED_FLAG                ||'|'
            ||rec_ss.MIE_CA_DISABLED_FLAG                   ||'|'
            ||rec_ss.MIE_GS_BBB_WEB_OFFERED                 ||'|'
            ||rec_ss.MIE_GS_BBB_DISABLED                    ||'|'
            ||rec_ss.MIE_GS_BAB_WEB_OFFERED                 ||'|'
            ||rec_ss.MIE_GS_BAB_DISABLED                    ||'|'
            ||rec_ss.MIE_GS_CA_WEB_OFFERED                  ||'|'
            ||rec_ss.MIE_GS_CA_DISABLED;
            
      -- Insert fetched cursor row into file
      UTL_FILE.PUT_LINE(l_output, v_text);

  END LOOP;    

  if cur_ss%isopen then
      close cur_ss;
  end if;   
    
    
  utl_file.fclose( l_output );    
  v_end_dt := sysdate;
  
  v_message := '<S> ' || TO_CHAR(v_start_dt,'DD-MON-YY HH24:MI:SS') || ' <E> ' || TO_CHAR(v_end_dt,'DD-MON-YY HH24:MI:SS') || 'Total SKUs Written: ' || v_total;
  INSERT INTO BBB_PIM_STG.PROCESS_SUPPORT
  ( id, process_cd, process_sub_cd, message, row_xng_usr )
  VALUES
  ( BBB_PIM_STG.process_support_seq.nextval , v_process_cd, v_process_sub_cd, v_message, USER );            
  COMMIT;  

exception

  WHEN UTL_FILE.INVALID_PATH THEN

    v_err_dt := SYSDATE;
    v_err_cd := SQLCODE;
    v_err_msg := RTRIM(SUBSTR(SQLERRM||' '||DBMS_UTILITY.FORMAT_ERROR_BACKTRACE,1,200));
    v_event_dt := SYSDATE;
    v_message := SUBSTR('Error in ' || v_process_sub_cd ||' UTL_FILE.INVALID_PATH - Error Code:' ||  v_err_cd || ' - Error Msg: ' || v_err_msg, 1, 512);    

    INSERT INTO BBB_PIM_STG.PROCESS_SUPPORT
    ( id, process_cd, process_sub_cd, message, row_xng_usr )
    VALUES
    ( BBB_PIM_STG.process_support_seq.nextval , v_process_cd, v_process_sub_cd, v_message, USER);            
    COMMIT;
    
  WHEN UTL_FILE.INVALID_MODE THEN

    v_err_dt := SYSDATE;
    v_err_cd := SQLCODE;
    v_err_msg := RTRIM(SUBSTR(SQLERRM||' '||DBMS_UTILITY.FORMAT_ERROR_BACKTRACE,1,200));
    v_event_dt := SYSDATE;
    v_message := SUBSTR('Error in ' || v_process_sub_cd ||' UTL_FILE.INVALID_MODE - Error Code:' ||  v_err_cd || ' - Error Msg: ' || v_err_msg, 1, 512);    

    INSERT INTO BBB_PIM_STG.PROCESS_SUPPORT
    ( id, process_cd, process_sub_cd, message, row_xng_usr )
    VALUES
    ( BBB_PIM_STG.process_support_seq.nextval , v_process_cd, v_process_sub_cd, v_message, USER );            
    COMMIT;

  WHEN UTL_FILE.INVALID_FILEHANDLE THEN

    v_err_dt := SYSDATE;
    v_err_cd := SQLCODE;
    v_err_msg := RTRIM(SUBSTR(SQLERRM||' '||DBMS_UTILITY.FORMAT_ERROR_BACKTRACE,1,200));
    v_event_dt := SYSDATE;
    v_message := SUBSTR('Error in ' || v_process_cd ||' UTL_FILE.INVALID_FILEHANDLE - Error Code:' ||  v_err_cd || ' - Error Msg: ' || v_err_msg, 1, 512);    

    INSERT INTO BBB_PIM_STG.PROCESS_SUPPORT
    ( id, process_cd, process_sub_cd, message, row_xng_usr )
    VALUES
    ( BBB_PIM_STG.process_support_seq.nextval , v_process_cd, v_process_sub_cd, v_message, USER );            
    COMMIT;

  WHEN UTL_FILE.INVALID_OPERATION THEN

    v_err_dt := SYSDATE;
    v_err_cd := SQLCODE;
    v_err_msg := RTRIM(SUBSTR(SQLERRM||' '||DBMS_UTILITY.FORMAT_ERROR_BACKTRACE,1,200));
    v_event_dt := SYSDATE;
    v_message := SUBSTR('Error in ' || v_process_cd ||' UTL_FILE.INVALID_OPERATION - Error Code:' ||  v_err_cd || ' - Error Msg: ' || v_err_msg, 1, 512);    

    INSERT INTO BBB_PIM_STG.PROCESS_SUPPORT
    ( id, process_cd, process_sub_cd, message, row_xng_usr )
    VALUES
    ( BBB_PIM_STG.process_support_seq.nextval , v_process_cd, v_process_sub_cd, v_message, USER );            
    COMMIT;

  WHEN UTL_FILE.WRITE_ERROR THEN

    v_err_dt := SYSDATE;
    v_err_cd := SQLCODE;
    v_err_msg := RTRIM(SUBSTR(SQLERRM||' '||DBMS_UTILITY.FORMAT_ERROR_BACKTRACE,1,200));
    v_event_dt := SYSDATE;

    v_message := SUBSTR('Error in ' || v_process_cd ||' UTL_FILE.WRITE_ERROR - Error Code:' ||  v_err_cd || ' - Error Msg: ' || v_err_msg, 1, 512);    

    INSERT INTO BBB_PIM_STG.PROCESS_SUPPORT
    ( id, process_cd, process_sub_cd, message, row_xng_usr )
    VALUES
    ( BBB_PIM_STG.process_support_seq.nextval , v_process_cd, v_process_sub_cd, v_message, USER );            
    COMMIT;

  WHEN OTHERS THEN
         
        IF cur_ss%ISOPEN THEN
            CLOSE cur_ss;
        END IF;                    
                
        v_err_dt := SYSDATE;
        v_err_cd := SQLCODE;
        v_err_msg := RTRIM(SUBSTR(SQLERRM||' '||DBMS_UTILITY.FORMAT_ERROR_BACKTRACE,1,200));
        v_event_dt := SYSDATE;
        v_message := SUBSTR('Error in ' || v_process_cd ||' SKU: ' || rec_ss.MIE_SKU_ID || ' - Error Code:' ||  v_err_cd || ' - Error Msg: ' || v_err_msg, 1, 512);    

        INSERT INTO BBB_PIM_STG.PROCESS_SUPPORT
        ( id, process_cd, process_sub_cd, message, row_xng_usr )
        VALUES
        ( BBB_PIM_STG.process_support_seq.nextval , v_process_cd, v_process_sub_cd, v_message, USER );            
        COMMIT;
        
                
end export_store_only;
 
PROCEDURE Export_OPB (
    p_filename         VARCHAR2,
    p_lastModifiedDate VARCHAR2,
    p_feedType         VARCHAR2)
  IS 
  l_rows       NUMBER;        -- pkg var to capture the number of rows affected from the function
  endeca_query VARCHAR(2000); -- pkg var to capture the SQL query
  nCount NUMBER;
  v_process_cd VARCHAR2(12); -- Endeca Procedure Name
  v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
  v_message VARCHAR(2500); -- message to log
  v_message_type CHAR(1); -- Message type to log, A for info, I for exception
  BEGIN 
	v_process_cd:='EN_FULL';
	v_process_sub_cd:='EXP_EXIM';
	v_message_type:='A';
	IF p_feedType  = 'PARTIAL' THEN
		v_process_cd:='EN_PARTIAL';
	END IF;
  IF p_feedType  = 'FULL' THEN
 endeca_query:='SELECT DISTINCT ''*#*'',(''OPB'' || ''_'' || decode(concept,''BEDBATHUS'',''1'',''BEDBATHCA'',''3'',''BUYBUYBABY'',''2'',''UNKNOWN'') || ''_'' ||REPLACE(keyword,''|'','''')) AS OPB_ID,REPLACE(keyword,''|'',''''),LISTAGG( product_id,'','') WITHIN GROUP (ORDER BY BOOST_SCORE DESC) AS OPB_PRODUCTID,decode(concept,''BEDBATHUS'',''1'',''BEDBATHCA'',''3'',''BUYBUYBABY'',''2'',''UNKNOWN'') AS SITE_NUM,''OPB_Terms'' FROM BBB_CORE.BBB_OMNITURE_REPORT_DATA GROUP BY concept,keyword';
 END IF; 

 SELECT dump_csv( endeca_query, '|', p_feedType, p_filename, NULL, 'NEW_RECORD|OPB_ID|OPB_SEARCH_TERM|OPB_PRODUCTID|SITE_NUM|ProductType')
  INTO l_rows
  FROM dual;
  
  dbms_output.put_line('Export_Omniture_Boosting_Products: '||l_rows);
  v_message :='Export_Omniture_Boosting_Products:ENDS: Total Rows:'||l_rows;
	Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
EXCEPTION
    WHEN OTHERS THEN -- handles all other errors
	   dbms_output.put_line('Export_Omniture_Boosting_Products: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
  v_message_type :='I';
  v_message := 'Export_Omniture_Boosting_Products: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64);
  Log_message(v_process_cd, v_process_sub_cd, RTRIM(SUBSTR( v_message,1,512)), v_message_type);
  commit;
  END Export_OPB;
  
  
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
  l_columnValue VARCHAR2(8000);
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
            dbms_sql.define_column( l_theCursor, i, l_columnValue, 8000 );
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
    dbms_sql.define_column( l_theCursor, 1, l_columnValue, 8000 );
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
END ATG_ENDECAEXPORT_PKG;
/
SHOW ERROR;
COMMIT;
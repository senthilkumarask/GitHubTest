--  DDL for Package Body ATG_GS_ENDECAEXPORT_PARTIALPKG
-- Build Version : 2.03.04.001

SET ECHO ON;
SET DEFINE OFF;
SET SERVEROUTPUT ON;

  CREATE OR REPLACE PACKAGE BODY BBB_CORE_PRV.ATG_GS_ENDECAEXPORT_PARTIALPKG 
AS
 
PROCEDURE Export_GS_Partial_Controller (p_Id varchar2, p_dataCenter varchar2)
  IS
    v_cat_prod_relation     NUMBER :=0; 
    v_prod                  NUMBER :=0; 
    v_sku                   NUMBER :=0; 
    
    rowcount_cat              NUMBER;
    rowcount_prod             NUMBER;
    rowcount_sku              NUMBER;
    
	running_status varchar(50) := 'RUNNING';
	complete_status varchar(50) := 'COMPLETE';
	failure_status varchar(50) := 'FAILURE';
    current_deployments number := 0;
	current_web_feed_generation number := 0;
	current_gs_feed_generation number := 0;
    v_last_mod_date timestamp;
	
	v_process_cd VARCHAR2(12); -- Endeca Procedure Name
	v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
	v_message VARCHAR(512); -- message to log
	v_message_type CHAR(1); --Message type to log, A for info, I for exception

   
  BEGIN
		v_process_cd:='EN_PARTIAL';
		v_message_type:='A';
		v_process_sub_cd:='GS_PAR_CONT';
		
		v_message := 'Export_GS_Partial_Controller : Begins';	
		ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		dbms_output.put_line('Export_GS_Partial_Controller: going to execute on environment : '||p_Id||' with data center: '||p_dataCenter);
		v_message := 'Export_GS_Partial_Controller: going to execute on environment : '||p_Id||' with data center: '||p_dataCenter;
		ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);        
		
		--Check for any current deployments
		IF p_Id = 'production' 
		THEN
			SELECT COUNT(*) INTO current_deployments FROM BBB_PIM_STG.ECP_FEED_MONITORING 
				where FEED_STATUS in('PRODUCTION_IN_PROGRESS', 'EMERGENCY_IN_PROGRESS');
			v_message := 'Export_GS_Partial_Controller: Production current deployments '||current_deployments;
			dbms_output.put_line('Production current deployments '||current_deployments);
		ELSE 
			SELECT COUNT(*) INTO current_deployments FROM BBB_PIM_STG.ECP_FEED_MONITORING 
				where FEED_STATUS in('STAGING_IN_PROGRESS', 'EMERGENCY_IN_PROGRESS');
			v_message := 'Export_GS_Partial_Controller: Staging current deployments '||current_deployments;	
			dbms_output.put_line('Staging current deployments '||current_deployments);
		END IF;    
		ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		
		--Check for any current Web Endeca Feed Generation
		
		 SELECT count(*) into current_web_feed_generation FROM BBB_PIM_STG.BBB_DEPLOYMENT_POLLING WHERE LOWER(ID) = p_Id and data_center = p_dataCenter and endeca_in_progress='Y';
		 v_message := 'Export_GS_Partial_Controller: Endeca Feed Generation for Web '||current_web_feed_generation;	
		 dbms_output.put_line('Endeca Feed Generation for Web '||current_web_feed_generation);
		 ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		 
		 --Check for any current GS Endeca Feed Generation
		 
		 SELECT count(*) into current_gs_feed_generation FROM BBB_PIM_STG.BBB_GS_ENDECA_DB_LOG WHERE data_center = p_dataCenter and status ='RUNNING';
		 dbms_output.put_line('Endeca Feed Generation for GS '||current_gs_feed_generation);
		 v_message := 'Export_GS_Partial_Controller: Endeca Feed Generation for GS '||current_gs_feed_generation;	
		 ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		 
		IF current_deployments > 0 or current_web_feed_generation > 0 or current_gs_feed_generation > 0
		THEN
			
				dbms_output.put_line('Execution of Endeca for PARTIAL Feed terminated ');
				v_message := 'Export_GS_Partial_Controller: Execution of Endeca for PARTIAL Feed terminated';	
				ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		else 
				select execution_date into v_last_mod_date from (select execution_date from BBB_PIM_STG.BBB_GS_ENDECA_DB_LOG  where feed_type='PARTIAL' and Data_center=p_dataCenter and status='COMPLETE' order by execution_date desc) WHERE ROWNUM=1;
				dbms_output.put_line('going to execute '||v_last_mod_date);
				v_message := 'Export_GS_Partial_Controller: going to execute '||v_last_mod_date;	
				ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
				
				 insert into BBB_PIM_STG.BBB_GS_ENDECA_DB_LOG (FEED_NAME, FEED_TYPE, STATUS, EXECUTION_DATE, DATA_CENTER) values ('PARTIAL-'||to_char(systimestamp, 'YYYY-MM-DD HH24:MI:SS'), 'PARTIAL', running_status , sysdate, p_dataCenter);
				commit;
				
				--category
				select count(*) into rowcount_cat From  bbb_category
					where last_mod_date > v_last_mod_date;
				v_message := 'Export_GS_Partial_Controller: Category Products Count : '||rowcount_cat;	
				ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
				
				if rowcount_cat > 0 then
					--variable value change
					v_cat_prod_relation :=1;       
					--category product relation
					ATG_GS_EndecaExport_Pkg.Export_GS_Category_Products ('Products_Category.txt', v_last_mod_date, 'PARTIAL');
				end if;
				
				rowcount_cat:=0;
				select count(*) into rowcount_cat from  bbb_product a, dcs_prd_chldsku b1, dcs_prd_chldsku b2, bbb_sku c1, bbb_sku c2, dcs_price d1, bbb_price e1, dcs_price d2, bbb_price e2
					where a.sku_low_price = b1.sku_id
					and a.sku_high_price = b2.sku_id
					and b1.sku_id = c1.sku_id
					and b2.sku_id = c2.sku_id
					and c1.sku_id = d1.sku_id
					and c2.sku_id = d2.sku_id
					and d1.price_id = e1.price_id
					and d2.price_id = e2.price_id
					and a.collection_flag = '1'
					and ( e1.last_mod_date > v_last_mod_date
						  or e2.last_mod_date > v_last_mod_date ) ;
				dbms_output.put_line('Export_GS_Partial_Controller: check for collection product price changes '||rowcount_cat);
				v_message := 'Export_GS_Partial_Controller: check for collection product price changes '||rowcount_cat;	
				ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
				
				if rowcount_cat > 0 then
					--variable value change
					v_cat_prod_relation :=1;       
					--category product relation
					ATG_GS_EndecaExport_Pkg.Export_GS_Category_Products ('Products_Category.txt', v_last_mod_date, 'PARTIAL');
				end if;
				
				 --category products
				select count(*) into rowcount_prod From  BBB_PRODUCT where last_mod_date > v_last_mod_date ;
				v_message := 'Export_GS_Partial_Controller: Products Count : '||rowcount_prod;	
				ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
				
				if rowcount_prod > 0 then
					--variable value change
					v_prod :=1;   
					--If it called for category no need to call it again            
					if v_cat_prod_relation=0 then
						--product category relation 
						ATG_GS_EndecaExport_Pkg.Export_GS_Category_Products ('Products_Category.txt', v_last_mod_date, 'PARTIAL');
					end if;
					
				end if;
			 

			  --sku
			  select count(*) into rowcount_sku From  BBB_SKU where last_mod_date > v_last_mod_date ;
			  v_message := 'Export_GS_Partial_Controller: SKUs Count : '||rowcount_sku;	
			  ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
			  
			  if rowcount_sku > 0 then
				 v_sku :=1;
				--product sku relation
				--if it is called for bbb_product or BBB_PRD_RELN no need to call it again   
			  end if;
			  
			  ---------------------------------------
			--mandatory file for partial generation: As per Divakar's discussion with Alan
			ATG_GS_EndecaExport_Pkg.Export_GS_SKU_Item_Attributes ('SKU_Item_Attributes.txt', v_last_mod_date, 'PARTIAL');
			ATG_GS_EndecaExport_Pkg.Export_GS_Product_Item_Attr ('Product_Item_Attributes.txt', v_last_mod_date, 'PARTIAL');
			ATG_GS_EndecaExport_Pkg.Export_GS_Accessories ('accessoryProducts.txt', v_last_mod_date, 'PARTIAL');
			
			ATG_GS_EndecaExport_Pkg.Export_GS_Simple_Products ('SimpleProducts.txt', v_last_mod_date, 'PARTIAL');
			ATG_GS_EndecaExport_Pkg.Export_GS_Lead_Products ('LeadProducts.txt', v_last_mod_date, 'PARTIAL');
			ATG_GS_EndecaExport_Pkg.Export_GS_Product_SKUs ('Product_SKUs.txt', v_last_mod_date, 'PARTIAL');
			ATG_GS_EndecaExport_Pkg.Export_GS_SKU_Site_Properties ('SKU_Site_Properties.txt', v_last_mod_date, 'PARTIAL');
			ATG_GS_EndecaExport_Pkg.Export_GS_Product_Site_Prop('Product_Site_Properties.txt', v_last_mod_date, 'PARTIAL');
			ATG_GS_EndecaExport_Pkg.Export_GS_Brands  ('brands.txt', v_last_mod_date, 'PARTIAL') ;
			ATG_GS_EndecaExport_Pkg.Export_GS_Prices  ('Prices.txt', v_last_mod_date, 'PARTIAL') ;
			
			ATG_GS_EndecaExport_Pkg.Export_GS_SKUs ('SKUs.txt', v_last_mod_date, 'PARTIAL');
			ATG_GS_EndecaExport_Pkg.Export_GS_Item_Attributes ('Item_Attributes.txt', v_last_mod_date, 'PARTIAL');
			ATG_GS_EndecaExport_Pkg.Export_GS_Reviews ('Reviews.txt', v_last_mod_date, 'PARTIAL'); -- full reviews, even in partial
			ATG_GS_EndecaExport_Pkg.Export_GS_SKU_Features ('SKU_Features.txt', v_last_mod_date, 'PARTIAL'); -- full reviews, even in partial
			ATG_GS_EndecaExport_Pkg.Export_GS_BBB_Channel_Category ('GS_BBB_Channel_Category.txt', v_last_mod_date, 'PARTIAL');
			ATG_GS_EndecaExport_Pkg.Export_GS_BAB_Channel_Category ('GS_BAB_Channel_Category.txt', v_last_mod_date, 'PARTIAL');
			ATG_GS_EndecaExport_Pkg.Export_GS_CA_Channel_Category ('GS_CA_Channel_Category.txt', v_last_mod_date, 'PARTIAL');
			---------------------------------------

			ATG_GS_EndecaExport_Pkg.Export_GS_Gofile('partial.go',v_last_mod_date,'PARTIAL');
			v_message := 'Export_GS_Partial_Controller Ends';	
			ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
			update BBB_PIM_STG.BBB_GS_ENDECA_DB_LOG set STATUS = complete_status where FEED_TYPE='PARTIAL' and STATUS=running_status and data_center = p_dataCenter;
			commit;
		END IF;
		
		 EXCEPTION
		WHEN OTHERS THEN  -- handles all other errors
          rollback;
          update BBB_PIM_STG.BBB_GS_ENDECA_DB_LOG set STATUS = failure_status where FEED_TYPE='PARTIAL' and STATUS=running_status and data_center = p_dataCenter;
          commit;
        -- UTL_MAIL.send( sender=>'kkumar1@SAPIENT.COM', recipients=>'kkumar1@SAPIENT.COM', subject=>'testing subject', message=>'testing body');
        
		dbms_output.put_line('Export_GS_Partial_Controller: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
		v_message_type :='I';
		v_message := 'Export_GS_Partial_Controller: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;	
		ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

END Export_GS_Partial_Controller;



 PROCEDURE Export_GS_Full_Controller (p_Id varchar2, p_dataCenter varchar2) IS
	running_status varchar(50) := 'RUNNING';
	complete_status varchar(50) := 'COMPLETE';
	failure_status varchar(50) := 'FAILURE';
    current_deployments number := 0;
	current_web_feed_generation number := 0;
	current_gs_feed_generation number := 0;
    v_last_mod_date timestamp;
	v_process_cd VARCHAR2(12); -- Endeca Procedure Name
	v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
	v_message VARCHAR(512); -- message to log
	v_message_type CHAR(1); --Message type to log, A for info, I for exception
  BEGIN
    dbms_output.put_line('Export_GS_Full_Controller : Begins');
	v_process_cd:='EN_FULL';
	v_message_type:='A';
	v_process_sub_cd:='GS_FULL_CONT';
	v_message := 'Export_GS_Full_Controller : Begins';	
	ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	
	v_message := 'Export_GS_Full_Controller: going to execute on environment : '||p_Id||' with data center: '||p_dataCenter;
	ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type); 
	--Check for any current deployments
    IF p_Id = 'production' 
    THEN
        SELECT COUNT(*) INTO current_deployments FROM BBB_PIM_STG.ECP_FEED_MONITORING 
            where FEED_STATUS in('PRODUCTION_IN_PROGRESS', 'EMERGENCY_IN_PROGRESS');
        dbms_output.put_line('Production current deployments '||current_deployments);
		v_message := 'Export_GS_Full_Controller : Production current deployments '||current_deployments;	
    ELSE 
        SELECT COUNT(*) INTO current_deployments FROM BBB_PIM_STG.ECP_FEED_MONITORING 
            where FEED_STATUS in('STAGING_IN_PROGRESS', 'EMERGENCY_IN_PROGRESS');
        dbms_output.put_line('Staging current_deployments '||current_deployments);
		v_message := 'Export_GS_Full_Controller : Staging current_deployments '||current_deployments;	
    END IF;    
	ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	
	--Check for any current Web Endeca Feed Generation
	
	 SELECT count(*) into current_web_feed_generation FROM BBB_PIM_STG.BBB_DEPLOYMENT_POLLING WHERE LOWER(ID) = p_Id and data_center = p_dataCenter and endeca_in_progress='Y';
	 dbms_output.put_line('Endeca Feed Generation for Web '||current_web_feed_generation);
	 v_message := 'Export_GS_Full_Controller : Endeca Feed Generation for Web '||current_web_feed_generation;	
	 ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
	 --Check for any current GS Endeca Feed Generation
	 
	 SELECT count(*) into current_gs_feed_generation FROM BBB_PIM_STG.BBB_GS_ENDECA_DB_LOG WHERE data_center = p_dataCenter and status ='RUNNING';
	 dbms_output.put_line('Endeca Feed Generation for GS '||current_gs_feed_generation);
     v_message := 'Export_GS_Full_Controller : Endeca Feed Generation for GS '||current_gs_feed_generation;	
	 ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
   
    IF current_deployments > 0 or current_web_feed_generation > 0 or current_gs_feed_generation > 0
    THEN
        
            dbms_output.put_line('Execution of Endeca for FULL Feed terminated ');
			v_message := 'Export_GS_Full_Controller : Execution of Endeca for FULL Feed terminated';	
			ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
        ELSE
            insert into BBB_PIM_STG.BBB_GS_ENDECA_DB_LOG (FEED_NAME, FEED_TYPE, STATUS, EXECUTION_DATE, DATA_CENTER) values ('FULL-'||to_char(systimestamp, 'YYYY-MM-DD HH24:MI:SS'), 'FULL', running_status , sysdate, p_dataCenter);
			commit;
            dbms_output.put_line('Execution of Endeca for FULL Feed started');
			v_message := 'Export_GS_Full_Controller : Execution of Endeca for FULL Feed started';	
			ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
			
            ATG_GS_EndecaExport_Pkg.Export_GS_BBB_Taxonomy ('GS_BBB_Taxonomy.txt', sysdate, 'FULL');
            ATG_GS_EndecaExport_Pkg.Export_GS_BAB_Taxonomy ('GS_BAB_Taxonomy.txt', sysdate, 'FULL');
            ATG_GS_EndecaExport_Pkg.Export_GS_CA_Taxonomy ('GS_CA_Taxonomy.txt', sysdate, 'FULL');
	    	ATG_GS_EndecaExport_Pkg.Export_GS_BBB_Channel_Category ('GS_BBB_Channel_Category.txt', sysdate, 'FULL');
	   		ATG_GS_EndecaExport_Pkg.Export_GS_BAB_Channel_Category ('GS_BAB_Channel_Category.txt', sysdate, 'FULL');
	    	ATG_GS_EndecaExport_Pkg.Export_GS_CA_Channel_Category ('GS_CA_Channel_Category.txt', sysdate, 'FULL');
            ATG_GS_EndecaExport_Pkg.Export_GS_Category_Products ('Products_Category.txt', sysdate, 'FULL');
            
            ATG_GS_EndecaExport_Pkg.Export_GS_Item_Attributes ('Item_Attributes.txt', sysdate, 'FULL');
            ATG_GS_EndecaExport_Pkg.Export_GS_SKU_Item_Attributes ('SKU_Item_Attributes.txt', sysdate, 'FULL');
            ATG_GS_EndecaExport_Pkg.Export_GS_Brands ('brands.txt', sysdate, 'FULL');
            
            ATG_GS_EndecaExport_Pkg.Export_GS_Simple_Products ('SimpleProducts.txt', sysdate, 'FULL');
            ATG_GS_EndecaExport_Pkg.Export_GS_Lead_Products ('LeadProducts.txt', sysdate, 'FULL');
            ATG_GS_EndecaExport_Pkg.Export_GS_Accessories ('accessoryProducts.txt', sysdate, 'FULL');
            
            ATG_GS_EndecaExport_Pkg.Export_GS_Product_SKUs ('Product_SKUs.txt', sysdate, 'FULL');
            ATG_GS_EndecaExport_Pkg.Export_GS_Prices ('Prices.txt', sysdate, 'FULL');

            ATG_GS_EndecaExport_Pkg.Export_GS_SKUs ('SKUs.txt', sysdate, 'FULL');
            ATG_GS_EndecaExport_Pkg.Export_GS_Product_Site_Prop ('Product_Site_Properties.txt', sysdate, 'FULL');
            ATG_GS_EndecaExport_Pkg.Export_GS_SKU_Site_Properties ('SKU_Site_Properties.txt', sysdate, 'FULL');
            ATG_GS_EndecaExport_Pkg.Export_GS_Product_Item_Attr ('Product_Item_Attributes.txt', sysdate, 'FULL');
            ATG_GS_EndecaExport_Pkg.Export_GS_Reviews ('Reviews.txt', sysdate, 'FULL');
            ATG_GS_EndecaExport_Pkg.Export_GS_Features ('Features.txt', sysdate, 'FULL'); 
            ATG_GS_EndecaExport_Pkg.Export_GS_SKU_Features ('SKU_Features.txt', sysdate, 'FULL'); 
            ATG_GS_EndecaExport_Pkg.Export_GS_ColorColorGroups ('ColorColorGroups.txt', sysdate, 'FULL');
            ATG_GS_EndecaExport_Pkg.Export_GS_Gofile('full.go',Sysdate,'FULL');
            dbms_output.put_line('Export_GS_Full_Controller complete');
			
			update BBB_PIM_STG.BBB_GS_ENDECA_DB_LOG set STATUS = complete_status where FEED_TYPE='FULL' and STATUS=running_status and data_center = p_dataCenter;
			v_message := 'Export_GS_Full_Controller Ends';	
			ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
          commit;
    END IF;
    dbms_output.put_line('File generation ends');
    EXCEPTION
    WHEN OTHERS THEN  -- handles all other errors
          rollback;
          update BBB_PIM_STG.BBB_GS_ENDECA_DB_LOG set STATUS = failure_status where FEED_TYPE='FULL' and STATUS=running_status and data_center = p_dataCenter;
          commit;
        -- UTL_MAIL.send( sender=>'kkumar1@SAPIENT.COM', recipients=>'kkumar1@SAPIENT.COM', subject=>'testing subject', message=>'testing body');
		dbms_output.put_line('Export_GS_Full_Controller: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM);
		v_message_type :='I';
		v_message := 'Export_GS_Full_Controller: Error! :'|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE || ' : ' ||SQLCODE || ' ' || SQLERRM;
		ATG_GS_EndecaExport_Pkg.GS_Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
END Export_GS_Full_Controller;


    
End ATG_GS_EndecaExport_PartialPkg;

/
show error;
COMMIT;

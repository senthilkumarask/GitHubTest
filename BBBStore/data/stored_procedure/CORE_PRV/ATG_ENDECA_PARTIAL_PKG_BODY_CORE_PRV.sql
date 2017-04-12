-- build version: Andromeda_AugPS_11Jul
-- Andromeda.3.4.1
SET ECHO ON;
set DEFINE OFF;
set SERVEROUTPUT ON;

--------------------------------------------------------
--  File created - Tuesday-January-15-2013   
--  DDL for Package Body ATG_ENDECAEXPORT_PARTIAL_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY BBB_CORE_PRV.ATG_ENDECAEXPORT_PARTIAL_PKG
AS

-- @Description: Procedure to generate Partial feed files.
-- @Usage: set serveroutput on;
--         execute ATG_EndecaExport_Partial_Pkg.Export_Controller ('2012-10-01');
-- @Params: p_lastModifiedDate: date of execution which will set the time period since last modified date of asset.

PROCEDURE Export_Controller (p_lastModifiedDate varchar2, p_feedType varchar2)
  IS
    v_cat_prod_relation     NUMBER :=0;
    v_prod                  NUMBER :=0;
    v_sku                   NUMBER :=0;

    rowcount_static           NUMBER;
    rowcount_guides           NUMBER;
    rowcount_cat              NUMBER;
    rowcount_prod             NUMBER;
    rowcount_sku              NUMBER;

   	v_process_cd VARCHAR2(12); -- Endeca Procedure Name
	v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
	v_message VARCHAR(512); -- message to log
	v_message_type CHAR(1); --Message type to log, A for info, I for exception

  BEGIN
		v_process_cd:='EN_PARTIAL';
		IF p_feedType  = 'FULL' THEN
			v_process_cd:='EN_FULL';
		END IF;
		v_message_type:='A';
		v_process_sub_cd:='Ex_Cntroller';

        dbms_output.put_line('Export_Controller: going to execute on: '||p_lastModifiedDate||' with feed type: '||p_feedType);
		v_message := 'Export_Controller: going to execute on: '||p_lastModifiedDate||' with feed type: '||p_feedType;
		ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

        --static
        select count(*) into rowcount_static From  bbb_Static_Template where last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') ;
        dbms_output.put_line('Export_Controller: Static Pages '||rowcount_static);
		v_message := 'Export_Controller: going to execute on: '||p_lastModifiedDate||' with feed type: '||p_feedType;
		ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
        if rowcount_static > 0 then
            -- generating StaticPages.txt
            ATG_EndecaExport_Pkg.Export_StaticPages ('StaticPages.txt', p_lastModifiedDate, p_feedType) ;
            -- generating StaticPages.txt
            ATG_EndecaExport_Pkg.Export_StaticPages_Site ('StaticPages.txt', p_lastModifiedDate, p_feedType) ;
        end if;

        --guides
        select count(*) into rowcount_guides From  BBB_GUIDES where last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') ;
        dbms_output.put_line('Export_Controller: '||rowcount_guides);
		v_message := 'Export_Controller: Export Guides Sites '||rowcount_guides;
		ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
        if rowcount_guides > 0 then
            -- generating Guides.txt
            ATG_EndecaExport_Pkg.Export_Guides ('Guides.txt', p_lastModifiedDate, p_feedType) ;
            -- generating Guides_Sites.txt
            ATG_EndecaExport_Pkg.Export_Guides_Sites ('Guides_Sites.txt', p_lastModifiedDate, p_feedType) ;
        end if;

        --If any category had been modified then send Products_category.txt flat file
        select count(*) into rowcount_cat From  bbb_category
            where last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') ;
        dbms_output.put_line('Export_Controller: '||rowcount_cat);
		v_message := 'Export_Controller: Category Products'||rowcount_cat;
		ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
        if rowcount_cat > 0 then
            --variable value change
            v_cat_prod_relation :=1;
            --category product relation
            ATG_EndecaExport_Pkg.Export_Category_Products ('Products_Category.txt', p_lastModifiedDate, p_feedType);
        end if;

        --If any price of any sku had been then send Products_category.txt flat file
        rowcount_cat:=0;
        -- call SP Export_Category_Products only when it has not been called above.
        if v_cat_prod_relation=0 then
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
                and ( e1.last_mod_date > to_date('2012-09-11 00:00:00','YYYY-MM-DD HH24:MI:SS')
                      or e2.last_mod_date > to_date('2012-09-11 00:00:00','YYYY-MM-DD HH24:MI:SS') ) ;
            dbms_output.put_line('Export_Controller: check for collection product price changes '||rowcount_cat);
    		v_message := 'Export_Controller: check for collection product price changes '||rowcount_cat;
    		ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);


            if rowcount_cat > 0 then
                --variable value change
                v_cat_prod_relation :=1;
                --category product relation
                ATG_EndecaExport_Pkg.Export_Category_Products ('Products_Category.txt', p_lastModifiedDate, p_feedType);
            end if;
        end if;
        --If any product had been modified then send Products_category.txt flat file
        select count(*) into rowcount_prod From  BBB_PRODUCT where last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') ;
        if rowcount_prod > 0 then
            --variable value change
            v_prod :=1;
            --If it called for category no need to call it again
            if v_cat_prod_relation=0 then
                --product category relation
                ATG_EndecaExport_Pkg.Export_Category_Products ('Products_Category.txt', p_lastModifiedDate, p_feedType);
            end if;
            --Media+Product relation
            ATG_EndecaExport_Pkg.Export_Prod_Media_Sites ('Prod_Media_Sites.txt', p_lastModifiedDate, p_feedType);
        end if;

		--sku
		select count(*) into rowcount_sku From  BBB_SKU where last_mod_date > to_date(p_lastModifiedDate,'YYYY-MM-DD HH24:MI:SS') ;
		if rowcount_sku > 0 then
			v_sku :=1;
			--product sku relation
			--if it is called for bbb_product or BBB_PRD_RELN no need to call it again
		end if;

        ---------------------------------------
        --mandatory file for partial generation: As per Divakar's discussion with Alan
		ATG_EndecaExport_Pkg.Export_Products_And_Skus (p_lastModifiedDate, p_feedType); -- in partial
        ATG_EndecaExport_Pkg.Export_SKU_Item_Attributes ('SKU_Item_Attributes.txt', p_lastModifiedDate, p_feedType);
        ATG_EndecaExport_Pkg.Export_Product_Item_Attributes ('Product_Item_Attributes.txt', p_lastModifiedDate, p_feedType);
        ATG_EndecaExport_Pkg.Export_Accessories ('accessoryProducts.txt', p_lastModifiedDate, p_feedType);
        ATG_EndecaExport_Pkg.Export_Media ('Media.txt', p_lastModifiedDate, p_feedType);
        ATG_EndecaExport_Pkg.Export_Collection_Products ('CollectionProducts.txt', p_lastModifiedDate, p_feedType);
        ATG_EndecaExport_Pkg.Export_Simple_Products ('SimpleProducts.txt', p_lastModifiedDate, p_feedType);
        ATG_EndecaExport_Pkg.Export_Lead_Products ('LeadProducts.txt', p_lastModifiedDate, p_feedType);
        ATG_EndecaExport_Pkg.Export_UnlikeProducts ('UnlikeProducts.txt', p_lastModifiedDate, p_feedType);
        ATG_EndecaExport_Pkg.Export_Collection_SKUs ('Collection_SKUs.txt', p_lastModifiedDate, p_feedType);
        ATG_EndecaExport_Pkg.Export_Product_SKUs ('Product_SKUs.txt', p_lastModifiedDate, p_feedType);
        ATG_EndecaExport_Pkg.Export_SKU_Site_Properties ('SKU_Site_Properties.txt', p_lastModifiedDate, p_feedType);
        ATG_EndecaExport_Pkg.Export_Product_Site_Properties ('Product_Site_Properties.txt', p_lastModifiedDate, p_feedType);
		----- Release 2.1 specific property files START
		ATG_EndecaExport_Pkg.Export_Prod_site_sales_data ('Product_Site_sales_data.txt', p_lastModifiedDate, p_feedType);
		----- Release 2.1 specific property files END
        ATG_EndecaExport_Pkg.Export_Brands  ('brands.txt', p_lastModifiedDate, p_feedType) ;
        ATG_EndecaExport_Pkg.Export_Prices  ('Prices.txt', p_lastModifiedDate, p_feedType) ;
		ATG_EndecaExport_Pkg.Export_Prices_Updates('Prices_Updates.txt', p_lastModifiedDate, p_feedType);
        ATG_EndecaExport_Pkg.Export_SKUs ('SKUs.txt', p_lastModifiedDate, p_feedType);
        ATG_EndecaExport_Pkg.Export_Item_Attributes ('Item_Attributes.txt', p_lastModifiedDate, p_feedType);
        ATG_EndecaExport_Pkg.Export_Reviews ('Reviews.txt', p_lastModifiedDate, p_feedType); -- full reviews, even in partial
        ATG_EndecaExport_Pkg.Export_SKU_Features ('SKU_Features.txt', p_lastModifiedDate, p_feedType);
		----- Release 2.2.1 Harmon Tab Sites File START
		ATG_EndecaExport_Pkg.Export_Tabs_Sites ('Harmon_Tabs_Sites.txt', p_lastModifiedDate, p_feedType);
		ATG_EndecaExport_Pkg.Export_Prod_Harmon_Tabs ('Prod_Harmon_Tabs.txt', p_lastModifiedDate, p_feedType);
		----- Release 2.2.1 Harmon Tab Sites File END
		ATG_Endecaexport_Pkg.Export_Gofile('partial.go', p_lastModifiedDate, p_feedType);

		dbms_output.put_line('Export_Controller: Static page table changed');
		v_message := 'Export_Controller: Static page table changed';
		ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
		v_message := 'Export_Controller: ends '||p_lastModifiedDate||' with feed type: '||p_feedType;
		ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
  END Export_Controller;

-- @Description: Entry point for DB job to invoke file generation
-- @Usage: set serveroutput on;
--         execute ATG_EndecaExport_Partial_Pkg.Export_Full_Controller ('production','EAST','PARTIAL');
-- @Params: p_Id: can be 'production' or 'staging'
--          p_dataCenter: can be EAST or WEST
--          p_feedType: Can be PARTIAL or FULL

 PROCEDURE Export_Full_Controller (p_Id varchar2, p_dataCenter varchar2, p_feedType varchar2) IS
    v_valid_status varchar(50) := 'COMPLETE_SWITCH_B';
    v_last_mod_date timestamp;
	v_process_cd VARCHAR2(12); -- Endeca Procedure Name
	v_process_sub_cd VARCHAR2(12); -- CANNOT EXCEED 12 BYTES
	v_message VARCHAR(512); -- message to log
	v_message_type CHAR(1); --Message type to log, A for info, I for exception
BEGIN
	v_process_cd:='EN_FULL';
	IF p_feedType  = 'PARTIAL' THEN
		v_process_cd:='EN_PARTIAL';
	END IF;
	v_message_type:='A';
	v_process_sub_cd:='Ex_Ful_Cntlr';

    dbms_output.put_line('Export_Full_Controller: BEGIN');
	v_message := 'Export_Full_Controller: BEGIN';
	ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

    -- find out the time when the last execution of feed was done
    Select t.last_execution_date into v_last_mod_date from bbb_pim_stg.BBB_ENDECA_EXECUTION_STATE t WHERE lower(id) = p_Id;
    dbms_output.put_line('Export_Full_Controller: last_mod_date '||to_char(v_last_mod_date,'YYYY-MM-DD HH24:MI:SS'));
	v_message := 'Export_Full_Controller: last_mod_date '||to_char(v_last_mod_date,'YYYY-MM-DD HH24:MI:SS');
	ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

    -- find out the status of feed. COMPLETE_SWITCH_A, COMPLETE_SWITCH_B, COMPLETE_STAGING should trigger the file generation for Endeca
    SELECT POLLING_STATUS into v_valid_status FROM bbb_pim_stg.BBB_DEPLOYMENT_POLLING WHERE LOWER(ID) = p_Id and data_center = p_dataCenter;
    dbms_output.put_line('Export_Full_Controller: v_valid_status '||v_valid_status);
	v_message := 'Export_Full_Controller: v_valid_status '||v_valid_status;
	ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

    -- find out the feed type and invoke the PROCs.
    IF p_feedType = 'PARTIAL' THEN
        IF v_valid_status = 'COMPLETE_SWITCH_A' or v_valid_status = 'COMPLETE_SWITCH_B' or v_valid_status = 'COMPLETE_STAGING'
        THEN
            -- update the deployment polling table before starting file generation
            update bbb_pim_stg.BBB_DEPLOYMENT_POLLING set polling_status = 'EndecaStart', ENDECA_IN_PROGRESS='Y', last_modified_date = sysdate where lower(id)= p_Id and data_center = p_dataCenter;
            commit;

            dbms_output.put_line('Export_Full_Controller: Start ATG_EndecaExport_Partial_Pkg.Export_Controller');
			v_message := 'Export_Full_Controller: Start PARTIAL ATG_EndecaExport_Partial_Pkg.Export_Controller';
			ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);

            -- call partial generation now
            ATG_EndecaExport_Partial_Pkg.Export_Controller (to_char(v_last_mod_date,'YYYY-MM-DD HH24:MI:SS'), p_feedType);

            dbms_output.put_line('Export_Full_Controller: End ATG_EndecaExport_Partial_Pkg.Export_Controller');
			v_message := 'Export_Full_Controller: End PARTIAL ATG_EndecaExport_Partial_Pkg.Export_Controller';
			ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
            -- update the timestamp of this execution
            update bbb_pim_stg.BBB_ENDECA_EXECUTION_STATE set last_execution_date=systimestamp WHERE lower(id) = p_Id;
            commit;
        END IF;
    END IF;

    IF p_feedType = 'FULL' THEN
        IF v_valid_status = 'COMPLETE_SWITCH_A' or v_valid_status = 'COMPLETE_SWITCH_B' or v_valid_status = 'COMPLETE_STAGING'
        THEN
            -- update the deployment polling table before starting file generation
            update bbb_pim_stg.BBB_DEPLOYMENT_POLLING set polling_status = 'EndecaStart', ENDECA_IN_PROGRESS='Y', last_modified_date = sysdate where lower(id)= p_Id and data_center = p_dataCenter;
            commit;

            dbms_output.put_line('Export_Full_Controller: Export_Full_Controller started');
			v_message := 'Export_Full_Controller: FULL feed: Export_Full_Controller started';
			ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
            -- call full file generation now
            ATG_EndecaExport_Pkg.Export_Guides ('Guides.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Guides_Sites ('Guides_Sites.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_StaticPages ('StaticPages.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_StaticPages_Site ('StaticPages_Site.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_BBB_Taxonomy ('BBB_Taxonomy.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_BAB_Taxonomy ('BAB_Taxonomy.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_CA_Taxonomy ('CA_Taxonomy.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Category_Products ('Products_Category.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Media ('Media.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Prod_Media_Sites ('Prod_Media_Sites.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Item_Attributes ('Item_Attributes.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_SKU_Item_Attributes ('SKU_Item_Attributes.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Brands ('brands.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Collection_Products ('CollectionProducts.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Simple_Products ('SimpleProducts.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Lead_Products ('LeadProducts.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Accessories ('accessoryProducts.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_UnlikeProducts ('UnlikeProducts.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Product_SKUs ('Product_SKUs.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Collection_SKUs ('Collection_SKUs.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Prices ('Prices.txt', sysdate, p_feedType);
			ATG_EndecaExport_Pkg.Export_Prices_Updates('Prices_Updates.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_SKUs ('SKUs.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Product_Site_Properties ('Product_Site_Properties.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_SKU_Site_Properties ('SKU_Site_Properties.txt', sysdate, p_feedType);
			----- Release 2.1 specific property files START
			ATG_EndecaExport_Pkg.Export_Prod_site_sales_data ('Product_Site_sales_data.txt', sysdate, p_feedType);
			----- Release 2.1 specific property files END
            ATG_EndecaExport_Pkg.Export_Product_Item_Attributes ('Product_Item_Attributes.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Reviews ('Reviews.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Features ('Features.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_SKU_Features ('SKU_Features.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_ColorColorGroups ('ColorColorGroups.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Colleges_States ('College.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Colleges_Taxonomy ('College_Taxonomy.txt', sysdate, p_feedType);
			----- Release 2.2.1 Harmon Tab Sites File START
            ATG_EndecaExport_Pkg.Export_Tabs_Sites ('Harmon_Tabs_Sites.txt', sysdate, p_feedType);
            ATG_EndecaExport_Pkg.Export_Prod_Harmon_Tabs ('Prod_Harmon_Tabs.txt', sysdate, p_feedType);
			----- Release 2.2.1 Harmon Tab Sites File END
            ATG_Endecaexport_Pkg.Export_Gofile('full.go', sysdate, p_feedType);

            dbms_output.put_line('Export_Full_Controller: complete!');
			v_message := 'Export_Full_Controller: FULL feed: ENDS';
			ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
            -- update the timestamp of this execution
            update bbb_pim_stg.BBB_ENDECA_EXECUTION_STATE set last_execution_date=systimestamp WHERE lower(id) = p_Id;
            commit;
        END IF;
    END IF;
    dbms_output.put_line('Export_Full_Controller: File generation ends');
	v_message := 'Export_Full_Controller: File generation ends';
	ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
    EXCEPTION
    WHEN OTHERS THEN  -- handles all other errors
          rollback;
          -- update status to EndecaFail, so that BCC project can get closed out.
          update bbb_pim_stg.BBB_DEPLOYMENT_POLLING set last_modified_date=sysdate, polling_status = 'EndecaFail', ENDECA_IN_PROGRESS='N' where lower(id)= p_Id and data_center = p_dataCenter;
          commit;
          dbms_output.put_line('Export_Full_Controller: FATAL ERROR. Please check logs for SPs getting executed in sequence.');
          v_message := 'Export_Full_Controller: FATAL ERROR. Please check logs for SPs getting executed in sequence.';
		  v_message_type :='I';
		  ATG_EndecaExport_Pkg.Log_message(v_process_cd, v_process_sub_cd, v_message, v_message_type);
END Export_Full_Controller;
End Atg_Endecaexport_Partial_Pkg;
/
SHOW ERROR;
COMMIT;
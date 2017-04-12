SET ECHO ON;
SET DEFINE OFF;
SET SERVEROUTPUT ON;


--------------------------------------------------------
--  DDL for Package Body UPDATE_RECORDS_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY BBB_SWITCH_B.UPDATE_RECORDS_PKG
AS

-- @Description: Procedure to generate Partial feed files. 
-- @Usage: set serveroutput on;
--         execute UPDATE_RECORDS_PKG.update_records_bbb_switch_b ();

 
PROCEDURE update_records_bbb_switch_b IS

	TYPE sku_attr_reln_id IS TABLE OF BBB_ATTR_RELN.sku_attr_reln_id%TYPE
		INDEX BY PLS_INTEGER;
	TYPE site_id IS TABLE OF BBB_ATTR_RELN_SITES.site_id%TYPE
		INDEX BY PLS_INTEGER;

	start_time NUMBER;
	elapsed_time NUMBER;
	updated_records NUMBER;
	total_failed_records NUMBER;
	nCount NUMBER;
	
	sku_attr_reln_ids sku_attr_reln_id;
	site_ids site_id;

	--Cursor for BBB_SWITCH_B Start
	cursor My_Data_Cur Is
	select sku_attr_reln_id, site_id from BBB_SWITCH_B.bbb_attr_reln_sites;
	--Cursor for BBB_SWITCH_B end
	BEGIN
	start_time := dbms_utility.get_cpu_time();
	updated_records :=0;
	total_failed_records:=0;
	
	SELECT count(*) into nCount FROM all_tables where table_name ='MIGRATION_ERROR';
	IF(nCount > 0)THEN
		EXECUTE immediate 'truncate table BBB_SWITCH_B.MIGRATION_ERROR ';   
	END IF;
	
	SELECT count(*) into nCount FROM all_tables where table_name ='MIGRATION_RECORD_COUNT';
	IF(nCount > 0)THEN
		EXECUTE immediate 'truncate table BBB_SWITCH_B.MIGRATION_RECORD_COUNT ';   
	END IF;
	
	open My_Data_Cur;
	LOOP

  -- Read the first group of records
	  fetch My_Data_Cur
	  bulk collect into sku_attr_reln_ids,site_ids
	  limit 20000;

	  -- Exit when there are no more records to process
	  Exit when sku_attr_reln_ids.count = 0;

	  -- Loop through the records in the group
		for idx in 1 .. sku_attr_reln_ids.count
		loop
			IF site_ids(idx)='BedBathUS' THEN
				update BBB_SWITCH_B.BBB_ATTR_RELN set BAB_Flag=1 where sku_attr_reln_id = sku_attr_reln_ids(idx);
        
			ELSIF site_ids(idx)='BuyBuyBaby' THEN
				update BBB_SWITCH_B.BBB_ATTR_RELN set BBB_Flag=1 where sku_attr_reln_id = sku_attr_reln_ids(idx);
        
			ELSIF site_ids(idx)='BedBathCanada' THEN
				update BBB_SWITCH_B.BBB_ATTR_RELN set CA_Flag=1 where sku_attr_reln_id = sku_attr_reln_ids(idx);
			ELSIF site_ids(idx)='GS_BedBathCanada' THEN
				update BBB_SWITCH_B.BBB_ATTR_RELN set GS_CA_FLAG=1 where sku_attr_reln_id = sku_attr_reln_ids(idx);
			ELSIF site_ids(idx)='GS_BuyBuyBaby' THEN
				update BBB_SWITCH_B.BBB_ATTR_RELN set GS_BBB_FLAG=1 where sku_attr_reln_id = sku_attr_reln_ids(idx);
			ELSIF site_ids(idx)='GS_BedBathUS' THEN
				update BBB_SWITCH_B.BBB_ATTR_RELN set GS_BAB_FLAG=1 where sku_attr_reln_id = sku_attr_reln_ids(idx);
			ELSE
				insert into BBB_SWITCH_B.MIGRATION_ERROR values (sku_attr_reln_ids(idx),site_ids(idx));
				updated_records := updated_records-1;
				total_failed_records := total_failed_records+1;
			END IF;
			updated_records := updated_records+1;
		end loop;
		commit;
		EXIT WHEN My_Data_Cur%NOTFOUND;
	insert into BBB_SWITCH_B.MIGRATION_RECORD_COUNT values (updated_records,total_failed_records);
	commit;		
  
END LOOP;
CLOSE My_Data_Cur;
elapsed_time := (dbms_utility.get_cpu_time() - start_time) / 100;
dbms_output.put_line('elapsed time: ' || elapsed_time);

EXCEPTION
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('update_records_bbb_switch_b: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
END update_records_bbb_switch_b;

    
End UPDATE_RECORDS_PKG;

/
SHOW ERROR;
COMMIT;

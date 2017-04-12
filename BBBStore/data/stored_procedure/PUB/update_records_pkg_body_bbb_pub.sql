
SET ECHO ON;
SET DEFINE OFF;
SET SERVEROUTPUT ON;


--------------------------------------------------------
--  DDL for Package Body UPDATE_RECORDS_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY BBB_PUB.UPDATE_RECORDS_PKG
AS

-- @Description: Procedure to migrate data. 
-- @Usage: set serveroutput on;
--         execute UPDATE_RECORDS_PKG.update_records_bbb_pub();

 
PROCEDURE update_records_bbb_pub is

TYPE sku_attr_reln_id IS TABLE OF BBB_PUB.BBB_ATTR_RELN.sku_attr_reln_id%TYPE
          INDEX BY PLS_INTEGER;
TYPE asset_version IS TABLE OF BBB_PUB.BBB_ATTR_RELN.asset_version%TYPE
          INDEX BY PLS_INTEGER;
TYPE site_id IS TABLE OF BBB_PUB.bbb_attr_reln_sites.site_id%TYPE
          INDEX BY PLS_INTEGER;
start_time NUMBER;
elapsed_time NUMBER;
updated_records NUMBER;
total_failed_records NUMBER;
nCount NUMBER;

sku_attr_reln_ids   sku_attr_reln_id;
asset_versions asset_version;
site_ids site_id;


--Cursor for BBB_Pub Start
cursor My_Data_Cur Is
select sku_attr_reln_id,asset_version,site_id from(
SELECT 
  reln.sku_attr_reln_id,reln.asset_version,sites1.site_id
FROM BBB_PUB.BBB_ATTR_RELN reln
  INNER JOIN bbb_attr_reln_sites sites1 ON (sites1.SKU_ATTR_RELN_ID = reln.SKU_ATTR_RELN_ID 
  and sites1.ASSET_VERSION = reln.ASSET_VERSION));

--Cursor for BBB_Pub end

  BEGIN
	start_time := dbms_utility.get_cpu_time();
  	updated_records :=0;
	total_failed_records:=0;
	
	SELECT count(*) into nCount FROM all_tables where table_name ='MIGRATION_ERROR';
	IF(nCount > 0)THEN
		EXECUTE immediate 'truncate table BBB_PUB.MIGRATION_ERROR ';   
	END IF;
	
	SELECT count(*) into nCount FROM all_tables where table_name ='MIGRATION_RECORD_COUNT';
	IF(nCount > 0)THEN
		EXECUTE immediate 'truncate table BBB_PUB.MIGRATION_RECORD_COUNT ';   
	END IF;
  
  open My_Data_Cur;
  
  loop

  -- Read the first group of records
  fetch My_Data_Cur
  bulk collect into sku_attr_reln_ids,asset_versions,site_ids
  limit 20000;

  -- Exit when there are no more records to process
  Exit when sku_attr_reln_ids.count = 0;

  -- Loop through the records in the group
  for idx in 1 .. sku_attr_reln_ids.count
  loop
	IF site_ids(idx)='BedBathUS' THEN
		merge into BBB_PUB.BBB_ATTR_RELN t using (SELECT sku_attr_reln_ids(idx) AS sku_attr_reln_id
			,asset_versions(idx) AS asset_version
			FROM   dual) t1 ON (t.sku_attr_reln_id = t1.sku_attr_reln_id and t.asset_version = t1.asset_version)
			when Matched Then Update Set t.BAB_Flag=1;
	ELSIF site_ids(idx)='BuyBuyBaby' THEN
		merge into BBB_PUB.BBB_ATTR_RELN t using (SELECT sku_attr_reln_ids(idx) AS sku_attr_reln_id
			,asset_versions(idx) AS asset_version
			FROM   dual) t1 ON (t.sku_attr_reln_id = t1.sku_attr_reln_id and t.asset_version = t1.asset_version)
			when Matched Then Update Set t.BBB_Flag=1;
	ELSIF site_ids(idx)='BedBathCanada' THEN
		merge into BBB_PUB.BBB_ATTR_RELN t using (SELECT sku_attr_reln_ids(idx) AS sku_attr_reln_id
			,asset_versions(idx) AS asset_version
			FROM   dual) t1 ON (t.sku_attr_reln_id = t1.sku_attr_reln_id and t.asset_version = t1.asset_version)
			when Matched Then Update Set t.CA_Flag=1;
	ELSIF site_ids(idx)='GS_BedBathUS' THEN
		merge into BBB_PUB.BBB_ATTR_RELN t using (SELECT sku_attr_reln_ids(idx) AS sku_attr_reln_id
			,asset_versions(idx) AS asset_version
			FROM   dual) t1 ON (t.sku_attr_reln_id = t1.sku_attr_reln_id and t.asset_version = t1.asset_version)
			when Matched Then Update Set t.GS_BAB_Flag=1;
	ELSIF site_ids(idx)='GS_BuyBuyBaby' THEN
		merge into BBB_PUB.BBB_ATTR_RELN t using (SELECT sku_attr_reln_ids(idx) AS sku_attr_reln_id
			,asset_versions(idx) AS asset_version
			FROM   dual) t1 ON (t.sku_attr_reln_id = t1.sku_attr_reln_id and t.asset_version = t1.asset_version)
			when Matched Then Update Set t.GS_BBB_Flag=1;
	ELSIF site_ids(idx)='GS_BedBathCanada' THEN
		merge into BBB_PUB.BBB_ATTR_RELN t using (SELECT sku_attr_reln_ids(idx) AS sku_attr_reln_id
			,asset_versions(idx) AS asset_version
			FROM   dual) t1 ON (t.sku_attr_reln_id = t1.sku_attr_reln_id and t.asset_version = t1.asset_version)
			when Matched Then Update Set t.GS_CA_Flag=1;
	ELSE
		insert into BBB_PUB.MIGRATION_ERROR values (sku_attr_reln_ids(idx),site_ids(idx));
		updated_records := updated_records-1;
		total_failed_records := total_failed_records+1;
	END IF;
	updated_records := updated_records+1;
  end loop;
  commit;
	EXIT WHEN My_Data_Cur%NOTFOUND;
	insert into BBB_PUB.MIGRATION_RECORD_COUNT values (updated_records,total_failed_records);
	commit;		
end loop;
close My_Data_Cur;
elapsed_time := (dbms_utility.get_cpu_time() - start_time) / 100;
dbms_output.put_line('elapsed time: ' || elapsed_time);
EXCEPTION
  WHEN OTHERS THEN -- handles all other errors
    dbms_output.put_line('update_records_bbb_pub: Error! :'||SQLCODE || ' ' || SUBSTR(SQLERRM, 1, 64));
end update_records_bbb_pub;

    
End UPDATE_RECORDS_PKG;

/
SHOW ERROR;
COMMIT;

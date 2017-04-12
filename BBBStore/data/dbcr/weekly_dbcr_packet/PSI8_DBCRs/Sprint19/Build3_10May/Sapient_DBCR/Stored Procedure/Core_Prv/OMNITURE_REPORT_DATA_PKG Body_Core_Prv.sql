-- Build Version : PSI8 Release Sprint19 Build3
SET ECHO ON;
set DEFINE OFF;
set SERVEROUTPUT ON;

--------------------------------------------------------
--  File created - Friday-May-06-2016   
--  DDL for Package Body OMNITURE_REPORT_DATA_PKG
--------------------------------------------------------

create or replace
PACKAGE BODY             BBB_CORE_PRV.OMNITURE_REPORT_DATA_PKG 
AS

PROCEDURE ARCHIVE_OMNITURE_DATA(
    p_report_id IN VARCHAR,
    p_concept   IN VARCHAR,
    p_records_moved OUT NUMBER)
IS
  Invalid_Concept EXCEPTION;
  batchSize       CONSTANT NUMBER := 5000;
  v_record_moved  NUMBER          := 0;
  deleteLoopCount NUMBER;
  totalCount      NUMBER;
  
  CURSOR cur_omniture_report_data
  IS
    SELECT R.ID, R.REPORT_ID, R.REPORT_SUITE, R.PERIOD, R.CONCEPT, R.BOOST_SCORE, R.PRODUCT_ID, R.KEYWORD
    FROM BBB_OMNITURE_REPORT_DATA R
    WHERE R.REPORT_ID <> p_report_id
    AND R.CONCEPT      = p_concept;
  cur_rec cur_omniture_report_data%ROWTYPE;

BEGIN

  SELECT COUNT(*)
  INTO totalCount
  FROM BBB_OMNITURE_REPORT_DATA omniture_tbl
  WHERE omniture_tbl.REPORT_ID <> p_report_id
  AND omniture_tbl.CONCEPT      = p_concept;
  dbms_output.put_line('totalCount=' || totalCount);
  
  --Check if the concept passed as input is a valid one.
  IF p_concept <> 'BEDBATHUS' AND p_concept <> 'BUYBUYBABY' AND p_concept <> 'BEDBATHCA' THEN
    RAISE Invalid_Concept;
  END IF;

  -- If the cursonr is not already opened, then open the cursor
  IF NOT cur_omniture_report_data%ISOPEN THEN
    OPEN cur_omniture_report_data;
  END IF;

  LOOP
    FETCH cur_omniture_report_data INTO cur_rec;
    EXIT
  WHEN cur_omniture_report_data%NOTFOUND;
    INSERT
    INTO OMNITURE_ARCHIVED_DATA(
        ID, REPORT_ID, REPORT_SUITE, PERIOD, CONCEPT, BOOST_SCORE, PRODUCT_ID, KEYWORD
      )VALUES(
        cur_rec.ID, cur_rec.REPORT_ID, cur_rec.REPORT_SUITE, cur_rec.PERIOD, cur_rec.CONCEPT, cur_rec.BOOST_SCORE, cur_rec.PRODUCT_ID, cur_rec.KEYWORD
      );
    v_record_moved                   := v_record_moved + 1;
    
    IF MOD(v_record_moved, batchSize) = 0 THEN
      dbms_output.put_line('committing insertions in OMNITURE_ARCHIVED_DATA table after ' || v_record_moved || ' records ');
      COMMIT;
    END IF;
  
  END LOOP;
  -- to commit the changes in the last iteration because MOD would ignore last set of records
  COMMIT;

  IF (totalCount     = v_record_moved) THEN
    p_records_moved := 1;
  ELSE
    p_records_moved := 0;
  END IF;  
  CLOSE cur_omniture_report_data;
  
EXCEPTION
WHEN Invalid_Concept THEN
  RAISE_APPLICATION_ERROR( -20501, 'Invalid value passed as concept :: ' || p_concept );
WHEN OTHERS THEN
  RAISE;
END ARCHIVE_OMNITURE_DATA;

PROCEDURE PURGE_OMNITURE_DATA(
    p_report_id IN VARCHAR,
    p_concept   IN VARCHAR,
    p_records_moved OUT NUMBER)
IS

  Invalid_Concept EXCEPTION;
  batchSize       CONSTANT NUMBER := 1000;
  deleteLoopCount NUMBER;
  totalCount      NUMBER;
  
BEGIN
  --Check if the concept passed as input is a valid one.
  IF p_concept <> 'BEDBATHUS' AND p_concept <> 'BUYBUYBABY' AND p_concept <> 'BEDBATHCA' THEN
    RAISE Invalid_Concept;
  END IF;

  dbms_output.put_line('Now deleting data' );
  
  -- Once older records have been copied into OMNITURE_ARCHIVED_DATA, delete those records from BBB_OMNITURE_REPORT_DATA
  --Get the total count of records to be deleted
  SELECT COUNT(*)
  INTO totalCount
  FROM BBB_OMNITURE_REPORT_DATA omniture_tbl
  WHERE omniture_tbl.concept  = p_concept
  AND omniture_tbl.report_id != p_report_id;
  dbms_output.put_line('totalCount=' || totalCount);
  
  -- idenitfy the number of iterations in which the records will be deleted
  deleteLoopCount := ROUND ((totalCount/ batchSize), 0);
  deleteLoopCount := deleteLoopCount   + 1;
  dbms_output.put_line('deleteLoopCount=' || deleteLoopCount || ' and totalCount=' || totalCount);
  
  --delete data in chunks
  IF deleteLoopCount > 0 THEN
    FOR i IN 1..deleteLoopCount
    LOOP
      dbms_output.put_line('iterating delete statement '|| i);
      DELETE
      FROM BBB_OMNITURE_REPORT_DATA omniture_tbl
      WHERE omniture_tbl.concept  = p_concept
      AND omniture_tbl.report_id !=p_report_id
      AND ROWNUM                 <= batchSize;
      --COMMIT;
      p_records_moved :=  p_records_moved + SQL%ROWCOUNT;
      dbms_output.put_line('total records deleted: '|| p_records_moved || ' and records deleted in current iteration =' || SQL%ROWCOUNT);
      
      EXIT
    WHEN SQL%ROWCOUNT = 0;
    --COMMIT;
    END LOOP;
    --p_records_moved :=  totalCount;
  END IF;
EXCEPTION
WHEN Invalid_Concept THEN
  RAISE_APPLICATION_ERROR( -20501, 'Invalid value passed as concept :: ' || p_concept );
WHEN OTHERS THEN
  RAISE;
END PURGE_OMNITURE_DATA;
  
END OMNITURE_REPORT_DATA_PKG;
/
SHOW ERROR;
COMMIT;